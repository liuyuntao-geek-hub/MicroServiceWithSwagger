package com.anthem.hpip.SysPerformance.SparkSQLSolution


import java.io.File

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{current_timestamp, lit}
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.Seconds

/*
  * Created by yuntliu on 1/20/2018.
*/

class SparkSQLSolutionOperation (confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("info")
  val sqlTargetTB=config.getString("sqlTargetTB")
  val secondTableName=config.getString("secondTableName")
  var startTime = DateTime.now
  def loadData(): Map[String, DataFrame] = {

    startTime = DateTime.now
    var dataMap = Map(sqlTargetTB->loadHiveTableData())

    return dataMap
  }


  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    return inDFs
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val df1 = outDFs.getOrElse(sqlTargetTB, null)
   // Thread.sleep(10000)
    //df1.show()
    df1.write.mode("overwrite").option("truncate", "true").insertInto(sqlTargetTB)
    
		val endTime = DateTime.now
		 info(" [COE 2.0 Spark SQL] Start Time: "+ startTime);
		 info(" [COE 2.0 Spark SQL] End Time: " + endTime)
     println(" [COE 2.0 Spark SQL] Minutes diff: " + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
     info (" [COE 2.0 Spark SQL] Seconds Diff: " + Seconds.secondsBetween(startTime, endTime).getSeconds + " seconds. ")
  	
     println(" [COE 2.0 Spark SQL] Start Time: "+ startTime);
		 println(" [COE 2.0 Spark SQL] End Time: " + endTime)   
     println (" [COE 2.0 Spark SQL] Seconds Diff: " + Seconds.secondsBetween(startTime, endTime).getSeconds + " seconds. ")
        
    
  }

  def loadHiveTableData(): DataFrame = {
    //Reading the data into Data frames
    println("Reading the data into Data frames")
    val clmQuery = config.getString("query_sqlToTargetTB")
    println(clmQuery)
    println("Reading the queries from config file")
    val clmDF = spark.sql(clmQuery).repartition(200)
    clmDF.printSchema()

    clmDF
  }

}


