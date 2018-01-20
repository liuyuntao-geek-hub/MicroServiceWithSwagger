package com.anthem.hpip.helper

import java.util.Properties
import org.apache.log4j.PropertyConfigurator
import org.apache.spark.{ SparkConf, SparkContext }
import java.io.File
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.hadoop.fs.Path
import java.io.InputStreamReader
import org.apache.hadoop.fs.FileSystem
import grizzled.slf4j.Logging
import org.apache.hadoop.hbase.HBaseConfiguration

class OperationSession(configPath: String, env: String, queryFileCategory: String) extends Logging {


  //  val conf = new SparkConf().setAppName("ADL").setMaster("local[4]")
  val conf = new SparkConf()
    .set("hive.execution.engine", "spark")
    .set("spark.acls.enable", "true")

  val sc = new SparkContext(conf)
  sc.hadoopConfiguration.set("parquet.enable.summary-metadata", "false")

  val hiveContext = new HiveContext(sc)
  hiveContext.setConf("hive.exec.dynamic.partition", "true")
  hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")
  hiveContext.setConf("spark.sql.parquet.compression.codec", "snappy")
  hiveContext.setConf("hive.warehouse.data.skipTrash", "true")

  lazy val sqlContext = new SQLContext(sc)

  lazy val hdfs: FileSystem = FileSystem.get(sc.hadoopConfiguration)
  //  sc.setLogLevel("OFF")

  val appConfPath = configPath + File.separator + s"application_${env}.properties"
  val queryFilePath = configPath + File.separator + s"query_${queryFileCategory}.properties"
 
  info(appConfPath)
  info(queryFilePath)

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

  var configHB:org.apache.hadoop.conf.Configuration = null

//// The following need to be disabled if not run on Deloitte Network /////
configHB = HBaseConfiguration.create()
configHB.set("hbase.zookeeper.quorum", config.getString("HBaseServerUrl"))

  
  
  info("Construct OperationStrategy")

}

object OperationSession {
  def apply(confFilePath: String, env: String, queryFileCategory: String): OperationSession = new OperationSession(confFilePath, env, queryFileCategory)
}