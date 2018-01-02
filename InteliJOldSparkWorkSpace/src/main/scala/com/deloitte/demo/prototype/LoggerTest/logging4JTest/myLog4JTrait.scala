package com.deloitte.demo.prototype.LoggerTest.logging4JTest

/**
  * Created by yuntliu on 11/12/2017.
  */
trait myLog4JTrait {
  @transient
  lazy private val log = org.apache.log4j.LogManager.getLogger(myLog4JSubclass.getClass)


  def runAll(input:String):Unit={
    log.warn("Trait - Rull All - log4J")
    needToImplement(input)
    needToImplementOther(input)
  }

  def needToImplementOther(input:String):Unit
  def needToImplement(input:String):Unit;

}
