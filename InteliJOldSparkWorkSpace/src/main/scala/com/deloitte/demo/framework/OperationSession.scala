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


  // In parent class Logger works
  @transient lazy private val log = org.apache.log4j.LogManager.getLogger(OperationSession.getClass)

 // @transient private val log = Logger("TraitOperator")

  def StartABCInit:Unit={
    println(sc.hadoopConfiguration)
    println("Application ID From YT: "+sc.applicationId)
  }

  val conf = new SparkConf().setAppName(AppName).setMaster(master)
  val sc = SparkContext.getOrCreate(conf);
  val sqlContext = new org.apache.spark.sql.SQLContext(sc)
  var hiveSqlContext:HiveContext = _
  var  docReader:Config = ConfigFactory.load();

  // THis is to give the customized configuration file from the file system
  val exDocReader:Config = ConfigFactory.parseFile(new File( externalConfig.getOrElse( "environment.json")))
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