package com.deloitte.demo.sasLogic

import com.deloitte.demo.framework._
import org.apache.spark.sql.DataFrame
/*import grizzled.slf4j._
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.apache.log4j.Logger
/**
  * Created by yuntliu on 11/8/2017.
  */
class wordCountOperation (override val AppName:String, override val master:String
                         ) extends OperationSession(AppName,master, None)  with Operator {

  // In subClass: Logger not working
 @transient
  lazy private val logger:Logger = Logger.getLogger(classOf[wordCountOperation])
  def loadData():Map[String,org.apache.spark.sql.DataFrame]={


    val path = exDocReader.getString("env.inputFilePath")+docReader.getString("inputFileName")
    println("Path:" + path)

    val testDF = sqlContext
    .read.format( "com.databricks.spark.csv" )
      .option( "header", "true" )
      .option( "inferSchema", "true" )
      .load( path )

    testDF.show()
   // rootlogger.error("wordcount")
    logger.warn("wordcount - data load")
    return Map("First DataFrame"->testDF)
  }

  override def writeData(outDFs:Map[String,DataFrame]):Unit={
    println("============== Frist Item to Write ===========")

    outDFs.head._2.show()
    println("================= Write All ===================")
    outDFs.foreach(x=>{
      println("---------- Item: " + x._1 + "-----------------")
      x._2.show()
    })
    }

 override def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={
   val df = inDFs.head._2;
   val testDF1 = df.rdd.map(row=>{(row.get(1).toString+","+row.get(2).toString)}).map(name=>(name,1)).reduceByKey(_+_)
   return Map("output Result"->sqlContext.createDataFrame(testDF1).toDF("name","count"));
  }


}
