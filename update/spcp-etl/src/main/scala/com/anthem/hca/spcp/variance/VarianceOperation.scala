package com.anthem.hca.spcp.variance

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
import com.anthem.hca.spcp.util.SpcpMailUtils
import com.anthem.hca.spcp.util.SPCPUDFs

class VarianceOperation(configPath: String, env: String, queryFileCategory: String,tableName: String)
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
   */
  override def extractData(): Map[String, DataFrame] = {
    //Reading the data into Data frames
    val startTime = DateTime.now
    info(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")

    //Reading the queries from config file
    info("Reading the queries from config file")
   // val tblList = config.getString("variance_tbl_list").split(",")
    val spcpAuditThrshldQry = config.getString("query_spcp_audit_thrshld").toLowerCase().replace(
      ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)

    info(s"[SPCP-ETL] Query for reading data from spcp_audit_thrshld table is: $spcpAuditThrshldQry")
    val spcpAuditThrshldDF = splicemachineContext.df(spcpAuditThrshldQry)

//    val dfs = for {
//      tableName <- tblList
//    } yield (
//      splicemachineContext.df(config.getString("query_variance").toLowerCase().replace(
//        ConfigKey.spcpSpliceDBPlaceHolder,
//        spcpSpliceDB).replace(ConfigKey.varianceTblPlaceHolder, tableName)).cache())

 //   val varianceAllTblDF = dfs.reduceLeft((a, b) => a.union(b))
    
    val varianceAllTblDF =  splicemachineContext.df(config.getString("query_variance").toLowerCase().replace(
        ConfigKey.spcpSpliceDBPlaceHolder,
        spcpSpliceDB).replace(ConfigKey.varianceTblPlaceHolder, tableName.toUpperCase())).cache()

    varianceAllTblDF.printSchema()
    varianceAllTblDF.show

    //Creating Map of Inbouond Data frames
    val mapDF = Map("varianceDF" -> varianceAllTblDF, "spcpAuditThrshld" -> spcpAuditThrshldDF)
    info(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }

  /*
   * This method takes care of all the transformation and derivation logic
   */
  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Processing Data Started: $startTime")

    val varianceDF = inDFs.getOrElse("varianceDF", null)
    val spcpAuditThrshldDF = inDFs.getOrElse("spcpAuditThrshld", null)

    val varianceAuditJoinDF = varianceDF.join(spcpAuditThrshldDF, Seq("TABLE_NM"))
      .withColumn("Is Threshld Exceeded", SPCPUDFs.checkThreshold($"VARIANCE", $"THRSHLD_LIMIT"))

    val varianceAuditDF = varianceAuditJoinDF.withColumnRenamed("TABLE_NM", "Table Name")
      .withColumnRenamed("LOAD_DT", "Load Date")
      .withColumnRenamed("ROW_CNT", "Row Count")
      .withColumnRenamed("PREV_ROW_CNT", "Previous Run Row Count")
      .withColumnRenamed("VARIANCE", "Variance%")
      .withColumnRenamed("THRSHLD_LIMIT", "Threshold Limit%")

    //TODO: isthreshhold crossed udf need to implemt
    //TABLE_NM	LOAD_DT	ROW_CNT	PREV_ROW_CNT	VARIANCE	THRSHLD_LIMIT

    val finalPcpDFMap = Map("varianceDF" -> varianceDF, "varianceAuditDF" -> varianceAuditDF)
    finalPcpDFMap

  }

  /*
   * This method truncates the target table and reloads it with the new data.
   */
  override def loadData(outDFs: Map[String, DataFrame]): Unit = {
    
     val varianceAuditDF = outDFs.getOrElse("varianceAuditDF", null)
    SpcpMailUtils.sendMail(config, varianceAuditDF)

    val varianceDF = outDFs.getOrElse("varianceDF", null)
      .withColumn("CREATED_BY", lit(sc.sparkUser))
      .withColumn("CREATED_DT", current_timestamp())

    varianceDF.printSchema()
    varianceDF.show

    splicemachineContext.insert(varianceDF, spcpSpliceDB + """.""" + "spcp_audit_variance")
   info("spcp_audit_variance RECORDS INSERTED")

   

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