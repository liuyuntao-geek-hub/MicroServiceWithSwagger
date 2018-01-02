package com.deloitte.demo.hiveOperation

import com.deloitte.demo.framework.{OperationSession, Operator, StringKey}
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.{DataFrame, SaveMode}

/**
  * Created by yuntliu on 11/8/2017.
  */
class hiveWriteOperation (override val AppName:String, override val master:String
                         ) extends OperationSession(AppName,master, None)  with Operator {

  val dataSetString1 = "NewData1"
  val dataSetString2 = "NewData2"
  override def loadData():Map[String,org.apache.spark.sql.DataFrame]= {


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
    var allDataMap: Map[String, org.apache.spark.sql.DataFrame] = null


    if (exDocReader.getString("env.name").equalsIgnoreCase(StringKey.SnetServer)) {
      allDataMap = Map(
        dataSetString1 -> hiveSqlContext.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1, x._2))).toDF("id", "number")
        ,
        dataSetString2 -> hiveSqlContext.createDataFrame(newData2).toDF("id", "number"))
    }
    else {
      allDataMap = Map(
        dataSetString1 -> sqlContext.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1, x._2))).toDF("id", "number")
        ,
        dataSetString2 -> sqlContext.createDataFrame(newData2).toDF("id", "number"))
    }

    return allDataMap

  }
  override def writeData(outDFs:Map[String,DataFrame]):Unit={
    val df=outDFs.getOrElse(dataSetString1, null)



    if (exDocReader.getString("env.name").equalsIgnoreCase(StringKey.SnetServer))
    {
      //var createDB = "CREATE DATABASE IF NOT EXISTS devTest"
      var createDB = docReader.getString("hiveCreateSQL").replaceAll("<dbname>",exDocReader.getString("DB.dbname"))

      println("createDB String:" + createDB)

      hiveSqlContext.sql(createDB)
      var createSQL = "CREATE TABLE IF NOT EXISTS devTest.SimpleTest (id String , number INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' STORED AS TEXTFILE"
      hiveSqlContext.sql(createSQL)
      df.registerTempTable("TestTable")
      hiveSqlContext.sql("Select * from TestTable").show()
      hiveSqlContext.sql("INSERT INTO TABLE devTest.SimpleTest select * from TestTable")
      val df2=outDFs.getOrElse(dataSetString1, null)
      df2.write.mode(SaveMode.Append).saveAsTable("devTest.SimpleTest")
      hiveSqlContext.sql("Select * from devTest.SimpleTest").show()
    }
    var createDB = docReader.getString("hiveCreateSQL").replaceAll("<dbname>",exDocReader.getString("DB.dbname"))

    println("createDB String:" + createDB)

    df.show()
  }

  override def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={

    return inDFs;
  }

}
