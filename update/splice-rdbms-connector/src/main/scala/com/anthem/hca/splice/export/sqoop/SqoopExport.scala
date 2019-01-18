package com.anthem.hca.splice.export.sqoop

import scala.sys.process._
import com.anthem.hca.splice.util.AESEncryptDecryptUtils
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Connection
import java.sql.Statement
import java.sql.PreparedStatement
import java.sql.ResultSet
import com.typesafe.config.ConfigFactory
import java.io.File
import grizzled.slf4j.Logging
import org.apache.hadoop.fs.FileSystem
import java.net.URI
import org.apache.hadoop.conf.Configuration
import com.anthem.hca.splice.util.DateUtils

object SqoopExport extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 2, """This program needs 2 argument which  is conf path and table name""")

    val Array(confFilePath, tblName) = args

    val config = ConfigFactory.parseFile(new File(confFilePath))

    val db = config.getString("src.common.dbtype").toLowerCase()

    val jdbcUrl = config.getString(s"src.${db}.url")
    val jdbcDriver = config.getString(s"src.${db}.driver")
    val sqlServerSchema = config.getString(s"src.${db}.schema")
    val jdbcUsername = config.getString(s"src.${db}.username")
    val encrypted_value = config.getString("src.aes.encryptedValue")
    val keystoreLocation = config.getString("src.aes.keystoreLocation")
    val keystorePass = config.getString("src.aes.keystorePass")
    val alias = config.getString("src.aes.alias")
    val keyPass = config.getString("src.aes.keyPass")
    val jdbcPassword = AESEncryptDecryptUtils.decrypt(encrypted_value, keystoreLocation, keystorePass, alias, keyPass)

    val iscontroltblEnbl = config.getBoolean(s"src.mischallenous.${tblName}.isView")
    val controlTblName = config.getString("src.mischallenous.controlTblName")
    val numOfMappers = config.getString("src.mischallenous.numOfMappers")
    val exportDir = config.getString(s"src.mischallenous.exportDir")
    val yarnQueue = config.getString(s"src.mischallenous.yarnQueue")
    val fieldDelimiter = config.getString(s"src.mischallenous.fieldDelimiter")

    //getting inactive table
    val inactiveTbl = getInactiveTbl(jdbcUrl, jdbcUsername, jdbcPassword, tblName, controlTblName)
    val viewName = tblName

    if (null != inactiveTbl && !"".equalsIgnoreCase(inactiveTbl)) {
      val targetTblWithSchema = sqlServerSchema + "." + inactiveTbl
      //truncate table
      val trunc_sql = s"TRUNCATE TABLE ${targetTblWithSchema}"

      info("trunc_SQL" + trunc_sql)
      deleteOperation(jdbcUrl, jdbcUsername, jdbcPassword, trunc_sql)

      executeSqoop(yarnQueue, jdbcUrl, jdbcUsername, jdbcPassword, inactiveTbl, sqlServerSchema, exportDir, numOfMappers, fieldDelimiter)
      alterView(jdbcUrl, jdbcUsername, jdbcPassword, sqlServerSchema, viewName, inactiveTbl)
      updateOperation(inactiveTbl, viewName, jdbcUrl, jdbcDriver, jdbcUsername, jdbcPassword, controlTblName)
    } else {
      error("Inactive table not found")
      throw new Exception("Inactive table not found")
    }

  }
  def deleteOperation(jdbcUrl: String, jdbcUsername: String, jdbcPassword: String, qry: String) {

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

  def executeSqoop(yarn_queue: String, connectionString: String, username: String, password: String, inactiveTbl: String,
                   sqlServerSchema: String, exportDir: String, numOfMappers: String, fieldDelimiter: String) = {

    // To print every single line the process is writing into stdout and stderr respectively
    val sqoopLogger = ProcessLogger(
      normalLine => debug(normalLine),
      errorLine => errorLine match {
        case line if line.contains("ERROR") => error(line)
        case line if line.contains("WARN")  => warn(line)
        case line if line.contains("INFO")  => info(line)
        case line                           => debug(line)
      })

    // Create Sqoop command, every parameter and value must be a separated String into the Seq
    val command = Seq("sqoop", "export",
      s"-Dmapreduce.job.queuename=${yarn_queue}",
      "--connect", connectionString,
      "--connection-manager", "org.apache.sqoop.manager.SQLServerManager",
      "--username", username,
      "--password", password,
      "--table", inactiveTbl,
      "--driver", "net.sourceforge.jtds.jdbc.Driver",
      "-m", numOfMappers,
      //      "--input-fields-terminated-by", "\u0001",
      "--input-fields-terminated-by", fieldDelimiter,
      "--input-lines-terminated-by","|\n",
      "--map-column-java", "BRTH_DT=java.sql.Date",
      "--map-column-java", "SOR_DTM=java.sql.TIMESTAMP",
      "--map-column-java", "LAST_UPDTD_DTM=java.sql.TIMESTAMP",
      "--export-dir", exportDir,
      "--", "--schema", sqlServerSchema)

    println(command.mkString(" ").replace(password, "MASKED"))

    // result will contain the exit code of the command
    val result = command.!!

    println(s"SQOOP EXPORT Return code is : $result")

    /*if (result != 0) {
      error("The Sqoop process did not finished successfully")
      throw new Exception("Sqoop Export Failed")
    }
*/
  }

  def getInactiveTbl(jdbcUrl: String, jdbcUsername: String, jdbcPassword: String, tblName: String, controlTblName: String): String = {

    var connection: Connection = null
    var statement: Statement = null
    var inactiveTable = ""

    try {
      connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
      statement = connection.createStatement()
      val qry = s"SELECT TABLE_NM  FROM ${controlTblName} WHERE  VIEW_NM='${tblName}' and STATUS = 'N'"
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

  def updateOperation(tableName: String, viewName: String, jdbcUrl: String, jdbcDriver: String,
                      jdbcUsername: String, jdbcPassword: String, controlTblName: String) {

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

  def alterView(jdbcUrl: String, jdbcUsername: String, jdbcPassword: String,
                sqlServerSchema: String, viewName: String, inactiveTbl: String) = {

    var connection: Connection = null
    var statement: Statement = null
    var inactiveTblUpper = inactiveTbl.toUpperCase()
    var viewUpper = viewName.toUpperCase()

    try {
      connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
      statement = connection.createStatement()
      //   val qry = s"SELECT TABLE_NM  FROM ${controlTblName} WHERE  VIEW_NM='${tblName}' and STATUS = 'N'"
      //   val qry = "ALTER VIEW TS_PDPSPCP_XM.MBR_INFO AS  SELECT 'MBR_INFO2' TAB_NM, A.* FROM TS_PDPSPCP_XM.MBR_INFO2 A"
      val qry = s"ALTER VIEW ${sqlServerSchema}.${viewUpper} AS  SELECT '${inactiveTblUpper}' TAB_NM, A.* FROM ${sqlServerSchema}.${inactiveTblUpper} A"
      println(s"Alter table query is ${qry}")
      println("Start time " + DateUtils.currentTimestamp())
      statement.executeUpdate(qry)
      println("End time " + DateUtils.currentTimestamp())

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

}