package com.anthem.hca.spcp.util

import org.apache.spark.sql.functions.udf

import com.fasterxml.jackson.databind.ObjectMapper

import scalaj.http.Http
import scalaj.http.HttpResponse

object SPCPUDFs {

  //Checking if the threshold is crossed for the variance
  def checkThreshold = udf((x: Double, y: Double) => {
    if (x.isNaN() || x > y || x < -y) {
      ("Yes")
    } else {
      ("No")
    }
  })

  def geoCodeUDF = udf((stateCode: String, locality: String, postalCode: String, addressLine: String,
    bingUrl: String, bingkey: String) => {

    val requestMap = Map(
      "countryRegion" -> "US",
      "adminDistrict" -> stateCode,
      "locality" -> locality,
      "postalCode" -> postalCode,
      "addressLine" -> addressLine,
      "key" -> bingkey,
      "output" -> "json")

    try {

      /** The socket connection and read timeouts in milliseconds. Defaults are 1000 and 5000 respectively */
      val result: HttpResponse[String] = Http(bingUrl).timeout(2000, 10000)
        .params(requestMap)
        .asString

      val mapper = new ObjectMapper

      val arrNode = mapper.readTree(result.body)
      if (null != arrNode && arrNode.get(SPCPConstants.BING_RESOURCE_SETS).size > 0 && arrNode.get(SPCPConstants.BING_RESOURCE_SETS).get(0).get(SPCPConstants.BING_RESOURCES).size > 0) {
        val node = arrNode.get(SPCPConstants.BING_RESOURCE_SETS).get(0).get(SPCPConstants.BING_RESOURCES).get(0).get(SPCPConstants.BING_POINT).get(SPCPConstants.BING_COORDINATES)
        (node.get(0).doubleValue.toString + " ," + node.get(1).doubleValue.toString)
      }else{
         ("0.0" + "," + "0.0")
      }
    } catch {
      case t: Throwable =>
        t.printStackTrace()
       ("0.0" + "," + "0.0")
      
    }
  })


}