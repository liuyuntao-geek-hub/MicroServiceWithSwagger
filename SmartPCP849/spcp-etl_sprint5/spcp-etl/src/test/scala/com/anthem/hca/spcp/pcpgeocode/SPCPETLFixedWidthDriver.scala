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

case class Address(stateCode: String, locality: String, postalCode: String, addressLine: String)

object SPCPETLDriver {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local[*]").appName("bing test").getOrCreate()
    import spark.implicits._
   
    val addressList = List(Address("CA", "Los Angeles", "90211", "8767 WILSHIRE BLVD FL 2"))
    val addrsDF = addressList.toDF()

    addrsDF.printSchema()
    addrsDF.show

    val addrsDF1 = addrsDF.withColumn("latitude_longitude", SPCPUDFs.geoCodeUDF($"stateCode", $"locality",
        $"postalCode", $"addressLine")).cache()
    addrsDF1.show
    addrsDF1.printSchema()
    val addressDF2 = addrsDF1.select($"stateCode", $"locality", $"postalCode", $"addressLine",
        expr("split(latitude_longitude,',')[0]").as("latitude").cast(DoubleType),
        expr("split(latitude_longitude,',')[1]").as("longitude").cast(DoubleType) )
        
    addressDF2.show
    addressDF2.printSchema()

  }
}