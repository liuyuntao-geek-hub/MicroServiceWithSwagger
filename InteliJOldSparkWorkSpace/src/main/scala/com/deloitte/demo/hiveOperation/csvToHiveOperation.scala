package com.deloitte.demo.hiveOperation

import com.deloitte.demo.framework.{OperationSession, Operator, StringKey}
import org.apache.spark.sql.DataFrame

/**
  * Created by yuntliu on 11/30/2017.
  */
class csvToHiveOperation (override val AppName:String, override val master:String
                         ) extends OperationSession(AppName,master, None)  with Operator {

  val dataSetString1 = "NewData1"
  val dataSetString2 = "NewData2"
    override def loadData():Map[String,org.apache.spark.sql.DataFrame]= {

      var allDataMap: Map[String, org.apache.spark.sql.DataFrame] = null

      if (exDocReader.getString("env.name").equalsIgnoreCase(StringKey.SnetServer)) {




        allDataMap = Map(
          dataSetString1 -> hiveSqlContext.sql("Select * from devTest.SimpleTest")
          ,
          dataSetString2 -> hiveSqlContext.sql("Select * from devTest.SimpleTest"))
      }
      else {

        val newData1 = List(
          ("a", 1),
          ("b", 2),
          ("c", 3),
          ("d", 4),
          ("e", 5)
        )
        val newData2 = List(
          ("a", 10),
          ("b", 20),
          ("c", 30),
          ("d", 40),
          ("e", 50)
        )
        allDataMap = Map(
          dataSetString1 -> sqlContext.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1, x._2))).toDF("id", "number")
          ,
          dataSetString2 -> sqlContext.createDataFrame(newData2).toDF("id", "number"))
      }

      return allDataMap
  }

  override def writeData(outDFs:Map[String,DataFrame]):Unit= {

    outDFs.head._2.coalesce(1).write.mode("overwrite").format( "com.databricks.spark.csv" )
      .option( "header", "true" ).save(exDocReader.getString("env.SaveFilePath") + exDocReader.getString("env.SaveFileName"))

  }

  override def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={

    return inDFs;
  }
}
