package com.anthem.hpip.SysPerformance.SparkDataFrameSolution

import java.io.File

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{current_timestamp, lit}
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.Seconds

import org.apache.spark.sql.functions._

/*
  * Created by yuntliu on 1/20/2018.
*/


class SparkDataFrameSolutionOperation (confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("info")
  val srcTableA=config.getString("srcTableA")
  val srcTableB=config.getString("srcTableB")
  val sqlTargetTB=config.getString("sqlTargetTB")
  var startTime = DateTime.now
  def loadData(): Map[String, DataFrame] = {

    startTime = DateTime.now
    var dataMap = Map(srcTableA->loadHiveTableData("query_srcTableA"), srcTableB->loadHiveTableData("query_srcTableB") )

    return dataMap
  }


  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    val DFa:DataFrame = inDFs.getOrElse(srcTableA, null)
    val DFb:DataFrame = inDFs.getOrElse(srcTableB, null)
    import spark.implicits._
     
    val DFGa = DFa.groupBy("random_string").agg(org.apache.spark.sql.functions.first("id_key"),first("random_number")).toDF("a_random_string","a_id_key","a_random_number")
    val DFGb = DFb.groupBy("random_string").agg(org.apache.spark.sql.functions.first("id_key"),first("random_number")).toDF("b_random_string","b_id_key","b_random_number")
        
    val DF = DFGa.join(DFGb, $"a_random_string" === $"b_random_string", "inner").select("a_random_string","b_id_key", "a_id_key", "b_random_number", "a_random_number").toDF("MatchingString","b_id_key", "a_id_key", "b_random_number", "a_random_number").withColumn("last_updt_dtm", lit(current_timestamp()))

    
    //DF.show();
    
    var dataMap = Map(sqlTargetTB->DF)
    return dataMap
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val DF = outDFs.getOrElse(sqlTargetTB, null)
   // val DFb = outDFs.getOrElse(srcTableB, null)
   // Thread.sleep(10000)
   // DF.show()
   // DFb.show()
   DF.write.mode("overwrite").option("truncate", "true").insertInto(sqlTargetTB)
    
		val endTime = DateTime.now
		 info(" [COE 2.0 Spark DataFrame ] Start Time: "+ startTime);
		 info(" [COE 2.0 Spark DataFrame] End Time: " + endTime)
     println(" [COE 2.0 Spark DataFrame] Minutes diff: " + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
     info (" [COE 2.0 Spark DataFrame] Seconds Diff: " + Seconds.secondsBetween(startTime, endTime).getSeconds + " seconds. ")
  	
     println(" [COE 2.0 Spark DataFrame] Start Time: "+ startTime);
		 println(" [COE 2.0 Spark DataFrame] End Time: " + endTime)   
     println (" [COE 2.0 Spark DataFrame] Seconds Diff: " + Seconds.secondsBetween(startTime, endTime).getSeconds + " seconds. ")
        
    
  }
  
  

  def loadHiveTableData(sqlString: String): DataFrame = {
    //Reading the data into Data frames
    println("Reading the data into Data frames")
    val clmQuery = config.getString(sqlString)
    println(clmQuery)
    println("Reading the queries from config file")
    val clmDF = spark.sql(clmQuery).repartition(200)
    
    
    
    clmDF.printSchema()

    clmDF
  }

}


