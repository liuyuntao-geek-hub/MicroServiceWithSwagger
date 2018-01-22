package com.anthem.hpip.asoPricing

import java.io.File

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{current_timestamp, lit}

/**
  * Created by yuntliu on 11/19/2017.
  */
class ASOPOperation (confFilePath: String, env: String,queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

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



   var dataMap = Map(firstTableName->newTestDF, secondTableName->loadHiveTableData())

    return dataMap
  }


  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val df1 = inDFs.getOrElse(firstTableName, null)
    val df2 = inDFs.getOrElse(secondTableName, null)
    val lastUpdatedDate = config.getString("audit-column-name").toLowerCase
    val df1WithAuditColumn = df1.withColumn(lastUpdatedDate, lit(current_timestamp()))
    val df2WithAuditColumn = df2.withColumn(lastUpdatedDate, lit(current_timestamp()))

    var dataMap = Map(firstTableName->df1WithAuditColumn, secondTableName->df2WithAuditColumn)
    return dataMap
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val df1 = outDFs.getOrElse(firstTableName, null)
    val df2 = outDFs.getOrElse(secondTableName, null)
    df1.show()
    df2.show()



    //Writing the data to a table in Hive
    println("Writing the data to a table in Hive")
    //Looping for map of data frames
    println("Looping for map of data frames")

    outDFs.foreach(x => {
      println("-------Inside For loop--------")

      val hiveDB = config.getString("inbound-hive-db")
      val warehouseHiveDB = config.getString("warehouse-hive-db")
      println("hiveDB is " + hiveDB)
      println("warehouseHiveDB is " + warehouseHiveDB)

      val tablename = x._1
      println("tablename is" + tablename)

      //Displaying the sample of data
      val df = x._2
      printf("Showing the contents of df")
      df.printSchema()
      df.show(false)
      //Truncating the previous table created
      println("Truncating the previous table created")
      spark.sql("ALTER TABLE " + warehouseHiveDB + """.""" + tablename+" SET TBLPROPERTIES('EXTERNAL'='FALSE')")
      spark.sql("truncate table " + warehouseHiveDB + """.""" + tablename)


      var partitionColumn1 = ""

      if (tablename.equalsIgnoreCase(firstTableName))
      {
        partitionColumn1 = config.getString("firstTable_partition_col").toLowerCase()
      }
      else
      {
        partitionColumn1 = config.getString("secondTable_partition_col").toLowerCase()
      }

      //Creating the table in Hive
      spark.conf.set("hive.exec.dynamic.partition", "true")
      spark.conf.set("hive.exec.dynamic.partition.mode", "nonstrict")
      //df.write.mode("overwrite").partitionBy(partitionColumn).insertInto(warehouseHiveDB + """.""" + tablename)
      df.write.mode("overwrite").insertInto(warehouseHiveDB + """.""" + tablename)

      spark.sql("ALTER TABLE " + warehouseHiveDB + """.""" + tablename+" SET TBLPROPERTIES('EXTERNAL'='TRUE')")
      println("Table created as " + tablename)

    })

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
