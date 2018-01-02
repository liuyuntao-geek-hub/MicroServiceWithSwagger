package com.deloitte.demo.hiveToHBase

import com.deloitte.demo.framework.{OperationSession, Operator, StringKey}
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{HTable, Put}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.monotonicallyIncreasingId

/**
  * Created by yuntliu on 12/5/2017.
  */
class hiveToHBaseOperation (override val AppName:String, override val master:String
                           ) extends OperationSession(AppName,master, None)  with Operator {



  override def loadData():Map[String,org.apache.spark.sql.DataFrame]= {
    val path = exDocReader.getString("env.inputFilePath")+docReader.getString("firstFileName")
    println("Path:" + path)

    var inputFieldsList:String = "source_rowID,first_name,last_name,street,city,state,zip,phone,email,source,rowID"
    var inputFieldsListArray:Array[String]=inputFieldsList.split(",")

    val testDF = sqlContext
      .read.format( "com.databricks.spark.csv" )
      .option( "header", "true" )
      .option( "inferSchema", "true" )
      .load( exDocReader.getString("env.inputFilePath")+docReader.getString("firstFileName") ).toDF(inputFieldsListArray: _*)
    testDF.show(50)

    val testDF2 = sqlContext
      .read.format( "com.databricks.spark.csv" )
      .option( "header", "true" )
      .option( "inferSchema", "true" )
      .load( exDocReader.getString("env.inputFilePath")+docReader.getString("secondFileName") ).toDF(inputFieldsListArray: _*)
    testDF2.show(50)

    var dataMap = Map("FirstDataFrame"->testDF, "SecondDataFrame"->testDF2)
    return dataMap

  }


  override def writeData(outDFs:Map[String,DataFrame]):Unit= {

    outDFs.head._2.show()
    val hTable = new HTable(configHB, "Anthem_PADP:customer")
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

  override def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={
    return inDFs;
  }

}
