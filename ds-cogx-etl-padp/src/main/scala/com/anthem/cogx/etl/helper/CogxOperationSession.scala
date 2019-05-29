package com.anthem.cogx.etl.helper

import scala.collection.mutable.ListBuffer
//import com.anthem.hpip.helper.Audit

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import java.text.SimpleDateFormat
import java.util.Calendar
//import com.anthem.hpip.config._
import com.anthem.cogx.etl.config._
import org.apache.spark.sql.DataFrame
import grizzled.slf4j.Logging
import org.apache.hadoop.mapreduce.Job
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.Seconds
//import com.anthem.hpip.util.DateUtils
import com.anthem.cogx.etl.util.CogxDateUtils
import org.apache.spark.sql.functions._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableMapReduceUtil, TableOutputFormat}
import java.io.File
import java.io.InputStreamReader
import java.io.FileInputStream

import org.apache.hadoop.fs.{FSDataInputStream, Path}
//import com.anthem.hpip.config.Spark2Config
import com.anthem.cogx.etl.config.CogxSpark2Config
import com.typesafe.config.{Config, ConfigFactory}
import grizzled.slf4j.Logging
//import com.anthem.hpip.config.Spark2Config

class CogxOperationSession(configPath: String, env: String, queryFileCategory: String) extends Logging {

  val sc = CogxSpark2Config.spark.sparkContext
  //	val hiveContext = SparkConfig.hiveContext
//  val hiveContext = Spark2Config.spark
  val spark = CogxSpark2Config.spark
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

 val hdfs = CogxSpark2Config.hdfs
        
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





  info(s"[COGX] Application Config Path is $appConfPath")
  info(s"[COGX] Query File Path is $queryFilePath")


  
  //merge both above conf file
  val config = queryConf.withFallback(appConf).resolve()

  info(s"[Cogx] Construct OperationStrategy")
  
  
  
  /////////////////////////////////////////////////////////////
  
      import CogxSpark2Config.spark.implicits._

  //Audit

  var ABC_start_time: DateTime = DateTime.now()

  
   @Override
   def beforeLoadData(): Unit = {
    var program = CogxSpark2Config.spark.sparkContext.appName
    var user_id = CogxSpark2Config.spark.sparkContext.sparkUser
    var app_id = CogxSpark2Config.spark.sparkContext.applicationId
    var ABClistBuffer = scala.collection.mutable.ListBuffer[com.anthem.cogx.etl.helper.CogxAudit]()

    var start_time = CogxDateUtils.getCurrentDateTime

    ABClistBuffer += CogxAudit(program, user_id, app_id, start_time, "0 Seconds", "Started")
    
    
    val hpipAuditDF = spark.createDataFrame(sc.parallelize(ABClistBuffer, 1).map(x => (x.program, x.user_id,x.app_id,x.start_time,x.app_duration,x.status))).
    toDF("program", "user_id","app_id","start_time","app_duration","status").withColumn("LastUpdate", lit(current_timestamp()))

    saveABCAudit(hpipAuditDF);
     

/*    val hpipAuditDF = listBuffer.toDS().withColumn("LastUpdate", lit(current_timestamp()))
    hpipAuditDF.write.insertInto(warehouseHiveDB + """.""" + "hpip_audit")*/
  
    }
  
 @Override
  def afterWriteData(): Unit = {

    var program = CogxSpark2Config.spark.sparkContext.appName
    var user_id = CogxSpark2Config.spark.sparkContext.sparkUser
    var app_id = CogxSpark2Config.spark.sparkContext.applicationId
    var ABClistBuffer = scala.collection.mutable.ListBuffer[com.anthem.cogx.etl.helper.CogxAudit]()

    var start = CogxDateUtils.getCurrentDateTime
    
    var listBuffer = ListBuffer[CogxAudit]()
    val duration = Seconds.secondsBetween(ABC_start_time, DateTime.now()).getSeconds + " Seconds"

    listBuffer += CogxAudit(program, user_id, app_id, start, duration, "completed")
   
    
   val hpipAuditDF = spark.createDataFrame(sc.parallelize(listBuffer, 1).map(x => (x.program, x.user_id,x.app_id,x.start_time,x.app_duration,x.status))).
    toDF("program", "user_id","app_id","start_time","app_duration","status").withColumn("LastUpdate", lit(current_timestamp()))
   saveABCAudit(hpipAuditDF);
    

    
    
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
  
  def getMapReduceJobConfiguration(tableName:String):Configuration = {
      val job = Job.getInstance(HBaseConfiguration.create(), "HDFS-to-HBase ETL")
      job.setOutputFormatClass(new org.apache.hadoop.hbase.mapreduce.TableOutputFormat[ImmutableBytesWritable].getClass)
      
      
      
      // Running on 
      
      
  if (env.equalsIgnoreCase("local"))
    {
      job.getConfiguration().addResource( (new FileInputStream(new File( configPath + File.separator + config.getString("hbaseconfigfile")  )) ) )
    }
  else
  {
    job.getConfiguration().addResource( (hdfs.open(new Path( configPath + File.separator + config.getString("hbaseconfigfile")  )) ) )
  }

      
      ///// The following is for Debug the configuration output
    var confIT = job.getConfiguration().iterator()
     println ("*********************************************************")
     while (confIT.hasNext())
     {
       var it = confIT.next()
       if (it.getKey.toString().contains("hbase"))
       {
       
       println("Key: "+ it.getKey())
       println("Value: " + it.getValue())
       }
     }
     println ("*********************************************************")
      
     //job.getConfiguration().addResource( (hdfs.open(new Path("hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/control/af35352Test/hbase-site.xml")) ) )    
      
      
      job.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE,tableName)
      job.getConfiguration
  }
  
  

}

object CogxOperationSession {
  def apply(confFilePath: String, env: String, queryFileCategory: String): CogxOperationSession = new CogxOperationSession(confFilePath, env, queryFileCategory)
}