package com.anthem.hca.spcp.provider.vbp

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._
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
import org.apache.spark.sql.SaveMode

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

class VbpOperation(configPath: String, env: String, queryFileCategory: String)
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

    val piProvHrchyQuery = config.getString("query_pi_prov_hrchy").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val pgmPgQuery = config.getString("query_pgm_pg").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val piPgmQuery = config.getString("query_pi_pgm").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val piPgmLobQuery = config.getString("query_pi_pgm_lob").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val pgmLobPslQuery = config.getString("query_pgm_lob_psl").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val pgmLobPslParmQuery = config.getString("query_pgm_lob_psl_parm").toLowerCase().replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val wgspPcpVbpPrgmMappingQuery = config.getString("query_wgsp_pcp_vbp_prgm_mapping").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)

    val piProvHrchyDF = splicemachineContext.internalDf(piProvHrchyQuery)
    val pgmPgDF = splicemachineContext.internalDf(pgmPgQuery)
    val piPgmDF = splicemachineContext.internalDf(piPgmQuery)
    val piPgmLobDF = splicemachineContext.internalDf(piPgmLobQuery)
    val pgmLobPslDF = splicemachineContext.internalDf(pgmLobPslQuery)
    val pgmLobPslParmDF = splicemachineContext.internalDf(pgmLobPslParmQuery)
    val wgspPcpVbpPrgmMappingDF = splicemachineContext.internalDf(wgspPcpVbpPrgmMappingQuery)

    val mapDF = Map("piProvHrchy" -> piProvHrchyDF, "pgmPg" -> pgmPgDF,
      "piPgm" -> piPgmDF, "piPgmLob" -> piPgmLobDF, "pgmLobPsl" -> pgmLobPslDF,
      "pgmLobPslParm" -> pgmLobPslParmDF, "wgspPcpVbpPrgmMapping" -> wgspPcpVbpPrgmMappingDF)

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

    val piProvHrchyDF = inDFs.getOrElse("piProvHrchy", null)
    val pgmPgDF = inDFs.getOrElse("pgmPg", null)
    val piPgmDF = inDFs.getOrElse("piPgm", null)
    val piPgmLobDF = inDFs.getOrElse("piPgmLob", null)
    val pgmLobPslDF = inDFs.getOrElse("pgmLobPsl", null)
    val pgmLobPslParmDF = inDFs.getOrElse("pgmLobPslParm", null)
    val wgspPcpVbpPrgmMappingDF = inDFs.getOrElse("wgspPcpVbpPrgmMapping", null)

    // Step1 :- Performing the inner join on vbpprojectcodes table and piPgm table
    val vbpProgramCodesJoinPiPgmDF = piPgmDF.join(
      wgspPcpVbpPrgmMappingDF,
      (piPgmDF.col("PGM_TYPE_CD")) === (wgspPcpVbpPrgmMappingDF.col("PRGM_TYP")), "INNER")

    //Step2 :- Joining hrchy and pgmpg table
    val interPimsVbpJoinDF1 = piProvHrchyDF.join(pgmPgDF, Seq("PG_ID"))

    //Step3 :- Joining interPIMSVBPJOINDF1 and pipgm table
    val interPimsVbpJoinDF2 = interPimsVbpJoinDF1.join(
      vbpProgramCodesJoinPiPgmDF,
      (pgmPgDF.col("PI_PGM_ID")) === (vbpProgramCodesJoinPiPgmDF.col("PI_PGM_ID")), "INNER")

    //Step4 :- Joining interPIMSVBPJOINDF2 and pipgmlob table
    val interPimsVbpJoinDF3 = interPimsVbpJoinDF2.join(
      piPgmLobDF,
      (vbpProgramCodesJoinPiPgmDF.col("PI_PGM_ID")) === (piPgmLobDF.col("PI_PGM_ID")), "INNER")

    //Step5 :- Joining pimsIntermediateDF3 and pgmLobPsl
    val interPimsVbpJoinDF4 = interPimsVbpJoinDF3.join(pgmLobPslDF, Seq("LOB_ID", "PGM_LOB_EFCTV_DT"))

    //Step6 :- Joining pimsIntermediateDF4 and pgmLobPslParm table and printing the count and schema
    val PimsVbpJoinDF = interPimsVbpJoinDF4.join(
      pgmLobPslParmDF,
      (piPgmLobDF.col("PI_PGM_ID")) === (pgmLobPslParmDF.col("PI_PGM_ID"))
        && (piPgmLobDF.col("LOB_ID")) === (pgmLobPslParmDF.col("LOB_ID")) &&
        (piPgmLobDF.col("PGM_LOB_EFCTV_DT")) === (pgmLobPslParmDF.col("PGM_LOB_EFCTV_DT")), "INNER")

    //Selecting only the required columns for the final dataframe.
    val FinalPimsVbpJoinDF = PimsVbpJoinDF.select($"PROV_ORG_TAX_ID", $"IP_NPI").distinct()

    val finalVbpDFMap = Map("targetTableVbp" -> FinalPimsVbpJoinDF)

    finalVbpDFMap
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

    val vbpTargetTable = config.getString("vbpTargetTable")
    val targetTableVbpDF = outDFs.getOrElse("targetTableVbp", null)

    splicemachineContext.execute(s"delete from $spcpSpliceDB.$vbpTargetTable")
    info(s"$spcpSpliceDB.$vbpTargetTable has been truncated")

    splicemachineContext.insert(targetTableVbpDF, spcpSpliceDB + """.""" + vbpTargetTable)

    //Write as csv file
    val vbpExportFilePath = config.getString("spcp.vbp.export.hdfs.path")
    info(s"VBP export path is $vbpExportFilePath")

    val filler = "                                                          " //58 chars
    val vbpFileOutputDF = targetTableVbpDF.select(concat_ws(" ", rpad($"prov_org_tax_id", 10, " "),
      rpad($"ip_npi", 10, " "), lit(filler)))
    vbpFileOutputDF.coalesce(1) //So just a single part- file will be created
      .write.mode(SaveMode.Overwrite)
      .option("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false") //Avoid creating of crc files
//      .option("delimiter", " ") writing as single column only
//      .option("header", "true") //Write the header
      .text(vbpExportFilePath)

    info("VBP RECORDS INSERTED")
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






