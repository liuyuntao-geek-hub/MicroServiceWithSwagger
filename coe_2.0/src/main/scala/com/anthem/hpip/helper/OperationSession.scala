package com.anthem.hpip.helper

import java.io.File
import java.io.InputStreamReader

import org.apache.hadoop.fs.{FSDataInputStream, Path}
import com.anthem.hpip.config.Spark2Config
import com.typesafe.config.{Config, ConfigFactory}
import grizzled.slf4j.Logging
import com.anthem.hpip.config.Spark2Config

class OperationSession(configPath: String, env: String, queryFileCategory: String) extends Logging {

  val sc = Spark2Config.spark.sparkContext
  //	val hiveContext = SparkConfig.hiveContext
//  val hiveContext = Spark2Config.spark
  val spark = Spark2Config.spark
  import spark.implicits._



  var appConfPath = ""
  var queryFilePath = ""
  var appConfFile:FSDataInputStream = null
  var appConfReader:InputStreamReader = null
  var appConf:Config=null
  var queryConfFile:FSDataInputStream = null
  var queryConfReader:InputStreamReader = null
  var queryConf:Config = null

  if (env.equalsIgnoreCase("local"))
    {
      appConfPath = configPath + File.separator + s"application_${env}.properties"
      queryFilePath = configPath + File.separator + s"query_${queryFileCategory}.properties"
      appConf= ConfigFactory.parseFile(new File( appConfPath))
      queryConf =ConfigFactory.parseFile(new File( queryFilePath))

    }
  else
    {
      val hdfs = Spark2Config.hdfs
      appConfPath = configPath + File.separator + s"application_${env}.properties"
      queryFilePath = configPath + File.separator + s"query_${queryFileCategory}.properties"
      appConfFile = hdfs.open(new Path(appConfPath))
      appConfReader = new InputStreamReader(appConfFile)
      appConf = ConfigFactory.parseReader(appConfReader)

      queryConfFile = hdfs.open(new Path(queryFilePath))
      queryConfReader = new InputStreamReader(queryConfFile)
      queryConf = ConfigFactory.parseReader(queryConfReader)
    }





  info(s"[HPIP-ETL] Application Config Path is $appConfPath")
  info(s"[HPIP-ETL] Query File Path is $queryFilePath")

  //loading application_<env>.properties file




  //loading query_<queryFileCategory>.properties file




  //merge both above conf file
  val config = queryConf.withFallback(appConf).resolve()

  info(s"[HPIP-ETL] Construct OperationStrategy")

}

object OperationSession {
  def apply(confFilePath: String, env: String, queryFileCategory: String): OperationSession = new OperationSession(confFilePath, env, queryFileCategory)
}