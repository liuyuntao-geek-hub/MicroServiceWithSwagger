package com.anthem.hca.spcp.pcpgeocode

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions.expr

import com.anthem.hca.spcp.util.SPCPConstants
import com.fasterxml.jackson.databind.ObjectMapper

import scalaj.http.Http
import scalaj.http.HttpResponse
import com.anthem.hca.spcp.util.SPCPUDFs
import org.apache.spark.sql.types.DoubleType
import com.typesafe.config.ConfigFactory
import java.io.File

//case class Address(stateCode: String, locality: String, postalCode: String, addressLine: String)

object SPCPETLFixedWidthDriver {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local[*]").appName("bing test").getOrCreate()
    import spark.implicits._
    
    val conf = ConfigFactory.parseFile(new File("C:\\Users\\af30986\\git\\spcp-etl\\src\\test\resources\\pcp_schema.conf"))
    
    val pcpAdrsRDD = spark.sparkContext.textFile("C:\\Users\\af30986\\git\\spcp-etl\\data\\PCPADRS.TXT")
    
    println(pcpAdrsRDD.count())
    
    
    
    
   

  }
}