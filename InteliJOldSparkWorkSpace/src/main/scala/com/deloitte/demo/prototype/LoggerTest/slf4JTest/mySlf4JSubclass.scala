package com.deloitte.demo.prototype.LoggerTest.slf4JTest


import java.io.File

import com.deloitte.demo.util.LogUtil
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * Created by yuntliu on 11/12/2017.
  */
class mySlf4JSubclass extends mySlf4JClass with mySlf4JTrait{

/*  @transient
  lazy private val log = org.apache.log4j.LogManager.getLogger(mySubclass.getClass)
  log.warn("Starting Program mySubclass")*/
@transient
val classlog = LoggerFactory.getLogger(mySlf4JSubclass.getClass)

  def needToImplement(input:String):Unit={
    println(input)
    printMyClass()
    classlog.warn("This is Trait Implementation without override - slf4JOnly")
  }
  override def needToImplementOther(input:String):Unit={
    classlog.warn("This is Trait Implementation with override - slf4JOnly")
  }
}

object mySlf4JSubclass{
  val exDocReader:Config = ConfigFactory.parseFile(new File( ( "environment.json")))

  LogUtil.initlog(exDocReader.getString("env.log4jConfig"))

  def main(args: Array[String]): Unit = {
/*    @transient
    lazy val log = org.apache.log4j.LogManager.getLogger(mySubclass.getClass)
    log.warn("Starting Program mySubclass - Object")*/

    @transient
    lazy val log = LoggerFactory.getLogger(mySlf4JSubclass.getClass)
    log.warn("Starting Program mySubclass - Object - slf4JOnly")

    new(mySlf4JSubclass).runAll("subclass String")
  }
}
