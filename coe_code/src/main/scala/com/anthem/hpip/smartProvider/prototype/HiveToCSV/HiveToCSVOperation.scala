package com.anthem.hpip.smartProvider.prototype.HiveToCSV

/**
  * Created by yuntliu on 12/6/2017.
  */

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame

/**
  * Created by yuntliu on 11/30/2017.
  */
class HiveToCSVOperation (confFilePath: String, env: String,queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("OFF")

  def loadData(): Map[String, DataFrame] = {
    return Map(("df", loadHiveTableData()))
  }

  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    return inDFs;
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {
    outDFs.head._2.coalesce(1).write.mode("overwrite").format( "com.databricks.spark.csv" )
      .option( "header", "true" ).save(config.getString("csvSaveFilePath") + config.getString("csvSaveFileName"))

  }
  def loadHiveTableData(): DataFrame = {
    //Reading the data into Data frames
    println("Reading the data into Data frames")
    val clmQuery = config.getString("csvHiveQuery").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    println(clmQuery)
    println("Reading the queries from config file")
    val clmDF = hiveContext.sql(clmQuery).repartition(200)
    clmDF.printSchema()

    clmDF
  }

}