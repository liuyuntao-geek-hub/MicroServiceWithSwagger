package com.anthem.hca.splice.helper

import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.spark.sql.DataFrame
import grizzled.slf4j.Logging
import org.joda.time.DateTime

trait Operator extends Logging {

  //The following Functions need to be override on the operation detail
  def extractData(): Map[String, DataFrame]
  def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame]
  def loadData(outDFs: Map[String, DataFrame]): Unit

  def extractDataWithABC(): Map[String, DataFrame] = {
    beforeExtractData()
    val data = extractData()
    afterExtractData()
    (data)
  }

  def transformDataWithABC(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    beforeTransformData
    val resultDF = transformData(inDFs)
    afterTransformData
    (resultDF)
  }

  def loadDataWithABC(outDFs: Map[String, DataFrame]): Unit = {
    beforeLoadData
    loadData(outDFs)
    afterLoadData
  }

  def operation(): Unit = {
    loadDataWithABC(transformDataWithABC(extractDataWithABC()));
  }

  def beforeExtractData(): Unit

  def afterExtractData(): Unit = {
    info("*********** End Extracting Data **************")
  }

  def beforeTransformData(): Unit = {
    info("*********** Start Transform Data **************")
    info("[SPLICE-RDBMS_CONNECTOR] Transform Start time: " + DateTime.now)
  }

  def afterTransformData(): Unit = {
    info("[SPLICE-RDBMS_CONNECTOR] Transform End time: " + DateTime.now)
    info("*********** End Transform Data **************")
  }

  def beforeLoadData(): Unit = {
    info("*********** Start load Data **************")
  }

  def afterLoadData(): Unit
}

