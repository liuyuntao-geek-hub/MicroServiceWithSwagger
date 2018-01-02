package com.deloitte.demo.prototype.LoggerTest.slf4JTest

import org.slf4j.LoggerFactory

/**
  * Created by yuntliu on 11/12/2017.
  */

trait mySlf4JTrait {

  @transient
  lazy val log = LoggerFactory.getLogger("mySlf4JTrait-Trait")

  def runAll(input:String):Unit={
    log.warn("Trait - Rull All - Slf4J Only")
    needToImplement(input)
    needToImplementOther(input)
  }

  def needToImplementOther(input:String):Unit
  def needToImplement(input:String):Unit;

}

