package com.deloitte.demo.prototype.LoggerTest.GrizzledSlf4jTest

import grizzled.slf4j.Logger

/**
  * Created by yuntliu on 11/12/2017.
  */
trait myGrizzledTrait {

/*
  @transient
  lazy private val log = org.apache.log4j.LogManager.getLogger(mySubclass.getClass)
*/


  def runAll(input:String):Unit={
    @transient val log = Logger(classOf[myGrizzledSubclass])
    log.warn("Trait - Rull All - Grizzled ")
    needToImplementOther(input)
    needToImplementOther(input)
  }

  def needToImplementOther(input:String):Unit
  def needToImplement(input:String):Unit;

}
