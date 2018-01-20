package com.anthem.hpip.smartProvider.prototype.CSVToHive

import java.io.File

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{current_timestamp, lit}

/**
  * Created by yuntliu on 12/6/2017.
  */
class CSVToHiveOperation (confFilePath: String, env: String,queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("OFF")
  def loadData(): Map[String, DataFrame] = {

    val path = config.getString("inputFilePath")+File.separator+config.getString("firstFileName")
    println("Path:" + path)
    var inputFieldsList:String =  config.getString("inputColumnList")
    var inputFieldsListArray:Array[String]=inputFieldsList.split(",")
    val testDF = hiveContext
      .read.format( "com.databricks.spark.csv" )
      .option( "header", "true" )
      .option( "inferSchema", "true" )
      .load( path )

    testDF.show(50)

    val newTestDF = testDF.toDF(inputFieldsListArray: _*)

    var dataMap = Map("zz_phmp_customer"->newTestDF, "zz_phmp_mtclm"->loadHiveTableData())

    return dataMap
  }

  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val df1 = inDFs.getOrElse("zz_phmp_customer", null)
    val df2 = inDFs.getOrElse("zz_phmp_mtclm", null)
    val lastUpdatedDate = config.getString("audit-column-name").toLowerCase
    val df1WithAuditColumn = df1.withColumn(lastUpdatedDate, lit(current_timestamp()))
    val df2WithAuditColumn = df2.withColumn(lastUpdatedDate, lit(current_timestamp()))

    var dataMap = Map("zz_phmp_customer"->df1WithAuditColumn, "zz_phmp_mtclm"->df2WithAuditColumn)
    return dataMap
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val df1 = outDFs.getOrElse("zz_phmp_customer", null)
    val df2 = outDFs.getOrElse("zz_phmp_mtclm", null)
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
      hiveContext.sql("ALTER TABLE " + warehouseHiveDB + """.""" + tablename+" SET TBLPROPERTIES('EXTERNAL'='FALSE')")
      hiveContext.sql("truncate table " + warehouseHiveDB + """.""" + tablename)


      var partitionColumn1 = ""

      if (tablename.equalsIgnoreCase("zz_phmp_customer"))
      {
        partitionColumn1 = config.getString("zz_phmp_customer_partition_col").toLowerCase()
      }
      else
      {
        partitionColumn1 = config.getString("zz_phmp_mtclm_partition_col").toLowerCase()
      }

      //Creating the table in Hive
      hiveContext.setConf("hive.exec.dynamic.partition", "true")
      hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")
      df.write.mode("overwrite").partitionBy(partitionColumn1).insertInto(warehouseHiveDB + """.""" + tablename)
      hiveContext.sql("ALTER TABLE " + warehouseHiveDB + """.""" + tablename+" SET TBLPROPERTIES('EXTERNAL'='TRUE')")
      println("Table created as " + tablename)

    })

  }

  def loadHiveTableData(): DataFrame = {
    //Reading the data into Data frames
    println("Reading the data into Data frames")
    val clmQuery = config.getString("query_clm").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    println(clmQuery)
    println("Reading the queries from config file")
    val clmDF = hiveContext.sql(clmQuery).repartition(200)
    clmDF.printSchema()

    clmDF
  }

}
