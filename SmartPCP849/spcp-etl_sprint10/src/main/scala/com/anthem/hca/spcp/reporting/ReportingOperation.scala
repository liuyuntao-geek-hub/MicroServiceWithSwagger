package com.anthem.hca.spcp.provider.reporting

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.when
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPConstants
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.spark.sql.functions.current_date

import scalaj.http.Http
import scalaj.http.HttpResponse
import javax.jdo.annotations.Join
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import com.anthem.hca.spcp.config.ConfigKey
import org.apache.spark.sql.types.DateType

/**
 * This class is meant to perform the PIMS VBP query.
 * It is meant for populating the PCP_VBP table with all the
 * two necessary columns, PROV_ORG_TAX_ID and IP_NPI,
 * which is then used to derive the VBP flag for the provider.
 *
 * The source consists of 6 PIMS tables,
 * and 1 lookup tables.
 *
 *
 */

class ReportingOperation(configPath: String, env: String, queryFileCategory: String)
    extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var start = ""
  var listBuffer = ListBuffer[Audit]()

  /*
   * This method is meant to load the data from splice tables to Spark Dataframes 
   * based on the queries in the properties file.
   * 
   * @param   None
   * @return  Map[String, DataFrame]
   */

  override def extractData(): Map[String, DataFrame] = {

    //Reading the data into Data frames

    val startTime = DateTime.now
    info(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")

    //Reading the queries from config file
    info("Reading the queries from config file")

    val mbrPcpQuery = config.getString("query_mbr_pcp").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val cmpnyCfQuery = config.getString("query_cmpny_cf").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val mpeCoaQuery = config.getString("query_mpe_coa").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val mpeQuery = config.getString("query_mpe").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val mbrQuery = config.getString("query_mbr_reprtng").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)

    /*
     * query_mbr_pcp="select * from <dbName>.mbr_pcp where rcrd_stts_cd <> 'DEL' and mbrshp_sor_cd in ('808','815')"
     * query_cmpny_cf="select * from <dbName>.cmpny_cf"
     * query_mpe_coa="select * from <dbName>.mbr_prod_enrlmnt_coa where rcrd_stts_cd <> 'DEL' and mbrshp_sor_cd in ('808','815')"
     * query_mpe="select * from <dbName>.mbr_prod_enrlmnt where rcrd_stts_cd <> 'DEL'"
     * query_mbr_reprtng="select * from <dbName>.mbr where rcrd_stts_cd <> 'DEL'"
     */

    info(s"[SPCP-ETL] Query for reading data from MBR_PCP table is: $mbrPcpQuery")
    info(s"[SPCP-ETL] Query for reading data from CMPNY_CF table is: $cmpnyCfQuery")
    info(s"[SPCP-ETL] Query for reading data from MPE_COA table is: $mpeCoaQuery")
    info(s"[SPCP-ETL] Query for reading data from MPE table is: $mpeQuery")
    info(s"[SPCP-ETL] Query for reading data from MBR table is: $mbrQuery")

    val mbrPcpDF = splicemachineContext.internalDf(mbrPcpQuery)
    val cmpnyCfDF = splicemachineContext.internalDf(cmpnyCfQuery)
    val mpeCoaDF = splicemachineContext.internalDf(mpeCoaQuery)
    val mpeDF = splicemachineContext.internalDf(mpeQuery)
    val mbrDF = splicemachineContext.internalDf(mbrQuery)

    val mapDF = Map("mbrPcp" -> mbrPcpDF, "cmpnyCf" -> cmpnyCfDF,
      "mpeCoa" -> mpeCoaDF, "mpe" -> mpeDF, "mbr" -> mbrDF)

    info(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }

  /*
   * This method takes care of all the transformation and derivation logic
   * 
   * @param   Map[String, DataFrame]
   * @return  Map[String, DataFrame]
   * 
   */

  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Processing Data Started: $startTime")

    val mbrPcpDF = inDFs.getOrElse("mbrPcp", null)
    val cmpnyCfDF = inDFs.getOrElse("cmpnyCf", null)
    val mpeCoaDF = inDFs.getOrElse("mpeCoa", null)
    val mpeDF = inDFs.getOrElse("mpe", null)
    val mbrDF = inDFs.getOrElse("mbr", null)

    //Step1: Left join between mbrPcpDF and mbrDF
    val mbrPcp_mbr_joinDF = mbrPcpDF.join(mbrDF,
      Seq("mbr_key", "mbrshp_sor_Cd"), "left")

    println("Step1: Left join between mbrPcpDF and mbrDF Done. Count is " + mbrPcp_mbr_joinDF.count())

    //Step2: Left join between mbrPcp_mbr_joinDF and mpeCoaDF
    val mbrPcp_mbr_mpeCoa_joinDF = mbrPcp_mbr_joinDF.join(mpeCoaDF,
      mbrPcpDF("mbr_key") === mpeCoaDF("mbr_key") &&
        mbrPcpDF("mbr_prod_enrlmnt_efctv_dt") === mpeCoaDF("mbr_prod_enrlmnt_efctv_dt") &&
        mbrPcpDF("prod_ofrg_key") === mpeCoaDF("prod_ofrg_key"), "left")

    println("Step2: Left join between mbrPcp_mbr_joinDF and mpeCoaDF Done. Count is " + mbrPcp_mbr_mpeCoa_joinDF.count())

    //Step3: Left join between mbrPcp_mbr_mpeCoa_joinDF and cmpnyCfDF
    val mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF = mbrPcp_mbr_mpeCoa_joinDF.join(cmpnyCfDF,
      cmpnyCfDF("cmpny_cf_cd") === mpeCoaDF("cmpny_cf_cd"), "left")

    println("Step3: Left join between mbrPcp_mbr_mpeCoa_joinDF and cmpnyCfDF Done. Count is " + mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF.count())

    //Step4: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF and mpeDF
    val mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF = mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF.join(mpeDF,
      mpeDF("mbr_key") === mbrPcpDF("mbr_key") &&
        mpeDF("prod_ofrg_key") === mbrPcpDF("prod_ofrg_key") &&
        mpeDF("mbrshp_sor_cd") === mbrPcpDF("mbrshp_sor_cd") &&
        mpeDF("mbr_prod_enrlmnt_efctv_dt") === mbrPcpDF("mbr_prod_enrlmnt_efctv_dt"), "left")

    println("Step4: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF and mpeDF Done." +
      " Final Dataframe count is: " + mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF.count())
      
      //Step5: Deriving the required columns from  mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF
      val finalDF = mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF.select(mbrPcpDF("mbr_key"),
          mpeDF("hc_id"), mbrDF("mbr_sqnc_nbr"), mbrPcpDF("mbr_prod_enrlmnt_efctv_dt"), 
          mbrDF("frst_nm"), mbrDF("last_nm"), mbrPcpDF("sor_dtm").cast(DateType), 
          mbrPcpDF("src_pcp_asgnmnt_mthd_cd"), 
          /*assignment type derivation*/
          when(mbrPcpDF("mbr_prod_enrlmnt_efctv_dt") === mbrPcpDF("pcp_efctv_dt"), lit("NEW"))
          .otherwise(when($"src_pcp_asgnmnt_mthd_cd" isin ("RC1A",	"RC2A",	"RC3A",	"RC4A",	"RC5A",	
              "RC6A",	"RC14",	"RC15",	"RC24",	"RC25",	"RC84",	"RC85",	"RC40",	"RC42"), lit("Affinity PCP"))),
            //  .otherwise(when)),
          cmpnyCfDF("cmpny_st_cd"), lit("").alias("prod_fmly_type_cd"), 
          /*mbu_cf_cd derivation*/
          when(substring($"mbu_cf_cd", 1, 2) equalTo "IN", lit("INDIVIDUAL"))
          .otherwise(when(substring($"mbu_cf_cd", 1, 2) equalTo "SG", lit("SMALL GROUP")
              .otherwise(lit("NA")))),
          mbrPcpDF("pcp_efctv_dt"), lit("").alias("tax_id"), lit("").alias("tax_id_nm"),
          lit("").alias("pcp_id"), lit("").alias("pcp_id_nm"), current_timestamp().alias("created_dtm"), 
          lit("manual_load").alias("created_by"), current_timestamp().alias("last_updtd_dtm"),
          lit("manual_load").alias("last_updtd_by"))
      
      println("Deriving the required columns from  mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF. Count is " + finalDF.count())

    mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF.printSchema()
    mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF.show(1000)

    val finalPcpDFMap = Map("reportingTargetTbl" -> mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF)

    finalPcpDFMap
  }

  /*
   * This method truncates the target table and reloads it with the new data.
   * 
   * @param   Map[String, DataFrame]
   * @return  None
   */

  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Loading Data Started: $startTime")

    val reportingTargetTable = config.getString("reportingTargetTable")
    val targetTableReportingDF = outDFs.getOrElse("reportingTargetTbl", null)

    splicemachineContext.execute(s"delete from $spcpSpliceDB.$reportingTargetTable")
    info(s"$spcpSpliceDB.$reportingTargetTable has been truncated")

    splicemachineContext.insert(targetTableReportingDF, spcpSpliceDB + """.""" + reportingTargetTable)
    info("REPORTING RECORDS INSERTED")
  }

  @Override
  def beforeExtractData() {
    program = sc.appName
    user_id = sc.sparkUser
    app_id = sc.applicationId

    start_time = DateTime.now()

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), "0min", "Started")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, lit(current_timestamp()))

    val spcpAuditDFWithUppercaseCol = SPCPDataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    //using upsert to avoid insert failure while job retry , also trying to limit job retries to 1
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDB + "." + spcpAuditTable)

  }

  @Override
  def afterLoadData() {

    var listBuffer = ListBuffer[Audit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), duration, "completed")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, current_timestamp())
    val spcpAuditDFWithUppercaseCol = SPCPDataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDB + "." + spcpAuditTable)

  }

}






