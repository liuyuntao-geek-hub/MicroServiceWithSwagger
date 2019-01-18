package com.anthem.hca.spcp.product

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

/*
 * This class is meant for populating the PROD_PKG
 * and PROD_PLAN tables in Splice, which contains 
 * all Product related details.
 */
class ProductOperation(configPath: String, env: String, queryFileCategory: String)
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
   * @param  None
   * @return Map[String, DataFrame]
   */
  override def extractData(): Map[String, DataFrame] = {
    //Reading the data into Data frames
    val startTime = DateTime.now
    info(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")

    //Reading the queries from config file
    info("Reading the queries from config file")
    val prodPkgQuery = config.getString("query_prod_pkg").toLowerCase().replace(ConfigKey.cdlEdwdDBPlaceHolder, cdlEdwdDB)
    val prodPlanQuery = config.getString("query_prod_plan").toLowerCase().replace(ConfigKey.cdlEdwdDBPlaceHolder, cdlEdwdDB)
    
    info(s"[SPCP-ETL] Query for reading data from prod_pkg table is: $prodPkgQuery")
    info(s"[SPCP-ETL] Query for reading data from prod_plan table is: $prodPlanQuery")
    
    val prodPkgDF = splicemachineContext.internalDf(prodPkgQuery)
    val prodPlanDF = splicemachineContext.internalDf(prodPlanQuery)
    
    info("=============================================")
   

    //Creating Map of Inbouond Data frames
    val mapDF = Map("prodPkg" -> prodPkgDF, "prodPlan" -> prodPlanDF)
    info(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }

  /*
   * This method takes care of all the transformation and derivation logic
   * 
   * @param  Map[String, DataFrame]
   * @return Map[String, DataFrame]
   */
  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Processing Data Started: $startTime")

    val prodPkgDF = inDFs.getOrElse("prodPkg", null)
    val prodPlanDF = inDFs.getOrElse("prodPlan", null)

    val finalPcpDFMap = Map("targetTableProdPkg" -> prodPkgDF, "targetTableProdPlan" -> prodPlanDF)
    finalPcpDFMap

  }

  /*
   * This method truncates the target table and reloads it with the new data.
   * 
   * @param  Map[String, DataFrame]
   * @eturn  None
   */
  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Loading Data Started: $startTime")

    val ProdPkgTargetTable = config.getString("ProdPkgTargetTable")
    val targetTableProdPkgDF = outDFs.getOrElse("targetTableProdPkg", null)

    val ProdPlanTargetTable = config.getString("ProdPlanTargetTable")
    val targetTableProdPlanDF = outDFs.getOrElse("targetTableProdPlan", null)

    //Truncating the target tables
    splicemachineContext.execute(s"delete from $spcpSpliceDB.$ProdPkgTargetTable")
    info(s"$spcpSpliceDB.$ProdPkgTargetTable has been truncated")

    splicemachineContext.execute(s"delete from $spcpSpliceDB.$ProdPlanTargetTable")
    info(s"$spcpSpliceDB.$ProdPlanTargetTable has been truncated")

    //inserting records in the target tables
    splicemachineContext.insert(targetTableProdPkgDF, spcpSpliceDB + """.""" + ProdPkgTargetTable)
    info("ProdPkg RECORDS INSERTED")

    splicemachineContext.insert(targetTableProdPlanDF, spcpSpliceDB + """.""" + ProdPlanTargetTable)
    info("ProdPlan RECORDS INSERTED")
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