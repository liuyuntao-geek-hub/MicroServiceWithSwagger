package com.anthem.hca.splice.export.sqoop

import scala.sys.process._
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Connection
import java.sql.Statement
import java.sql.PreparedStatement
import java.sql.ResultSet
import com.typesafe.config.ConfigFactory
import java.io.File
import grizzled.slf4j.Logging

object SqoopExport extends Logging {

  def main(args: Array[String]): Unit = {

    val confPath = args(0)

    //    val confPath = "C:\\Users\\af30986\\git-sept\\splice-rdbms-connector\\src\\main\\resources\\src_application_dev.conf"

    val config = ConfigFactory.parseFile(new File(confPath))

    val db = "sqlserver"

    val jdbcUrl = config.getString(s"src.${db}.url")
    val jdbcDriver = config.getString(s"src.${db}.driver")
    val sqlServerSchema = "pdpspcp_xm"
//    val sqlServerSchema = config.getString(s"src.${db}.schema")
    // val jdbcUsername = config.getString(s"src.${db}.username")
    val jdbcUsername = "af30986"
    val jdbcPassword = ""

    //getting inactive table
    val inactiveTbl = getInactiveTbl(jdbcUrl, jdbcUsername, jdbcPassword)

    if (null != inactiveTbl && !"".equalsIgnoreCase(inactiveTbl)) {
      val targetTblWithSchema = sqlServerSchema + "." + inactiveTbl
      //truncate table
      val trunc_sql = s"TRUNCATE TABLE ${targetTblWithSchema}"

      info("trunc_SQL" + trunc_sql)
      deleteOperation(jdbcUrl, jdbcUsername, jdbcPassword, trunc_sql)
      //Sqoop Export
      executeSqoop(jdbcUrl, jdbcUsername, jdbcPassword, inactiveTbl)

      //Control table switch
      updateOperation(inactiveTbl, "MBR_INFO", jdbcUrl, jdbcDriver, jdbcUsername, jdbcPassword)
    } else {
      error("Inactive table not found")
      throw new Exception("Inactive table not found")
    }

  }

  def deleteOperation(jdbcUrl: String, jdbcUsername: String, jdbcPassword: String, qry: String) {
    println("=================================================================================")
    println("=================================================================================")
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
  def executeSqoop(connectionString: String, username: String, password: String,
                   viewName: String) = {
    // To print every single line the process is writing into stdout and stderr respectively
    val sqoopLogger = ProcessLogger(
      normalLine => debug(normalLine),
      errorLine => errorLine match {
        case line if line.contains("ERROR") => error(line)
        case line if line.contains("WARN")  => warn(line)
        case line if line.contains("INFO")  => info(line)
        case line                           => debug(line)
      })

    val dateCol = "\"BRTH_DT=java.sql.Date,LAST_UPDTD_DTM=java.sql.TIMESTAMP\""
    // Create Sqoop command, every parameter and value must be a separated String into the Seq
    val command = Seq("sqoop", "export",
      "-Dmapreduce.job.queuename=cdl_yarn",
      "--connect", connectionString,
      "--connection-manager", "org.apache.sqoop.manager.SQLServerManager",
      "--username", username,
      "--password", password,
      "--table", viewName,
      "--driver", "net.sourceforge.jtds.jdbc.Driver",
      "-m", "8",
      "--input-fields-terminated-by", "^",
      //    "--map-column-java", dateCol,
      "--map-column-java", "BRTH_DT=java.sql.Date",
      "--map-column-java", "LAST_UPDTD_DTM=java.sql.TIMESTAMP",
      "--export-dir", "/dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/etl/singleFile.csv",
      "--", "--schema", "PDPSPCP_XM")

    println(command)

    // result will contain the exit code of the command
    //  val result = command ! sqoopLogger
    val result = command.!!
    if (result != 0) {
      error("The Sqoop process did not finished successfully")
    } else {
      info("The Sqoop process finished successfully")
    }
  }

  def getInactiveTbl(jdbcUrl: String, jdbcUsername: String, jdbcPassword: String): String = {

    var connection: Connection = null
    var statement: Statement = null
    var inactiveTable = ""

    try {
      connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
      statement = connection.createStatement()
      val qry = "SELECT TABLE_NM  FROM PDPSPCP_XM.SPCP_CNTRL WHERE  VIEW_NM='MBR_INFO'and STATUS = 'N'"
      val rs = statement.executeQuery(qry)
      if (rs.next()) {
        inactiveTable = rs.getString("TABLE_NM")
      }
      inactiveTable
    } catch {
      case e: SQLException =>
        error("An error occurred while fetching inactive table from database", e)
        throw e
    } finally {
      try {
        if (statement != null)
          statement.close()
      } catch {
        case e: Exception => error("Fetch succeeded, but closing statement failed", e)
      }
      try {
        if (connection != null)
          connection.close()
      } catch {
        case e: Exception => error("Fetch succeeded, but closing connection failed", e)
      }
    }

  }

  def updateOperation(tableName: String, viewName: String, jdbcUrl: String, jdbcDriver: String, jdbcUsername: String, jdbcPassword: String) {

    var connection: Connection = null
    var updStatement: PreparedStatement = null

    try {
      connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
      connection.setAutoCommit(false)

      val updSql = "UPDATE PDPSPCP_XM.SPCP_CNTRL SET STATUS = ? WHERE TABLE_NM = ? and VIEW_NM = 'MBR_INFO'"

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

}