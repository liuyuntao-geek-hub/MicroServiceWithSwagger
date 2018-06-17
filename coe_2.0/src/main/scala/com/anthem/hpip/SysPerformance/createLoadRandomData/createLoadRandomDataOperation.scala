package com.anthem.hpip.SysPerformance.createLoadRandomData

import java.io.File
import java.util.UUID
import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{current_timestamp, lit}

/**
  * Created by yuntliu on 05/26/2018.
  */
class createLoadRandomDataOperation (confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("info")
  val firstTableName=config.getString("randomTB")

  def loadData(): Map[String, DataFrame] = {



    var uuid: String = null;
     var newData1 = List((0L,""));
    for (i<-1L to config.getLong("uuidCount"))
    {
      uuid = UUID.randomUUID().toString()
      newData1 = newData1:::List((i,uuid))
    }
    newData1=newData1.drop(1)
    
     val allDataMap = Map(
      firstTableName -> spark.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1,x._2)) ).toDF("id_key", "random_string")
    )


    return allDataMap
  }


  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    return inDFs
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val df1 = outDFs.getOrElse(firstTableName, null)
    
    df1.show()
//***** enable when write to Hive + disable if test on local *******//
    
   df1.write.mode("overwrite").option("truncate", "true").insertInto(firstTableName)

    
  }

  
  
  def loadHiveTableData(): DataFrame = {
    //Reading the data into Data frames
    println("Reading the data into Data frames")
    val clmQuery = config.getString("query_clm").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    println(clmQuery)
    println("Reading the queries from config file")
    val clmDF = spark.sql(clmQuery).repartition(200)
    clmDF.printSchema()

    clmDF
  }

}
