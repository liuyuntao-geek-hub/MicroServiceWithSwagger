package com.deloitte.demo.framework

import java.io.File

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat

//import grizzled.slf4j._
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.fs.Path
import java.io.InputStreamReader
import org.apache.hadoop.fs.FileSystem
import grizzled.slf4j.Logging
import org.apache.spark.SparkFiles

/**
  * Created by yuntliu on 11/7/2017.
  */
class OperationSession (val AppName:String="ParentDriver", val master:String="local[5]",
                        val externalConfig:Option[String]
                       )
{
// Setup HBase Configuration

  ///////////// **** Constructing *********** //////////////////
 // grizzled.slf4j = not doing well when extended
 // val rootlogger = Logger(classOf[OperationSession])
/*   Problem of slf4j:
  - cannot show the class correctly:
  2017-11-11 23:30:40,557 [main] [WARN] (slf4j.scala:190) - Starting Program
  - Trait: cannot log correctly
  - SubClass cannot log correctly
    - Only class can log correction ===> Only class itself*/


 // @transient private val logger:Logger = LoggerFactory.getLogger(OperationSession.getClass)

/*  val conf = OperationSession.conf
  val sc = OperationSession.sc;
  val sqlContext = OperationSession.sqlContext
  var hiveSqlContext:HiveContext = _
  var  docReader:Config = OperationSession.docReader*/
/*  
    val conf = new SparkConf()
    .set("hive.execution.engine", "spark")
    .set("spark.acls.enable", "true")

  val sc = new SparkContext(conf)
    */
    

 val conf = new SparkConf().setAppName(AppName).setMaster(master)
 val sc = SparkContext.getOrCreate(conf);
 

  val sqlContext = new org.apache.spark.sql.SQLContext(sc)
  var hiveSqlContext:HiveContext = _
  var  docReader:Config = ConfigFactory.load();
  
  // In parent class Logger works
  @transient lazy private val log = org.apache.log4j.LogManager.getLogger(OperationSession.getClass)

 // @transient private val log = Logger("TraitOperator")

  def StartABCInit:Unit={
    println(sc.hadoopConfiguration)
    println("Application ID From YT: "+sc.applicationId)
  }


  
  
   val hdfs: FileSystem = OperationSession.hdfs
  //  sc.setLogLevel("OFF")

  //loading application_<env>.properties file
   //************************************ When running on deploy-mode cluster- local mode ***************************************
  val appConfFile = hdfs.open(new Path("hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))
  val appConfReader = new InputStreamReader(appConfFile)
  val exDocReader:Config = ConfigFactory.parseReader(appConfReader)
   //****************************************************************************************************************
   
 // val appConf = ConfigFactory.parseReader(appConfReader)


  // THis is to give the customized configuration file from the file system
//************************************ When running on deploy-mode client local mode ***************************************
 //  val exDocReader:Config = ConfigFactory.parseFile(new File( externalConfig.getOrElse( "environment.json")))
     //****************************************************************************************************************
   
  println(exDocReader.getString("env.inputFilePath"))

  //rootlogger.warn("Root")

/*
 logger.warn("OperationSession logging")
*/


  var configHB:org.apache.hadoop.conf.Configuration = null

  //// The following need to be disabled if not run on Deloitte Network /////
  configHB = HBaseConfiguration.create()
  configHB.set("hbase.zookeeper.quorum", "10.118.36.103")


  log.warn("OperationSession Start")

  if (exDocReader.getString("env.name").equalsIgnoreCase(StringKey.SnetServer))
    {
      hiveSqlContext = SQLHiveContextSingleton.getInstance(sc)



    }

  sc.setLogLevel("WARN")


/*  if (getEnvironment.equalsIgnoreCase(StringKey.SnetServer))
  {
    docReader = ConfigFactory.load("appServer")
    hiveSqlContext = SQLHiveContextSingleton.getInstance(sc)
  }*/


  println("Construct OperationStrategy")

/*  def getEnvironment():String={
    if (EnvOption.isDefined)
    {
      if (EnvOption.getOrElse(StringKey.localMachine).equalsIgnoreCase(StringKey.SnetServer))
      {
        return StringKey.SnetServer
      }
    }
    return StringKey.localMachine

  }*/


}


object OperationSession {
  
 // val conf = new SparkConf().setAppName("override").setMaster("local[4]")
    val conf = new SparkConf().setAppName("override").setMaster("yarn")
  val sc = SparkContext.getOrCreate(conf);
  val sqlContext = new org.apache.spark.sql.SQLContext(sc)
  var hiveSqlContext:HiveContext = _
  var  docReader:Config = ConfigFactory.load();
  
  val hdfs: FileSystem = FileSystem.get(sc.hadoopConfiguration)

  
  def apply(AppName:String,Master:String): OperationSession = new OperationSession(AppName,Master,  None)
  
  
  
  
}







object SQLHiveContextSingleton {
    @transient private var instance: HiveContext = _
    def getInstance(sparkContext: SparkContext): HiveContext = {
      synchronized {
        if (instance == null ) {
        instance = new HiveContext(sparkContext)
      }
      instance
    }
  }
}