package com.anthem.hca.spcp.member

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.joda.time.DateTime
import org.joda.time.Minutes


import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import com.anthem.hca.spcp.config.ConfigKey

class MemberCodeOperation(configPath: String, env: String, queryFileCategory: String)
  extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var start = ""
  var listBuffer = ListBuffer[Audit]()

  override def extractData(): Map[String, DataFrame] = {

    //Reading the data into Data frames
    val startTime = DateTime.now
    info(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")

    //Reading the queries from config file
    info("Reading the queries from config file")

    val memProdEnrQuery = config.getString("query_mem_prod_enrolmnt").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val memQuery = config.getString("query_mem").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val xwalkQuery = config.getString("query_xwalk").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspPcpQuery = config.getString("query_wgsp_pcp").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspPcpAltidQuery = config.getString("query_wgsp_pcp_altid_mbr").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val piAfntyAtrbnQuery = config.getString("query_pi_afnty_atrbn").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    info(s"[SPCP-ETL] Query for reading data from Member Prod Enrolment table is: $memProdEnrQuery")
    info(s"[SPCP-ETL] Query for reading data from Member table is: $memQuery")
    info(s"[SPCP-ETL] Query for reading data from Xwalk table is: $xwalkQuery")
    info(s"[SPCP-ETL] Query for reading data from Wgsp PCP table is: $wgspPcpQuery")
    info(s"[SPCP-ETL] Query for reading data from Wgsp PCPAltid table is: $wgspPcpAltidQuery")
    info(s"[SPCP-ETL] Query for reading data from Pi Afnty Atrbn table is: $piAfntyAtrbnQuery")

    val memProfEnrDF = splicemachineContext.df(memProdEnrQuery)
    val memDF = splicemachineContext.df(memQuery)
    val xwalkDF = splicemachineContext.df(xwalkQuery)
    val wgspPcpDF = splicemachineContext.df(wgspPcpQuery)
    val wgspPcpAltidDF = splicemachineContext.df(wgspPcpAltidQuery)
    val piAfntyAtrbnDF = splicemachineContext.df(piAfntyAtrbnQuery)

    val mapDF = Map("memProfEnr" -> memProfEnrDF, "mem" -> memDF, "xwalk" -> xwalkDF, "wgspPcp" -> wgspPcpDF, "wgspPcpAltid" -> wgspPcpAltidDF, "piAfntyAtrbn" -> piAfntyAtrbnDF)

    info(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }

  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val startTime = DateTime.now
    info(s"[SPCP-ETL] Processing Data Started: $startTime")

    //Reading the data frames as elements from Map
    val memProfEnrDF = inDFs.getOrElse("memProfEnr", null)
    val memDF = inDFs.getOrElse("mem", null)
    val xwalkDF = inDFs.getOrElse("xwalk", null)
    val wgspPcpDF = inDFs.getOrElse("wgspPcp", null)
    val wgspPcpAltidDF = inDFs.getOrElse("wgspPcpAltid", null)
    val piAfntyAtrbnDF = inDFs.getOrElse("piAfntyAtrbn", null)

    val mpeMemJoinDF = memProfEnrDF.join(memDF, Seq("MBR_KEY"))
    val xwalkFinalDF = xwalkDF.join(mpeMemJoinDF, Seq("MBR_KEY"))
    val wgspPcpPcpalitJoinDF = wgspPcpDF.withColumnRenamed(
      "TAX_ID",
      "PROV_ORG_TAX_ID").join(wgspPcpAltidDF.withColumnRenamed("NPI", "IP_NPI"), Seq("PROV_PCP_ID","HMO_TYPE_CD","SRC_SOR_CD","PCP_EFCTV_DT"))
    val piAfntyAtrbnFinalDF = piAfntyAtrbnDF.join(wgspPcpPcpalitJoinDF, Seq("PROV_ORG_TAX_ID", "IP_NPI"))
    val memberFinalDF = piAfntyAtrbnFinalDF.join(xwalkFinalDF, Seq("MCID")).withColumn("LAST_UPDTD_DTM", current_timestamp())
    val memberFinalDFWithSelectColumns = memberFinalDF.select("MBR_KEY","HC_ID","FRST_NM","BRTH_DT","MCID","MBR_SQNC_NBR",
        "PROV_ORG_TAX_ID","IP_NPI","RANKG_ORDR_NBR","AFNTY_FILE_TYPE_CD","PROV_PCP_ID","LAST_UPDTD_DTM")
    val outMapDF = Map("targetTableMember" -> memberFinalDFWithSelectColumns)

    info(s"[SPCP-ETL] processing() Data Completed at: " + DateTime.now())
    info(s"[SPCP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    outMapDF

  }

  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Loading Data Started: $startTime")

    val memberTargetTable = config.getString("memberTargetTable")
    val targetTableMemberDF = outDFs.getOrElse("targetTableMember", null)
    
    splicemachineContext.execute(s"delete from $spcpSpliceDb.$memberTargetTable");
    
    splicemachineContext.insert(targetTableMemberDF, spcpSpliceDb + """.""" + memberTargetTable)

    println("MEMBERSHIP CODE COMMITTTED")
    info(s"$spcpSpliceDb.$memberTargetTable insert completed")
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
    //using upsert to avoid insert failure while job retry , also trying to limit job rretries to 1
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDb + """.""" + spcpAuditTable)
  }

  @Override
  def afterLoadData() { var listBuffer = ListBuffer[Audit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), duration, "completed")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, current_timestamp())
    spcpAuditDF.show
    val spcpAuditDFWithUppercaseCol = SPCPDataFrameUtils.columnsInUpper(spcpAuditDF)
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDb + """.""" + spcpAuditTable)}

}