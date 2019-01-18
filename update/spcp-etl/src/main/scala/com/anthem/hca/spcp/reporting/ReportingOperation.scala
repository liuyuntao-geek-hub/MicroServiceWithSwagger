package com.anthem.hca.spcp.reporting

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.when
import org.apache.spark.sql.types.DateType
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.config.ConfigKey
import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import org.apache.spark.storage.StorageLevel
import com.anthem.hca.spcp.util.SpcpMailUtils

/**
 * This class is meant to perform the Report query.
 *
 * The source consists of  tables,
 * and  lookup tables.
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

  //reading the reporting target table name from config file
  val reportingTargetTable = config.getString("reportingTargetTable")

  //reading the sendNtfctnMail value from the config file
  val sendNtfctnMail = config.getBoolean("sendNtfctnMail")

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

    //Driver table
    val mbrPcpQuery = config.getString("query_mbr_pcp").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    //member lookup table
    val mbrQuery = config.getString("query_mbr_reprtng").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val mpeCoaQuery = config.getString("query_mpe_coa").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val cmpnyCfQuery = config.getString("query_cmpny_cf").replace(ConfigKey.cdlRfdmAllphiDBPlaceHolder, cdlRfdmAllphiDB)
    val mpeQuery = config.getString("query_mpe").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val hcaRankingQuery = config.getString("query_hca_prov_ranking").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val providerQuery = config.getString("query_provider").replace(ConfigKey.cdlWgspAllphiDBPlaceHolder, cdlWgspAllphiDB)
    val rcodeMappingQuery = config.getString("rcode_mapping_query").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val prodHrzntlHrchyQuery = config.getString("prod_hrzntl_hrchy_query").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val mbuHrzntlHrchyQuery = config.getString("mbu_hrzntl_hrchy_query").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)

    info(s"[SPCP-ETL] Query for reading data from MBR_PCP table is: $mbrPcpQuery")
    info(s"[SPCP-ETL] Query for reading data from CMPNY_CF table is: $cmpnyCfQuery")
    info(s"[SPCP-ETL] Query for reading data from MPE_COA table is: $mpeCoaQuery")
    info(s"[SPCP-ETL] Query for reading data from MPE table is: $mpeQuery")
    info(s"[SPCP-ETL] Query for reading data from MBR table is: $mbrQuery")
    info(s"[SPCP-ETL] Query for reading data from HCA_RANKING table is: $hcaRankingQuery")
    info(s"[SPCP-ETL] Query for reading data from PROVIDER_INFO table is: $providerQuery")
    info(s"[SPCP-ETL] Query for reading data from PROD_HRZNTL_HRCHY table is: $prodHrzntlHrchyQuery")
    info(s"[SPCP-ETL] Query for reading data from MBU_HRZNTL_HRCHY table is: $mbuHrzntlHrchyQuery")
    info(s"[SPCP-ETL] Query for reading data from RCODE_MAPPING table is: $rcodeMappingQuery")

    val mbrPcpDF = splicemachineContext.internalDf(mbrPcpQuery)
    val cmpnyCfDF = splicemachineContext.internalDf(cmpnyCfQuery)
    val mpeCoaDF = splicemachineContext.internalDf(mpeCoaQuery)
    val mpeDF = splicemachineContext.internalDf(mpeQuery)
    val mbrDF = splicemachineContext.internalDf(mbrQuery)
    val hcaRankingDF = splicemachineContext.internalDf(hcaRankingQuery)
    val providerDF = splicemachineContext.internalDf(providerQuery)
    val prodHrzntlHrchyDF = splicemachineContext.internalDf(prodHrzntlHrchyQuery)
    val mbuHrzntlHrchyDF = splicemachineContext.internalDf(mbuHrzntlHrchyQuery)
    val rcodeMappingDF = splicemachineContext.internalDf(rcodeMappingQuery)

    val mapDF = Map("mbrPcp" -> mbrPcpDF, "cmpnyCf" -> cmpnyCfDF, "rcodeMapping" -> rcodeMappingDF,
      "mpeCoa" -> mpeCoaDF, "mpe" -> mpeDF, "mbr" -> mbrDF, "hcaRanking" -> hcaRankingDF,
      "provider" -> providerDF, "prodHrzntlHrchy" -> prodHrzntlHrchyDF, "mbuHrzntlHrchy" -> mbuHrzntlHrchyDF)

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
    val mpeCoaDFRaw = inDFs.getOrElse("mpeCoa", null)
    val mpeDF = inDFs.getOrElse("mpe", null)
    val mbrDF = inDFs.getOrElse("mbr", null)
    val hcaRankingDF = inDFs.getOrElse("hcaRanking", null)
    val providerDF = inDFs.getOrElse("provider", null)
    val prodHrzntlHrchyDF = inDFs.getOrElse("prodHrzntlHrchy", null)
    val mbuHrzntlHrchyDF = inDFs.getOrElse("mbuHrzntlHrchy", null)
    val pcpAsgnmntRcodeMappingDF = inDFs.getOrElse("rcodeMapping", null)
    
     //Step1: Left join between mbrPcpDF and pcpAsgnmntRcodeMappingDF
    val mbrPcp_rcodeMapping_joinDF = mbrPcpDF.join(
      pcpAsgnmntRcodeMappingDF,
      mbrPcpDF("src_pcp_asgnmnt_mthd_cd")=== pcpAsgnmntRcodeMappingDF("rcode"), "inner")
    info("Step1: Inner join between mbrPcpDF and pcpAsgnmntRcodeMappingDF done.")
    
     val mpeCoaDF =  mpeCoaDFRaw.join(
      mbuHrzntlHrchyDF, Seq("mbu_cf_cd"))

    //Step2: Left join between mbrPcpDF and mbrDF
    val mbrPcp_mbr_joinDF = mbrPcp_rcodeMapping_joinDF.join(mbrDF, Seq("mbr_key", "mbrshp_sor_Cd"), "left")
    info("Step2: Left join between mbrPcpDF and mbrDF Done.")

    //Step3: Left join between mbrPcp_mbr_joinDF and mpeCoaDF
    val mbrPcp_mbr_mpeCoa_joinDF = mbrPcp_mbr_joinDF.join(
      mpeCoaDF,
      mbrPcpDF("mbr_key") === mpeCoaDF("mbr_key") &&
        mbrPcpDF("mbr_prod_enrlmnt_efctv_dt") === mpeCoaDF("mbr_prod_enrlmnt_efctv_dt") &&
        mbrPcpDF("prod_ofrg_key") === mpeCoaDF("prod_ofrg_key"), "left")
    info("Step3: Left join between mbrPcp_mbr_joinDF and mpeCoaDF Done.")

    //Step4: Left join between mbrPcp_mbr_mpeCoa_joinDF and cmpnyCfDF
    val mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF = mbrPcp_mbr_mpeCoa_joinDF.join(
      cmpnyCfDF,
      cmpnyCfDF("cmpny_cf_cd") === mpeCoaDF("cmpny_cf_cd"), "left")
    info("Step4: Left join between mbrPcp_mbr_mpeCoa_joinDF and cmpnyCfDF Done.")

    //Step5: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF and mpeDF
    val mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF = mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF.join(
      mpeDF,
      mpeDF("mbr_key") === mbrPcpDF("mbr_key") &&
        mpeDF("prod_ofrg_key") === mbrPcpDF("prod_ofrg_key") &&
        mpeDF("mbrshp_sor_cd") === mbrPcpDF("mbrshp_sor_cd") &&
        mpeDF("mbr_prod_enrlmnt_efctv_dt") === mbrPcpDF("mbr_prod_enrlmnt_efctv_dt"), "left")
    info("Step5: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_joinDF and mpeDF done.")

    //Step6: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF and providerInfoDF
    val mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_provInfo_joinDF = mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF.join(
      providerDF,
      mbrPcpDF("pcp_id") === providerDF("prov_pcp_id"), "left")
    info("Step6: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF and providerInfoDF Done.")

    //Step7: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_provInfo_joinDF and hcaRankingDF
    val mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_provInfo_hcaRanking_joinDF = mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_provInfo_joinDF.join(
      hcaRankingDF,
      providerDF("tax_id") === hcaRankingDF("tax_id") &&
        cmpnyCfDF("cmpny_st_cd") === hcaRankingDF("adrs_st_cd"), "left")
    info("Step7: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_hcaRanking_joinDF and hcaRankingDF Done.")

    //Step8: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_provInfo_hcaRanking_joinDF and prodHrzntlHrchyDF
    val rprtngDF = mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_provInfo_hcaRanking_joinDF.join(
      prodHrzntlHrchyDF, mpeCoaDF("prod_cf_cd") === prodHrzntlHrchyDF("prod_cf_cd"), "left")
    info("Step8: Left join between between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_provInfo_hcaRanking_joinDF and prodHrzntlHrchyDF Done.")

    //Step9: Deriving the required columns from  mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_hcaRanking_provInfo_prodOfrg_prodPlan_joinDF
    val finalReportingDF = rprtngDF
      .select(lit(1).as("rpt_id"),
        mbrPcpDF("mbr_key"),
        mpeDF("hc_id"),
        mbrDF("mbr_sqnc_nbr"),
        mbrPcpDF("mbr_prod_enrlmnt_efctv_dt"),
        mbrDF("frst_nm"),
        mbrDF("last_nm"),
        mbrPcpDF("sor_dtm").cast(DateType).as("assignment_dt"),
        mbrPcpDF("src_pcp_asgnmnt_mthd_cd"),
        when(mbrPcpDF("src_pcp_asgnmnt_mthd_cd") === pcpAsgnmntRcodeMappingDF("rcode"), pcpAsgnmntRcodeMappingDF("assignment_type"))
          otherwise (lit("NA")) as ("assignment_type"),
        cmpnyCfDF("cmpny_st_cd"),
        prodHrzntlHrchyDF("prod_fmly_type_cd"),
        mpeCoaDF("mbu_cf_cd"),
        mbrPcpDF("pcp_efctv_dt"),
        providerDF("tax_id"),
        hcaRankingDF("tax_id_nm"),
        mbrPcpDF("pcp_id"),
        providerDF("pcp_frst_nm"),
        providerDF("pcp_mid_nm"),
        providerDF("pcp_last_nm"),
        hcaRankingDF("pcp_mdo_rank"),
        mpeDF("prod_ofrg_key"),
        current_timestamp().alias("created_dtm"),
        lit(user_id).alias("created_by"),
        current_timestamp().alias("last_updtd_dtm"),
        lit(user_id).alias("last_updtd_by")).persist(StorageLevel.MEMORY_AND_DISK)

    info("Step9: Deriving the required columns from  rprtngDF done. Final datframe generated!")

    finalReportingDF.printSchema()
    var finalPcpDFMap: Map[String, DataFrame] = null

    info("End of processing")

    if (sendNtfctnMail) {
      val minRowCnt = config.getInt("reportingDataMinCnt")
      val maxRowCnt = config.getInt("reportingDataMaxCnt")
      val finalDfCnt = finalReportingDF.count()
      info(s"[SPCP-ETL] Final Report Table Count is : " + finalDfCnt)
      if ((finalDfCnt < minRowCnt) || (finalDfCnt > maxRowCnt)) {
        finalPcpDFMap = Map("reportingTargetTbl" -> null)
        info("Member data not within threshold limits")
        val mailDF = spark.sparkContext.parallelize(List((reportingTargetTable, minRowCnt, maxRowCnt, finalDfCnt)))
          .toDF("Table_Nm", "Min_Cnt", "Max_Cnt", "Current_Record_Cnt")
        mailDF.show()
        SpcpMailUtils.sendMail(config, mailDF)
        throw new Exception("[SPCP-ETL] : RPT_MBR_PCP_DTLS Data Load Suspended. Row count beyond threshold.")
      } else {
        finalPcpDFMap = Map("reportingTargetTbl" -> finalReportingDF.distinct())
      }
    } else {
      finalPcpDFMap = Map("reportingTargetTbl" -> finalReportingDF.distinct())
    }

    info(s"[SPCP-ETL] processing() Data Completed at: " + DateTime.now())
    info(s"[SPCP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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

    val targetTableReportingDF = outDFs.getOrElse("reportingTargetTbl", null)

    val modTargetDF = SPCPDataFrameUtils.columnsInUpper(targetTableReportingDF)

    info("capital schema is : ")
    modTargetDF.printSchema()

    splicemachineContext.execute(s"delete from $spcpSpliceDB.$reportingTargetTable")
    info(s"$spcpSpliceDB.$reportingTargetTable has been truncated")

    splicemachineContext.insert(modTargetDF, spcpSpliceDB + """.""" + reportingTargetTable)
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






