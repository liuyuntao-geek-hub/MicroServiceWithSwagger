package com.deloitte.demo.sasLogic

//import org.apache.log4j.LogManager._
//import grizzled.slf4j.Logger
import java.io.File

import com.deloitte.demo.framework.OperationWrapper
import com.deloitte.demo.util.LogUtil
import com.typesafe.config.{Config, ConfigFactory}


/**
  * Created by yuntliu on 11/8/2017.
  */
class wordCountWrapper{

}
object wordCountWrapper  extends OperationWrapper{
  def main(args: Array[String]): Unit = {

    // In object: logger works
    val exDocReader:Config = ConfigFactory.parseFile(new File( ( "environment.json")))
    LogUtil.initlog(exDocReader.getString("env.log4jConfig"))
///// This env.log4jConfig => need to be totally local path, not file:/// => java will read it, and will fail
    ///// Simply need to be c:/xxx
    ///// not a good idea to use env.log4jConfig => better just use the default loge4.properties and put it into the resource folder in the package



    // Following is log4j
    @transient lazy val log = org.apache.log4j.LogManager.getLogger(wordCountWrapper.getClass)




/*
    // Following is grizzeled slf4J
    @transient
    lazy val log = Logger(classOf[wordCountWrapper])
*/

   // @transient lazy val log = LoggerFactory.getLogger(wordCountWrapper.getClass)

    log.warn("Starting Program")

    (new wordCountOperation(AppName = "FirstTestApp", master = "local[1]"
    )).operation()
  }

}
