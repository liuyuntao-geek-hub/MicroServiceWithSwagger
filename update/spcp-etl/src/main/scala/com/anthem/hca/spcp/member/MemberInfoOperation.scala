package com.anthem.hca.spcp.member

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.lit
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.config.ConfigKey
import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import com.anthem.hca.spcp.util.SpcpMailUtils
import org.apache.spark.sql.SaveMode
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.expressions.Window

/*
 * This Class Populates MBR_INFO target table for Member detailed data. 
 * The source consists of 4 (PIMS and EDWD) tables and 2 WGSP tables. 
 * The target table is a de-normalization of these source tables.
 * The ETL logic loads these tables into the spark memory, transforms them
 * and writes the final output into the Member target table. 
 * The de-normalized data in target table is used to fetch data for Member details for Affinity micro service 
 * queries are configured in query_spcp.properties files.The below are the tables taht are being used
 * mbr_prod_enrlmnt,mbr,pi_mcid_mbr_key_xwalk_all,wgsp_pcp,wgsp_pcp_altid,pi_afnty_atrbn
 * 
 * 
 * 
 * @author Gouse Marurshaik (af69961)
 * 
 */
class MemberInfoOperation(configPath: String, env: String, queryFileCategory: String)
    extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var start = ""
  var listBuffer = ListBuffer[Audit]()

  //reading the member target table name from config file
  val memberTargetTable = config.getString("memberTargetTable")

  //reading the sendNtfctnMail value from the config file
  val sendNtfctnMail = config.getBoolean("sendNtfctnMail")

  /*
 * This Method reads the queries configured in query_spcp.properties file 
 * and converts into Dataframe and Map all the dataframes to single dataframe using Map()
 * and sends and input to transformData method. 
 */
  override def extractData(): Map[String, DataFrame] = {

    //Reading the data into Data frames
    val startTime = DateTime.now
    info(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")

    info("Reading the queries from config file")
    //Reading the queries from config file
    val memProdEnrQuery = config.getString("query_mem_prod_enrolmnt").replace(ConfigKey.cdlEdwdDBPlaceHolder, cdlEdwdDB)
    val memQuery = config.getString("query_mem").replace(ConfigKey.cdlEdwdDBPlaceHolder, cdlEdwdDB)
    val xwalkQuery = config.getString("query_xwalk").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val piAfntyAtrbnQuery = config.getString("query_pi_afnty_atrbn").replace(ConfigKey.cdlPimsDBPlaceHolder, cdlPimsDB)
    val wgspPcpQuery = config.getString("query_wgsp_pcp_mbr").replace(ConfigKey.cdlWgspDBPlaceHolder, cdlWgspDB)
    val wgspPcpAltidQuery = config.getString("query_wgsp_pcp_altid_mbr").replace(ConfigKey.cdlWgspDBPlaceHolder, cdlWgspDB)

    info(s"[SPCP-ETL] Query for reading data from Member Prod Enrolment table is: $memProdEnrQuery")
    info(s"[SPCP-ETL] Query for reading data from Member table is: $memQuery")
    info(s"[SPCP-ETL] Query for reading data from Xwalk table is: $xwalkQuery")
    info(s"[SPCP-ETL] Query for reading data from Wgsp PCP table is: $wgspPcpQuery")
    info(s"[SPCP-ETL] Query for reading data from Wgsp PCPAltid table is: $wgspPcpAltidQuery")
    info(s"[SPCP-ETL] Query for reading data from Pi Afnty Atrbn table is: $piAfntyAtrbnQuery")

    val memProfEnrDF = splicemachineContext.internalDf(memProdEnrQuery)
    val memDF = splicemachineContext.internalDf(memQuery)
    val xwalkDF = splicemachineContext.internalDf(xwalkQuery)
    val wgspPcpDF = splicemachineContext.internalDf(wgspPcpQuery)
    val wgspPcpAltidDF = splicemachineContext.internalDf(wgspPcpAltidQuery)
    val piAfntyAtrbnDF = splicemachineContext.internalDf(piAfntyAtrbnQuery)
    //    val spcpThrhldDF = splicemachineContext.internalDf(thrshldTblLoadQuery)

    val mapDF = Map("memProfEnr" -> memProfEnrDF, "mem" -> memDF, "xwalk" -> xwalkDF, "wgspPcp" -> wgspPcpDF,
      "wgspPcpAltid" -> wgspPcpAltidDF, "piAfntyAtrbn" -> piAfntyAtrbnDF)

    info(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }

  /*
   * This  method reads the Dataframe from Map as an input from extractData() 
   * and performs join operations on the extracted elements from the Dataframe 
   */
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

    //Joins memdf,memProfEnrDF on "MBR_KEY"
    val mpeMemJoinDF = memProfEnrDF.join(memDF, Seq("MBR_KEY"))
      .select($"MBR_KEY", $"HC_ID", $"FRST_NM", $"MID_INIT_NM", $"BRTH_DT",
        $"MBR_SQNC_NBR", $"LAST_NM", $"MBRSHP_SOR_CD", $"INDRCT_INSRT_CD", $"SOR_DTM",$"SBSCRBR_ID").distinct()

    //Joins xwalkDF,mpeMemJoinDF on "MBR_KEY"
    val xwalkFinalDF = xwalkDF.join(mpeMemJoinDF, Seq("MBR_KEY"))
      .select($"MBR_KEY", $"HC_ID", $"FRST_NM", $"MID_INIT_NM", $"BRTH_DT", $"MBR_SQNC_NBR",
        $"LAST_NM", $"MCID", $"MBRSHP_SOR_CD", $"INDRCT_INSRT_CD", $"SOR_DTM",$"SBSCRBR_ID").distinct()

    //Rename column TAX_ID of wgspPcpDF to PROV_ORG_TAX_ID,column NPI of wgspPcpAltidDF to IP_NPI 
    //and Joins wgspPcpDF,wgspPcpAltidDF on "PROV_PCP_ID","HMO_TYPE_CD","SRC_SOR_CD","PCP_EFCTV_DT"
    val wgspPcpPcpalitJoinDF = wgspPcpDF.withColumnRenamed(
      "TAX_ID",
      "PROV_ORG_TAX_ID").join(wgspPcpAltidDF.withColumnRenamed("NPI", "IP_NPI"),
        Seq("PROV_PCP_ID", "HMO_TYPE_CD", "SRC_SOR_CD", "PCP_EFCTV_DT"))
      .select($"PROV_ORG_TAX_ID", $"IP_NPI", $"PROV_PCP_ID").distinct()

    //Joins piAfntyAtrbnDF,wgspPcpPcpalitJoinDF on "PROV_ORG_TAX_ID", "IP_NPI"
    val piAfntyAtrbnFinalDF = piAfntyAtrbnDF.join(wgspPcpPcpalitJoinDF, Seq("PROV_ORG_TAX_ID", "IP_NPI"))
      .select($"PROV_ORG_TAX_ID", $"IP_NPI", $"PROV_PCP_ID",
        $"RANKG_ORDR_NBR", $"AFNTY_FILE_TYPE_CD", $"MCID").distinct()

    //Joins piAfntyAtrbnFinalDF,xwalkFinalDF on "MCID" and add new column "LAST_UPDTD_DTM" to form new dataframe memberFinalDF
   /* val memberFinalDF = piAfntyAtrbnFinalDF.join(xwalkFinalDF, Seq("MCID"))
      .withColumn("LAST_UPDTD_DTM", current_timestamp())*/
      
      val windowFunc = Window.partitionBy($"MBR_KEY", $"HC_ID", $"MCID", $"PROV_ORG_TAX_ID", $"IP_NPI",
                $"RANKG_ORDR_NBR", $"PROV_PCP_ID",$"SBSCRBR_ID").orderBy($"SOR_DTM" desc)
    //Joins piAfntyAtrbnFinalDF,xwalkFinalDF on "MCID" and add new column "LAST_UPDTD_DTM" to form new dataframe memberFinalDF
    val memberFinalDF = piAfntyAtrbnFinalDF.join(xwalkFinalDF, Seq("MCID"))
      .withColumn("LAST_UPDTD_DTM", current_timestamp()).withColumn("rowNum", row_number().over(windowFunc))
        .where($"rowNum"===1)


    //Select only required columns to form memberFinalDFWithSelectColumns
    val memberFinalDFWithSelectColumns = memberFinalDF
      .select($"MBR_KEY", $"HC_ID", $"FRST_NM", $"LAST_NM", $"MID_INIT_NM", $"BRTH_DT",
        $"MCID", $"MBR_SQNC_NBR", $"PROV_ORG_TAX_ID", $"IP_NPI", $"RANKG_ORDR_NBR",
        $"AFNTY_FILE_TYPE_CD", $"PROV_PCP_ID", $"LAST_UPDTD_DTM", $"MBRSHP_SOR_CD",
        $"INDRCT_INSRT_CD", $"SOR_DTM",$"SBSCRBR_ID").persist(StorageLevel.MEMORY_AND_DISK)

    memberFinalDFWithSelectColumns.printSchema()
    var outMapDF: Map[String, DataFrame] = null

    if (sendNtfctnMail) {
      val minRowCnt = config.getInt("memberDataMinCnt")
      val maxRowCnt = config.getInt("memberDataMaxCnt")
      val finalDfCnt = memberFinalDFWithSelectColumns.count()
      info(s"[SPCP-ETL] MBR_INFO Table Count is : " + finalDfCnt)
      if ((finalDfCnt < minRowCnt) || (finalDfCnt > maxRowCnt)) {
        outMapDF = Map("targetTableMember" -> null)
        info("Member data not within threshold limits")
        val mailDF = spark.sparkContext.parallelize(List((memberTargetTable, minRowCnt, maxRowCnt, finalDfCnt)))
          .toDF("Table_Nm", "Min_Cnt", "Max_Cnt", "Current_Record_Cnt")
        mailDF.show()
        SpcpMailUtils.sendMail(config, mailDF)
        throw new Exception("[SPCP-ETL] : MBR_INFO Data Load Suspended. Row count beyond threshold.")
      } else {
        outMapDF = Map("targetTableMember" -> memberFinalDFWithSelectColumns)
      }
    } else {
      outMapDF = Map("targetTableMember" -> memberFinalDFWithSelectColumns)
    }

    info(s"[SPCP-ETL] processing() Data Completed at: " + DateTime.now())
    info(s"[SPCP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    outMapDF
  }

  //This method gets the final Member dataframe as an input from transformData() and writes the dataframe to 
  //membertarget table MBR_INFO after deleting the existing data from MBR_INFO in splice machine
  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Loading Data Started: $startTime")

    val targetTableMemberDF = outDFs.getOrElse("targetTableMember", null)

    if (targetTableMemberDF == null) {
      info("MEMBER RECORDS CANNOT BE INSERTED")
    } else {
      //Deletes the data from MBR_INFO of splice machine
      splicemachineContext.execute(s"delete from $spcpSpliceDB.$memberTargetTable");
      val sqoopExportFilePath = config.getString("spcp.sqoop.export.hdfs.path")
      val sqoopExportdelimiter = config.getString("spcp.sqoop.export.hdfs.delimiter")

      targetTableMemberDF //So just a single part- file will be created
        .write.mode(SaveMode.Overwrite)
        .option("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false") //Avoid creating of crc files
        .option("delimiter", sqoopExportdelimiter) // "\u0001"
        .option("inferSchema", "true").option("timestampFormat", "yyyy-MM-dd HH:mm:ss")
        .option("header", "false") //Write the header
        .csv(sqoopExportFilePath)

      splicemachineContext.insert(targetTableMemberDF, spcpSpliceDB + """.""" + memberTargetTable)

      info("MEMBERSHIP CODE COMMITTTED")
      info(s"$spcpSpliceDB.$memberTargetTable insert completed")
    }
  }

  /*
   * This method executes before extractData() for Auditing purpose and writes the start time,program ,user id,app id 
   * details to the audit table in splice machine. 
   */
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
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDB + """.""" + spcpAuditTable)
  }

  /*
   * This method executes after inserting data in MBR_INFO target table in splice machine 
   *  for Auditing purpose and writes the end time,program ,user id,app id 
   * details to the audit table in splice machine. 
   */
  @Override
  def afterLoadData() {
    var listBuffer = ListBuffer[Audit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), duration, "completed")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, current_timestamp())
    spcpAuditDF.show
    val spcpAuditDFWithUppercaseCol = SPCPDataFrameUtils.columnsInUpper(spcpAuditDF)
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDB + """.""" + spcpAuditTable)
  }

}