/*
 * Copyright (c) 2017, Anthem Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE THIS FILE HEADER.
 *
 */
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
  val cdlRfdmDB = config.getString(ConfigKey.cdlRfdmDB)
  val spcpSpliceDB = config.getString(ConfigKey.spcpSpliceDB)
  
  val cdlWgspAllphiDB = config.getString(ConfigKey.cdlWgspAllphiDB)
  val cdlEdwdAllphiDB = config.getString(ConfigKey.cdlEdwdAllphiDB)
  val cdlPimsAllphiDB = config.getString(ConfigKey.cdlPimsAllphiDB)
  val cdlRfdmAllphiDB = config.getString(ConfigKey.cdlRfdmAllphiDB)

  val lastUpdatedDate = config.getString(ConfigKey.auditColumnName)
  val spcpAuditTable = config.getString("spcp-etl-audit-table")

  info(s"[SPCP-ETL] The cdl wgsp database name is $cdlWgspDB")
  info(s"[SPCP-ETL] The cdl edwd database name is $cdlEdwdDB")
  info(s"[SPCP-ETL] The spcp database name is $spcpSpliceDB")
  info(s"[SPCP-ETL] The cdl pims database name is $cdlPimsDB")

  info(s"[SPCP-ETL] The Audit column name is $lastUpdatedDate")
  info(s"[SPCP-ETL] The Audit table name is $spcpAuditTable")

  SpliceSpark.setContext(sc)

  val dbUrlWithOutPwd = config.getString("splice.connection.string")
  val passwordAlias = config.getString("password.jceks.alias")
  val passwordJceksLoc = config.getString("password.jceks.location")
  val password = getCredentialSecret(passwordJceksLoc, passwordAlias)
  val dbUrl = dbUrlWithOutPwd.concat(password)

  info(s"[SPCP-ETL] Splice URL is  $dbUrlWithOutPwd")
  info(s"[SPCP-ETL] Splice Password alias is $passwordAlias")
  info(s"[SPCP-ETL] Splice password JCEKS locaton is  $passwordJceksLoc")

  //Getting the bingKey using jceks and alias
  val splicemachineContext = new SplicemachineContext(dbUrl)

  def getCredentialSecret(aCredentialStore: String, aCredentialAlias: String): String = {
    val hadoopConfig = new org.apache.hadoop.conf.Configuration()
    hadoopConfig.set(org.apache.hadoop.security.alias.CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH, aCredentialStore)
    if (null == hadoopConfig.getPassword(aCredentialAlias)) {
      error(s"Invalid credenials with store: $aCredentialStore and alias: $aCredentialAlias")
      throw new Exception(s"Invalid credenials with store: $aCredentialStore and alias: $aCredentialAlias")
    }
    String.valueOf(hadoopConfig.getPassword(aCredentialAlias))
  }

}

object OperationSession {
  def apply(confFilePath: String, env: String, queryFileCategory: String): OperationSession = new OperationSession(confFilePath, env, queryFileCategory)
}