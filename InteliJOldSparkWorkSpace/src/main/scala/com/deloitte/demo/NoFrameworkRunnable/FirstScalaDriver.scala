package com.deloitte.demo.NoFrameworkRunnable

import java.io.File
import java.util.Properties

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import org.apache.log4j.PropertyConfigurator
import org.apache.spark.{SparkConf, SparkContext}
/**
  * Created by yuntliu on 11/7/2017.
  */
class FirstScalaDriver {
  ///////////// **** Constructor *********** //////////////////

  ///////////// **** Done Constructor *********** //////////////////
}
object FirstScalaDriver {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName( "First Application" ).setMaster( "local[3]" )
    val sc = SparkContext.getOrCreate( conf );
    val sqlContext = new org.apache.spark.sql.SQLContext( sc )
    sc.setLogLevel( "OFF" )

    var myConfig:Config = ConfigFactory.load()
/*
    if (args.length>0)
      {
            if (args(0).equalsIgnoreCase("SERVER"))
              {
                myConfig = ConfigFactory.load("appServer")
              }
      }
*/

     val  propertyFile="/application.properties"

    val dbProperties = new Properties
    dbProperties.load( getClass().getResourceAsStream(propertyFile) )
    PropertyConfigurator.configure( dbProperties )

   // println( "Construct OperationStrategy" )
  //  val path = System.getProperty("user.dir") + dbProperties.getProperty( "inputFilePath" ) + dbProperties.getProperty( "inputFileName" )

   //val path = dbProperties.getProperty( "inputFilePath" ) + dbProperties.getProperty( "inputFileName" )
   val exDocReader:Config = ConfigFactory.parseFile(new File( ( "environment.json")))
    println(exDocReader.getString("env.inputFilePath"))

    val path = exDocReader.getString("env.inputFilePath")+myConfig.getString("inputFileName")
    println("Path:" + path)
    val dataSourceDirectory = ""
    val rawDataDF = sqlContext
      .read.format( "com.databricks.spark.csv" )
      .option( "header", "true" )
      .option( "inferSchema", "true" )
      .load( path )


    rawDataDF.show()

  }

}