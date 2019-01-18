/*
 * Copyright (c) 2017, Anthem Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE THIS FILE HEADER.
 *
 */
package com.anthem.hca.spcp.helper

import com.anthem.hca.spcp.config.SparkConfig
import java.io.InputStreamReader
import com.typesafe.config.ConfigFactory
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.FileSystem

/*
 * Just to verify the jceks
 */
object CredentialsVerifyDriver {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, "This Program needs exactly 3 arguments")
    val spark = SparkSession.builder().getOrCreate()
    lazy val hdfs: FileSystem = FileSystem.get(spark.sparkContext.hadoopConfiguration)

    spark.sparkContext.setLogLevel("ERROR")

    val Array(aCredentialStore, aCredentialAlias, value) = args

    assert(value.equals(getCredentialSecret(aCredentialStore, aCredentialAlias)), "values not macthed")
    println("values Matched")

    //loading application_<env>.properties file
    //    val appConfFile = hdfs.open(new Path(appConfPath))
    //    val appConfReader = new InputStreamReader(appConfFile)
    //    val appConf = ConfigFactory.parseReader(appConfReader)

    def getCredentialSecret(aCredentialStore: String, aCredentialAlias: String): String = {
      val hadoopConfig = new org.apache.hadoop.conf.Configuration()
      hadoopConfig.set(org.apache.hadoop.security.alias.CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH, aCredentialStore)
      if (null == hadoopConfig.getPassword(aCredentialAlias)) {
      //  error(s"Invalid credenials with store: $aCredentialStore and alias: $aCredentialAlias")
        throw new Exception(s"Invalid credenials with store: $aCredentialStore and alias: $aCredentialAlias")
      }
      String.valueOf(hadoopConfig.getPassword(aCredentialAlias))
    }

  }

}