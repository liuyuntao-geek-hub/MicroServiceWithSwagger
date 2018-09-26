package com.anthem.hca.spcp.pcpgeocode

import com.fasterxml.jackson.databind.ObjectMapper
	import org.apache.spark.sql.SparkSession
	
	import scalaj.http.Http
	import scalaj.http.HttpResponse
	import com.anthem.hca.spcp.util.SPCPConstants
	
	object SPCPETLDriver2 {
	
	  def main(args: Array[String]): Unit = {
	
	    val spark = SparkSession.builder().master("local[*]").appName("bing test").getOrCreate()
	
	
	    val url = "http://dev.virtualearth.net/REST/v1/Locations"
	    
//	    spark.udf.register("geoCode", (stateCode: String, addressLine: String,) => (())
	
	    val requestMap = Map(
	      "countryRegion" -> "US",
	      "adminDistrict" -> "CA",
	      "locality" -> " Los Angeles",
	      "postalCode"->"90211",
	      "addressLine" -> "8767 WILSHIRE BLVD FL 2",
	      "key" -> "AihX02iGBexb_-Z7ZjC0-SsvOV-gQlu4HEnoC5AsnD1mdmUFEVJfgpg0_7OzFKaJ",
	      "output" -> "json"
	    )
	    val result: HttpResponse[String] = Http(url) 
	      .params(requestMap)
	      .asString
	
	    println("reuslt is " + result)
	    println("result body is " + result.body)
	    println()
	
	    val mapper = new ObjectMapper
	
	    val arrNode = mapper.readTree(result.body)
	    if (null != arrNode && arrNode.get(SPCPConstants.BING_RESOURCE_SETS).size > 0 && arrNode.get(SPCPConstants.BING_RESOURCE_SETS).get(0).get(SPCPConstants.BING_RESOURCES).size > 0) {
	      //      val memberAddress = member.getMemberAddress
	      val node = arrNode.get(SPCPConstants.BING_RESOURCE_SETS).get(0).get(SPCPConstants.BING_RESOURCES).get(0).get(SPCPConstants.BING_POINT).get(SPCPConstants.BING_COORDINATES)
	      println(node.get(0).doubleValue)
	      println(node.get(1).doubleValue)
	      //      memberAddress.setLatitude(node.get(0).doubleValue)
	      //      memberAddress.setLongitude(node.get(1).doubleValue)
	    }
	
	  }
	
	}