package com.deloitte.demo.framework

/**
  * Created by yuntliu on 11/7/2017.
  */

import java.text.SimpleDateFormat
import java.util.Calendar

//import grizzled.slf4j.Logger
import org.apache.spark.sql.DataFrame
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
/**
  * Created by yuntliu on 10/11/2017.
  */
trait Operator {
// In trait the logger does not work
  @transient lazy private val log = org.apache.log4j.LogManager.getLogger("TraitOperator")

  // @transient private lazy val log = Logger("TraitOperator")

 // @transient private lazy val log = LoggerFactory.getLogger("TraitOperator")

  val DATE_FORMAT = "yyyy-MM-dd-HH:mm:ss";
  var sdf = new SimpleDateFormat(DATE_FORMAT);


  //The following Functions need to be override on the operation detail
  def loadData():Map[String,org.apache.spark.sql.DataFrame];
  def writeData(outDFs:Map[String,DataFrame]):Unit;
  def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame];
  // check out the sample at package programTemplate

  // This section define the AOP for Load
  ///////////////////// ************* Change For ABC *************** //////////////////
  def StartABCInit(): Unit;
  ///////////////////// ************* Change For ABC *************** //////////////////
  def beforeLoadData():Unit={
    println("*********** Start Loading Data **************")
    StartABCInit()
    log.warn("*** Start load ***")
  }

  ///////////////////// ************* Change For ABC *************** //////////////////
  def afterLoadData():Unit={
    println("*********** End Loading Data **************")
  }
  def loadDataWithABC():Map[String,DataFrame]={
    beforeLoadData()
    val data = loadData()
    afterLoadData()
    return data;
  }

  // This section define the AOP for Load
  def beforeWriteData():Unit={
    println("*********** Start Write Data **************")
  }
  def afterWriteData():Unit={
    println("*********** End Write Data **************")
  }
  def writeDataWithABC(outDFs:Map[String,DataFrame]):Unit={
    beforeWriteData
    writeData(outDFs)
    afterWriteData
  }


  def beforeProcData():Unit={
    println("*********** Start Proc Data **************")
    println("Proc Start time: " + sdf.format(Calendar.getInstance().getTime()))
  }
  def afterProcData():Unit={
    println("Proc End time: " + sdf.format(Calendar.getInstance().getTime()))
    println("*********** End Proc Data **************")
  }
  def processDataWithABC(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={
    beforeProcData
    val resultDF=processData(inDFs)
    afterProcData
    return resultDF
  }




  def operation():Unit={

    @transient val log = org.apache.log4j.LogManager.getLogger("TraitOperator")

    //@transient lazy val log = Logger("TraitOperator")
   // @transient lazy val log = LoggerFactory.getLogger("TraitOperator")

    log.warn("Starting Operation")
    println("*** Starting Operation *****")
    writeDataWithABC(processDataWithABC(loadDataWithABC()));
  }

  //def processData(df:DataFrame):RDD[(String,Int)];


}
