package com.anthem.hca.spliceexport.helper

import java.io.File
import java.io.InputStreamReader
import java.util.Properties

import org.apache.hadoop.fs.Path

import com.anthem.hca.splice.config.SparkConfig
import com.splicemachine.derby.impl.SpliceSpark
import com.splicemachine.spark.splicemachine.SplicemachineContext
import com.typesafe.config.ConfigFactory

import grizzled.slf4j.Logging

class OperationSession(configPath: String,env: String,tblName: String) extends Logging {

  val spark = SparkConfig.spark
  val sc = spark.sparkContext
  val hdfs = SparkConfig.hdfs

  sc.setLogLevel("ERROR")

  val appConfPath = configPath + File.separator + s"src_application_${env}.conf"
  val queryFilePath = configPath + File.separator + s"src_source_query.properties"

  info(s"[SPCP-ETL] Application Config Path is $appConfPath")
  info(s"[SPCP-ETL] Query File Path is $queryFilePath")
  //  info(s"[SPCP-ETL] Config File Path is $configFilePath")

  //loading application_<env>.properties file
  val appConfFile = hdfs.open(new Path(appConfPath))
  val appConfReader = new InputStreamReader(appConfFile)
  val appConf = ConfigFactory.parseReader(appConfReader)

  //loading query_<queryFileCategory>.properties file
  val queryConfFile = hdfs.open(new Path(queryFilePath))
  val queryConfReader = new InputStreamReader(queryConfFile)
  val queryConf = ConfigFactory.parseReader(queryConfReader)

  //merge both above conf file
  val config = queryConf.withFallback(appConf).resolve()

  val inboundSpliceDB = config.getString("src.splice.inboundSpliceDB")
  val lastUpdatedDate = config.getString("src.splice.auditColumnName")
  val spcpAuditTable = config.getString("src.splice.spcp-etl-audit-table")

  info(s"[SPCP-ETL] The Inbound Hive schema is $inboundSpliceDB")
  info(s"[SPCP-ETL] The Audit column name is $lastUpdatedDate")
  info(s"[SPCP-ETL] The Audit table name is $spcpAuditTable")

  SpliceSpark.setContext(sc)

  val dbUrlWithOutPwd = config.getString("src.splice.url")
  val passwordAlias = config.getString("src.splice.password.jceks.alias")
  val passwordJceksLoc = config.getString("src.splice.password.jceks.location")
  val password = getCredentialSecret(passwordJceksLoc, passwordAlias)
  val dbUrl = dbUrlWithOutPwd.concat(password)

  val splicemachineContext = new SplicemachineContext(dbUrl)
  
  val db = config.getString("src.common.dbtype").toLowerCase()

  val jdbcUrl = config.getString(s"src.${db}.url")
  val jdbcDriver = config.getString(s"src.${db}.driver")
  val jdbcUsername = config.getString(s"src.${db}.username")
  val sqlpasswordAlias = config.getString(s"src.${db}.password.jceks.alias")
  val sqlpasswordJceksLoc = config.getString(s"src.${db}.password.jceks.location")
  val jdbcPassword = getCredentialSecret(sqlpasswordJceksLoc, sqlpasswordAlias)
  val sqlServerSchema = config.getString(s"src.${db}.schema")
  val numOfPartions = config.getInt(s"src.${db}.allowed.connection")
  
  val iscontroltblEnbl = config.getBoolean(s"src.mischallenous.${tblName}.isView")
  val controlTblName = config.getString("src.mischallenous.controlTblName")
  val batchSize = config.getString(s"src.${db}.batchSize")

  info(s"[SPLICE-RDBMS_CONNECTOR] Export table is view: $iscontroltblEnbl")
  // Create the JDBC URL without passing in the user and password parameters.

  val connectionProperties = new Properties()
  connectionProperties.put("driver", jdbcDriver)
  connectionProperties.put("user", jdbcUsername)
  connectionProperties.put("password", jdbcPassword)

  def getCredentialSecret(aCredentialStore: String, aCredentialAlias: String): String = {
    val config = new org.apache.hadoop.conf.Configuration()
    config.set(org.apache.hadoop.security.alias.CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH, aCredentialStore)
    String.valueOf(config.getPassword(aCredentialAlias))
  }

}

object OperationSession {
  def apply(confFilePath: String,env: String, tblName: String): OperationSession = new OperationSession(confFilePath,env,tblName)
}