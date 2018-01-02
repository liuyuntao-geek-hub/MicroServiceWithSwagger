package com.deloitte.demo.prototype.monad

/**
  * Created by yuntliu on 10/30/2017.
  */

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

object monadWrapper {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster( "local[5]" ).setAppName( "SpendMonad" )
    val sc = new SparkContext( conf )
    val sqlContext = new SQLContext( sc )
    // val hiveContext = new HiveContext( sc )
    sc.setLogLevel( "OFF" )
    System.setProperty("hadoop.home.dir", "C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\libs\\winutils")


    val f = for {
      spendDF <- HiveTableLoad( "Select * from Table" )
      processDF <- ProcessSpend( spendDF )
      writeDF <- WriteSpend( processDF )

    } yield {
      println( "DONE" )
    }

    f.run( sqlContext )
  }

}
