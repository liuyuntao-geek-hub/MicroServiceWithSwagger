package com.anthem.hpip.Template

import java.io.File

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{current_timestamp, lit}

/**
  * Created by yuntliu on 1/20/2018.
  */
class TemplateOperation(confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("OFF")
  val firstTableName=config.getString("firstTableName")
  val secondTableName=config.getString("secondTableName")
  def loadData(): Map[String, DataFrame] = {

    val path = config.getString("inputFilePath")+File.separator+config.getString("firstFileName")
    println("Path:" + path)
    var inputFieldsList:String =  config.getString("inputColumnList")
    var inputFieldsListArray:Array[String]=inputFieldsList.split(",")
    val testDF = spark
      .read.format( "com.databricks.spark.csv" )
      .option( "header", "true" )
      .option( "inferSchema", "true" )
      .load( path )

    testDF.show(50)

    val newTestDF = testDF.toDF(inputFieldsListArray: _*)



    var dataMap = Map(firstTableName->newTestDF)

    return dataMap
  }


  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val df1 = inDFs.getOrElse(firstTableName, null)

    val lastUpdatedDate = config.getString("audit-column-name").toLowerCase
    val df1WithAuditColumn = df1.withColumn(lastUpdatedDate, lit(current_timestamp()))


    var dataMap = Map(firstTableName->df1WithAuditColumn)
    return dataMap
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val df1 = outDFs.getOrElse(firstTableName, null)

    df1.show()

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
