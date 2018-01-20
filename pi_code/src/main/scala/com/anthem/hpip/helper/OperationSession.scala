package com.anthem.hpip.helper

import java.io.File
import java.io.InputStreamReader

import org.apache.hadoop.fs.Path

import com.anthem.hpip.config.Spark2Config
import com.typesafe.config.ConfigFactory

import grizzled.slf4j.Logging
import com.anthem.hpip.config.Spark2Config

class OperationSession(configPath: String, env: String, queryFileCategory: String) extends Logging {

  val sc = Spark2Config.spark.sparkContext
  //	val hiveContext = SparkConfig.hiveContext
//  val hiveContext = Spark2Config.spark
  val spark = Spark2Config.spark
  import spark.implicits._

  val hdfs = Spark2Config.hdfs

  val appConfPath = configPath + File.separator + s"application_${env}.properties"
  val queryFilePath = configPath + File.separator + s"query_${queryFileCategory}.properties"

  info(s"[HPIP-ETL] Application Config Path is $appConfPath")
  info(s"[HPIP-ETL] Query File Path is $queryFilePath")

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

  info(s"[HPIP-ETL] Construct OperationStrategy")

}

object OperationSession {
  def apply(confFilePath: String, env: String, queryFileCategory: String): OperationSession = new OperationSession(confFilePath, env, queryFileCategory)
}