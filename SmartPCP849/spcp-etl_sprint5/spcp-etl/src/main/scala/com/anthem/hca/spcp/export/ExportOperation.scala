package com.anthem.hca.spcp.export

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.expr
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.types.DoubleType
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import com.anthem.hca.spcp.util.SPCPUDFs
import java.util.Properties
import org.apache.spark.sql.SaveMode
import com.anthem.hca.spcp.config.ConfigKey

class ExportOperation(configPath: String, env: String, queryFileCategory: String, tblName: String)
  extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var listBuffer = ListBuffer[Audit]()

  override def extractData(): Map[String, DataFrame] = {
    val startTime = DateTime.now
    println(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")
    println("Reading the queries from config file")

    val exportTblQuery = config.getString(s"export_query_$tblName").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)

    println(s"[SPCP-ETL] Query for reading data from $tblName table is: $exportTblQuery")

    val exportTblDF = splicemachineContext.df(exportTblQuery)

    //    exportTblDF.show(1)
    exportTblDF.printSchema()

    println(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    val jdbcUrl = config.getString("spcp.sqlserver.url")
    val jdbcDriver = config.getString("spcp.sqlserver.driver")
    val jdbcUsername = config.getString("spcp.sqlserver.username")
    val jdbcPassword = config.getString("spcp.sqlserver.password")
    val sqlServerSchema = config.getString("spcp.sqlserver.schema")

    val targetTbl = sqlServerSchema + "." + tblName

    print(s"target table name is :$targetTbl")
    // Create the JDBC URL without passing in the user and password parameters.

    // Create a Properties() object to hold the parameters.
    import java.util.Properties
    val connectionProperties = new Properties()
    connectionProperties.put("driver", jdbcDriver)
    connectionProperties.put("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    //    connectionProperties.put("user", "af30986")
    //    connectionProperties.put("password", "AAAAAAAAAAAAAAA")

    exportTblDF.write.mode(SaveMode.Append).jdbc(jdbcUrl, targetTbl, connectionProperties)

    // Given the number of partitions above,
    //we can reduce the partition value by calling coalesce() or increase it by calling repartition() to manage the number of connections.
    //refAdrsDF.repartition(10).write.mode(SaveMode.Overwrite).jdbc(jdbcUrl, "diamonds", connectionProperties)

    return null
  }

  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    return null
  }

  override def loadData(outDFs: Map[String, DataFrame]): Unit = {
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
    //  splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDb + "." + spcpAuditTable)
  }

  @Override
  def afterLoadData() {

    var listBuffer = ListBuffer[Audit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), duration, "completed")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, current_timestamp())
    val spcpAuditDFWithUppercaseCol = SPCPDataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    //splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDb + "." + spcpAuditTable)

  }

}