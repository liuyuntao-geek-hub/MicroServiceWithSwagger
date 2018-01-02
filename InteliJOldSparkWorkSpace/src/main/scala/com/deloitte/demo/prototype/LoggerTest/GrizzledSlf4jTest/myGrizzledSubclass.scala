package com.deloitte.demo.prototype.LoggerTest.GrizzledSlf4jTest

import java.io.File

import com.deloitte.demo.util.LogUtil
import com.typesafe.config.{Config, ConfigFactory}
import grizzled.slf4j.Logger

/**
  * Created by yuntliu on 11/12/2017.
  */
class myGrizzledSubclass extends myGrizzeledClass with myGrizzledTrait{


  override def needToImplement(input:String):Unit={
    @transient val log = Logger(classOf[myGrizzledSubclass])
    log.warn("This is the needToImplement on myGrizzledSubClass")
    println(input)
    printMyClass()
  }
  override def needToImplementOther(input:String):Unit={
    println(input)
    printMyClass()
  }
}

object myGrizzledSubclass{





  def main(args: Array[String]): Unit = {
    val exDocReader:Config = ConfigFactory.parseFile(new File( ( "environment.json")))

    LogUtil.initlog(exDocReader.getString("env.log4jConfig"))
    @transient val log = Logger(classOf[myGrizzledSubclass])
    log.warn("This is the sub Class - Grizzled")
    new(myGrizzledSubclass).runAll("subclass String - Grizzled")
  }
}
