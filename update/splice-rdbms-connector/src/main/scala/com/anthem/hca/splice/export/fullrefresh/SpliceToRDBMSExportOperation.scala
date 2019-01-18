package com.anthem.hca.splice.export.fullrefresh

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.splice.config.ConfigKey
import com.anthem.hca.splice.helper.Audit
import com.anthem.hca.splice.helper.OperationSession
import com.anthem.hca.splice.helper.Operator
import com.anthem.hca.splice.util.DataFrameUtils
import com.anthem.hca.splice.util.DateUtils

class SpliceToRDBMSExportOperation(configPath: String, env: String, tblName: String)
  extends OperationSession(configPath, env, tblName) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var listBuffer = ListBuffer[Audit]()

  var targetTableName = tblName
  var sourceCnt: Long = 0

  /*
   * Extracting the Splice machine data to Data frame
   */
  override def extractData(): Map[String, DataFrame] = {
    val startTime = DateTime.now
    info(s"[SPLICE-RDBMS_CONNECTOR] The loading of Data started with start time at :  + $startTime")
    info("Reading the queries from config file")

    val exportTblQuery = config.getString(tblName).toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, inboundSpliceDB)

    info(s"[SPLICE-RDBMS_CONNECTOR] Query for reading data from $tblName table is: $exportTblQuery")

    //    val exportTblDF = splicemachineContext.df(exportTblQuery).cache()
    val exportTblDF = splicemachineContext.df(exportTblQuery)
    exportTblDF.printSchema()

    info(s"[SPLICE-RDBMS_CONNECTOR] Loading data completed at : " + DateTime.now())

    sourceCnt = exportTblDF.count()
    if (sourceCnt == 0L) {
      //Send email
      throw new Exception(s"Source Table : $tblName is empty")
    }

    var targetTbl = tblName
    val viewName = tblName

    val cntrlDf = spark.read.jdbc(jdbcUrl, controlTblName, connectionProperties)
    cntrlDf.show()
    cntrlDf.printSchema()

    //Target Table
    if (iscontroltblEnbl) {
      targetTbl = getInactiveTableFromControlTable(viewName, cntrlDf)
    }

    targetTableName = targetTbl

    Map(targetTableName -> exportTblDF)

  }

  /*
   * No Transformations for current Scope
   */
  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    return inDFs
  }

  /*
   * Loading the data into RDBMS
   */

  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val exportTblDF = outDFs.getOrElse(targetTableName, null)
    val targetTblWithSchema = sqlServerSchema + "." + targetTableName
    val trunc_sql = s"TRUNCATE TABLE ${targetTblWithSchema}"

    info("trunc_SQL" + trunc_sql)
    deleteOperation(trunc_sql)

    exportTblDF.coalesce(numOfPartions).write.mode(SaveMode.Append).option("batchSize", batchSize).jdbc(jdbcUrl, targetTblWithSchema, connectionProperties)

    val targetTblCntAfterExport = spark.read.jdbc(jdbcUrl, targetTblWithSchema, connectionProperties).count()

    if (sourceCnt == targetTblCntAfterExport) {
      if (iscontroltblEnbl) {
        updateOperation(targetTableName, tblName)
      }
    } else {
      info("DataFrame count and sqlserver table count is not matching")
    }

  }

  def deleteOperation(qry: String) {

    var connection: Connection = null
    var statement: Statement = null

    try {
      connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
      connection.setAutoCommit(false)
      statement = connection.createStatement();
      statement.executeUpdate(qry)
      connection.commit()
    } catch {
      case e: SQLException =>
        error("An error occurred while deleting from database", e)
        throw e
    } finally {
      try {
        if (statement != null)
          statement.close()
      } catch {
        case e: Exception => error("delete succeeded, but closing statement failed", e)
      }
      try {
        if (connection != null)
          connection.close()
      } catch {
        case e: Exception => error("delete succeeded, but closing connection failed", e)
      }
    }

  }

  def updateOperation(tableName: String, viewName: String) {

    var connection: Connection = null
    var updStatement: PreparedStatement = null

    try {
      connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
      connection.setAutoCommit(false)

      val updSql = s"UPDATE ${controlTblName} SET STATUS = ? WHERE TABLE_NM = ? and VIEW_NM = '${viewName}'"

      updStatement = connection.prepareStatement(updSql)
      updStatement.setString(1, "Y")
      updStatement.setString(2, tableName)
      updStatement.addBatch()

      var table2 = tableName
      if (tableName.endsWith("1")) {
        table2 = table2.dropRight(1).concat("2")
      } else {
        table2 = table2.dropRight(1).concat("1")
      }
      updStatement.setString(1, "N")
      updStatement.setString(2, table2)
      updStatement.addBatch()

      updStatement.executeBatch()
      connection.commit()
    } catch {
      case e: SQLException =>
        error("An error occurred while deleting from database", e)
        throw e
    } finally {
      try {
        if (updStatement != null)
          updStatement.close()
      } catch {
        case e: Exception => error("delete succeeded, but closing updStatement failed", e)
      }
      try {
        if (connection != null)
          connection.close()
      } catch {
        case e: Exception => error("delete succeeded, but closing connection failed", e)
      }
    }
  }

  @Override
  def beforeExtractData() {

    program = sc.appName
    user_id = sc.sparkUser
    app_id = sc.applicationId

    start_time = DateTime.now()

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), "0min", "Started")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, lit(current_timestamp()))

    val spcpAuditDFWithUppercaseCol = DataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    //using upsert to avoid insert failure while job retry , also trying to limit job retries to 1
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, inboundSpliceDB + "." + spcpAuditTable)
  }

  @Override
  def afterLoadData() {

    var listBuffer = ListBuffer[Audit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), duration, "completed")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, current_timestamp())
    val spcpAuditDFWithUppercaseCol = DataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, inboundSpliceDB + "." + spcpAuditTable)

  }

  def getInactiveTableFromControlTable(viewName: String, cntrlDf: DataFrame): String = {
    /* val cntrlDf = spark.read.jdbc(jdbcUrl, controlTblName, connectionProperties)
    cntrlDf.show()
    cntrlDf.printSchema()*/

    val inActiveTblDf = cntrlDf.filter($"VIEW_NM" === viewName && $"STATUS" === "N").select($"TABLE_NM")
    if (inActiveTblDf.count != 1) {
      throw new Exception("Table name not found")
    }
    val tbl = inActiveTblDf.head().getString(0)
    info(s"target table name is :$tbl")
    (tbl)
  }

}