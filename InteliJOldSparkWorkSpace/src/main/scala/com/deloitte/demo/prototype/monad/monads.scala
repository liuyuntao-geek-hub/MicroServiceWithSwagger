package com.deloitte.demo.prototype.monad

/**
  * Created by yuntliu on 10/30/2017.
  */

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.sql.hive.HiveContext


// need to be case class => only way to extends from trait
case class HiveTableLoad(query: String) extends PADPMonad[DataFrame] {
  override def run(sqlContext: SQLContext): DataFrame = {
    println("this is load monad")
    //sqlContext.sql(query)
    val dataSourceDirectory = System.getProperty("user.dir") + "/data/"
    val testDF = sqlContext
      .read.format("com.databricks.spark.csv") //added
      .option("header", "true")
      .option("inferSchema", "true")
      .load(dataSourceDirectory+"testdata_combined.csv")
    println("Data Directory:" + dataSourceDirectory)
    testDF.show()
    testDF
  }
}

case class ProcessSpend(df: DataFrame) extends PADPMonad[DataFrame] {
  override def run(sqlContext: SQLContext): DataFrame = {
    println("this is process monad")
    df.show()
    df
  }
}

case class WriteSpend(df: DataFrame) extends PADPMonad[Unit] {
  override def run(sqlContext: SQLContext): Unit = {
    println("this is write monad")
    df.show()
  }
}
