package com.anthem.hca.spcp.helper

import java.io.File
import java.io.InputStreamReader

import org.apache.hadoop.fs.Path

import com.splicemachine.derby.impl.SpliceSpark
import com.splicemachine.spark.splicemachine.SplicemachineContext

import grizzled.slf4j.Logging
import com.anthem.hca.spcp.config.SparkConfig
import com.typesafe.config.ConfigFactory
import com.anthem.hca.spcp.config.ConfigKey

class OperationSession(configPath: String, env: String, queryFileCategory: String) extends Logging {

  val spark = SparkConfig.spark
  val sc = spark.sparkContext
  val hdfs = SparkConfig.hdfs

  sc.setLogLevel("ERROR")

  val appConfPath = configPath + File.separator + s"spcp_etl_application_${env}.properties"
  val queryFilePath = configPath + File.separator + s"query_${queryFileCategory}.properties"

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

  val cdlWgspDB = config.getString(ConfigKey.cdlWgspDB)
  val cdlEdwdDB = config.getString(ConfigKey.cdlEdwdDB)
  val cdlPimsDB = config.getString(ConfigKey.cdlPimsDB)
  val spcpSpliceDB = config.getString(ConfigKey.spcpSpliceDB)

  val lastUpdatedDate = config.getString(ConfigKey.auditColumnName)
  val spcpAuditTable = config.getString("spcp-etl-audit-table")

  info(s"[SPCP-ETL] The cdl wgsp database name is $cdlWgspDB")
  info(s"[SPCP-ETL] The cdl edwd database name is $cdlEdwdDB")
  info(s"[SPCP-ETL] The spcp database name is $spcpSpliceDB")
  info(s"[SPCP-ETL] The cdl pims database name is $cdlPimsDB")

  info(s"[SPCP-ETL] The Audit column name is $lastUpdatedDate")
  info(s"[SPCP-ETL] The Audit table name is $spcpAuditTable")

  SpliceSpark.setContext(sc)

  //  val dbUrlWithOutPwd = "jdbc:splice://dwbdtest1r5w1.wellpoint.com:1527/splicedb;user=srcpdpspcpbthdv;password="
  //  val dbUrl = dbUrlWithOutPwd.concat(getCredentialSecret("jceks://hdfs//dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin//dev849.jceks", "dev849pass"))
  val dbUrlWithOutPwd = config.getString("splice.connection.string")
  val passwordAlias = config.getString("password.jceks.alias")
  val passwordJceksLoc = config.getString("password.jceks.location")
  val password = getCredentialSecret(passwordJceksLoc, passwordAlias)
  val dbUrl = dbUrlWithOutPwd.concat(password)
  
  //Getting the bingKey using jceks and alias
  val bingKeyJceksLoc = config.getString("bingKey.jceks.location")
  val bingKeyAlias = config.getString("bingKey.jceks.alias")
  val bingKey = getCredentialSecret(bingKeyJceksLoc, bingKeyAlias)

  val splicemachineContext = new SplicemachineContext(dbUrl)
  //  println(dbUrl)

  def getCredentialSecret(aCredentialStore: String, aCredentialAlias: String): String = {
    val config = new org.apache.hadoop.conf.Configuration()
    config.set(org.apache.hadoop.security.alias.CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH, aCredentialStore)
    String.valueOf(config.getPassword(aCredentialAlias))
  }

}

object OperationSession {
  def apply(confFilePath: String, env: String, queryFileCategory: String): OperationSession = new OperationSession(confFilePath, env, queryFileCategory)
}