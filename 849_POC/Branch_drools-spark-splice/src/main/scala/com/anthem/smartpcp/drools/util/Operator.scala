package com.anthem.smartpcp.drools.util

import java.text.SimpleDateFormat
import org.apache.spark.sql.DataFrame
import grizzled.slf4j.Logging
import java.util.Calendar

trait Operator extends Logging {

  val DATE_FORMAT = "yyyy-MM-dd-HH:mm:sss";
  var sdf = new SimpleDateFormat(DATE_FORMAT);

  //The following Functions need to be override on the operation detail
  def loadData(): Map[String, DataFrame]
  def writeData(outDFs: Map[String, DataFrame]): Unit
  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame]

  def beforeLoadData(): Unit = {}
  def afterWriteData(): Unit = {}

  def afterLoadData(): Unit = {
    info("*********** End Loading Data **************")
  }

  def loadDataWithABC(): Map[String, DataFrame] = {
    beforeLoadData()
    val data = loadData()
    afterLoadData()
    return data;
  }

  def beforeWriteData(): Unit = {
    info("*********** Start Write Data **************")
  }

  def writeDataWithABC(outDFs: Map[String, DataFrame]): Unit = {
    beforeWriteData
    writeData(outDFs)
    afterWriteData
  }

  def beforeProcData(): Unit = {
    info("*********** Start Proc Data **************")
    info(s"Proc Start time: " + sdf.format(Calendar.getInstance().getTime()))
  }

  def afterProcData(): Unit = {
    info(s"Proc End time: " + sdf.format(Calendar.getInstance().getTime()))
    info("*********** End Proc Data **************")
  }

  def processDataWithABC(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    beforeProcData
    val resultDF = processData(inDFs)
    afterProcData
    return resultDF
  }

  def operation(): Unit = {
    writeDataWithABC(processDataWithABC(loadDataWithABC()));
  }

}