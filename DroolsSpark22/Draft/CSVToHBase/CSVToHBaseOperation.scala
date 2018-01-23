package com.anthem.hpip.JumpStart.prototype.CSVToHBase


import java.io.File

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{current_timestamp, lit}
import org.apache.hadoop.hbase.client.{HTable, Put}
import org.apache.hadoop.hbase.util.Bytes
/**
  * Created by yuntliu on 12/6/2017.
  */
class CSVToHBaseOperation (confFilePath: String, env: String,queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

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


  override def writeData(outDFs:Map[String,DataFrame]):Unit= {

    outDFs.head._2.show()
    val hTable = new HTable(configHB, config.getString("HBaseTable"))
    for (i <- 1 to 5) {

      val myArray = outDFs.head._2.map(row => (row.getString(row.fieldIndex("first_name"))
        , row.getString(row.fieldIndex("last_name"))
        , row.getString(row.fieldIndex("street"))
        , row.getString(row.fieldIndex("city"))
        , row.getString(row.fieldIndex("state"))
        , row.getInt(row.fieldIndex("zip"))
        , row.getString(row.fieldIndex("phone"))
        , row.getString(row.fieldIndex("email"))
        , row.getString(row.fieldIndex("first_name")) + "_" + row.getLong(row.fieldIndex("rowID")) + "_" + i
      )).toArray()

      for (value<-myArray)
      {

        val p = new Put(Bytes.toBytes(value._9))
        p.add(Bytes.toBytes("personalID"), Bytes.toBytes("first_name"), Bytes.toBytes(value._1))
        p.add(Bytes.toBytes("personalID"), Bytes.toBytes("last_name"), Bytes.toBytes(value._2))
        p.add(Bytes.toBytes("personalID"), Bytes.toBytes("street"), Bytes.toBytes(value._3))
        p.add(Bytes.toBytes("personalID"), Bytes.toBytes("city"), Bytes.toBytes(value._4))
        p.add(Bytes.toBytes("personalID"), Bytes.toBytes("state"), Bytes.toBytes(value._5))
        p.add(Bytes.toBytes("personalID"), Bytes.toBytes("zip"), Bytes.toBytes(value._6.toString))
        p.add(Bytes.toBytes("personalID"), Bytes.toBytes("phone"), Bytes.toBytes(value._7))
        p.add(Bytes.toBytes("personalContact"), Bytes.toBytes("email"), Bytes.toBytes(value._8))
        // Saving the put Instance to the HTable.
        hTable.put(p)
        hTable.flushCommits()
        println("Insert: " + value._9 + "....... Done")
      }
    }
    hTable.close()

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
