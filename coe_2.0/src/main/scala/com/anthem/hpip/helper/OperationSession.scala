package com.anthem.hpip.helper

import scala.collection.mutable.ListBuffer
import com.anthem.hpip.helper.Audit

import java.text.SimpleDateFormat
import java.util.Calendar
import com.anthem.hpip.config._
import org.apache.spark.sql.DataFrame
import grizzled.slf4j.Logging

import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.Seconds
import com.anthem.hpip.util.DateUtils
import org.apache.spark.sql.functions._

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

///////////////////////////////////////////

  var appConfPath = ""
  var queryFilePath = ""
  var appConfFile:FSDataInputStream = null
  var appConfReader:InputStreamReader = null
  var appConf:Config=null
  var queryConfFile:FSDataInputStream = null
  var queryConfReader:InputStreamReader = null
  var queryConf:Config = null

 val hdfs = Spark2Config.hdfs
        
  if (env.equalsIgnoreCase("local"))
    {
      appConfPath = configPath + File.separator + s"application_${env}.properties"
      queryFilePath = configPath + File.separator + s"query_${queryFileCategory}.properties"
      appConf= ConfigFactory.parseFile(new File( appConfPath))
      queryConf =ConfigFactory.parseFile(new File( queryFilePath))

    }
  else
    {

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

//////////////////////////////////////////////////////////////
  
  /*
  
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

  
  
  */

/////////////////////////////////////////////////////////////
  
  
  //merge both above conf file
  val config = queryConf.withFallback(appConf).resolve()

  info(s"[HPIP-ETL] Construct OperationStrategy")
  
  
  
  /////////////////////////////////////////////////////////////
  
      import Spark2Config.spark.implicits._

  //Audit

  var ABC_start_time: DateTime = DateTime.now()

  
   @Override
   def beforeLoadData(): Unit = {
    var program = Spark2Config.spark.sparkContext.appName
    var user_id = Spark2Config.spark.sparkContext.sparkUser
    var app_id = Spark2Config.spark.sparkContext.applicationId
    var ABClistBuffer = scala.collection.mutable.ListBuffer[com.anthem.hpip.helper.Audit]()

    var start_time = DateUtils.getCurrentDateTime

    ABClistBuffer += Audit(program, user_id, app_id, start_time, "0 Seconds", "Started")
    
    
    val hpipAuditDF = spark.createDataFrame(sc.parallelize(ABClistBuffer, 1).map(x => (x.program, x.user_id,x.app_id,x.start_time,x.app_duration,x.status))).
    toDF("program", "user_id","app_id","start_time","app_duration","status").withColumn("LastUpdate", lit(current_timestamp()))

    saveABCAudit(hpipAuditDF);
     

/*    val hpipAuditDF = listBuffer.toDS().withColumn("LastUpdate", lit(current_timestamp()))
    hpipAuditDF.write.insertInto(warehouseHiveDB + """.""" + "hpip_audit")*/
  
    }
  
 @Override
  def afterWriteData(): Unit = {

    var program = Spark2Config.spark.sparkContext.appName
    var user_id = Spark2Config.spark.sparkContext.sparkUser
    var app_id = Spark2Config.spark.sparkContext.applicationId
    var ABClistBuffer = scala.collection.mutable.ListBuffer[com.anthem.hpip.helper.Audit]()

    var start = DateUtils.getCurrentDateTime
    
    var listBuffer = ListBuffer[Audit]()
    val duration = Seconds.secondsBetween(ABC_start_time, DateTime.now()).getSeconds + " Seconds"

    listBuffer += Audit(program, user_id, app_id, start, duration, "completed")
   
    
   val hpipAuditDF = spark.createDataFrame(sc.parallelize(listBuffer, 1).map(x => (x.program, x.user_id,x.app_id,x.start_time,x.app_duration,x.status))).
    toDF("program", "user_id","app_id","start_time","app_duration","status").withColumn("LastUpdate", lit(current_timestamp()))
   saveABCAudit(hpipAuditDF);
    
  // val hpipAuditDF = listBuffer.toDS().withColumn("LastUpdate", current_timestamp())
 //   hpipAuditDF.write.insertInto(warehouseHiveDB + """.""" + "hpip_audit")
    
    
  }
  
  def saveABCAudit( df:DataFrame ): Unit ={
    
    val saveFormat:String = config.getString("ABCSaveFormat")
    if (saveFormat.equalsIgnoreCase("csvFile"))
    {
       df.show()
       df.write.mode("append").format( "com.databricks.spark.csv" )
      .option( "header", "true" ).save(config.getString("ABCLocalOutputPathFile") )
    }
    else if (saveFormat.equalsIgnoreCase("hive"))
    {
      df.show()
    }
    
    else if (saveFormat.equalsIgnoreCase("none"))
    {
      df.show()
    }
    else
    {
      df.show()
    }
    
  }
  
  
  
  

}

object OperationSession {
  def apply(confFilePath: String, env: String, queryFileCategory: String): OperationSession = new OperationSession(confFilePath, env, queryFileCategory)
}