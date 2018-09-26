package com.anthem.hca.spcp.util

import org.apache.spark.sql.functions.udf

import com.fasterxml.jackson.databind.ObjectMapper

import scalaj.http.Http
import scalaj.http.HttpResponse

object SPCPUDFs {

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

    val result: HttpResponse[String] = Http(bingUrl)
      .params(requestMap)
      .asString

    println("result body is " + result.body)

    val mapper = new ObjectMapper

    val arrNode = mapper.readTree(result.body)
    if (null != arrNode && arrNode.get(SPCPConstants.BING_RESOURCE_SETS).size > 0 && arrNode.get(SPCPConstants.BING_RESOURCE_SETS).get(0).get(SPCPConstants.BING_RESOURCES).size > 0) {
      val node = arrNode.get(SPCPConstants.BING_RESOURCE_SETS).get(0).get(SPCPConstants.BING_RESOURCES).get(0).get(SPCPConstants.BING_POINT).get(SPCPConstants.BING_COORDINATES)
      println(node.get(0).doubleValue)
      println(node.get(1).doubleValue)
      (node.get(0).doubleValue + " ," + node.get(1).doubleValue.toString)
    } else {
      //TODO need to through the run time exception, if we are not getting the lat long we will check for typos in address line text
      ("0.0" + "," + "0.0")
    }
  })

}