package com.deloitte.demo.prototype.LoggerTest.logging4JTest

import java.io.File

import com.deloitte.demo.util.LogUtil
import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by yuntliu on 11/12/2017.
  */
/*

- Log4j:
	- Only on Spark => Trait logging not working
	- outside Spark => Everything works
	- Customized configuration works
- Slf4j:
	- on Spark => Trait logging not working
	- outside Spark => Everything works
	- auto binding to Log4j = only need to add slf4j-log4j2 into the maven - or class path
	- log4J configuration coding still working => out put the the correct folder with the specified logging
	- * subclass implementation = working correctly
- Grizzled slf4J:
	- on Spark => Trait logging not working
	- The logging on File => not giving the right class name
	- auto binding to Log4j = only need to add slf4j-log4j2 into the maven - or class path
	- log4J configuration coding still working => out put the the correct folder with the specified logging
	- * subClass implementation = not working correctly

*/
class myLog4JSubclass extends myLog4JClass with myLog4JTrait{

  @transient
  lazy private val log = org.apache.log4j.LogManager.getLogger(myLog4JSubclass.getClass)
  log.warn("Starting Program mySubclass")
  override def needToImplement(input:String):Unit={
    println(input)
    printMyClass()
    log.warn("This is the Trait implementation with override - log4J")
  }
  def needToImplementOther(input:String):Unit={
    log.warn("This is the Trait implementation without Override - log4J")
  }
}

object myLog4JSubclass{
  val exDocReader:Config = ConfigFactory.parseFile(new File( ( "environment.json")))

  LogUtil.initlog(exDocReader.getString("env.log4jConfig"))
  def main(args: Array[String]): Unit = {
    @transient
    lazy val log = org.apache.log4j.LogManager.getLogger(myLog4JSubclass.getClass)
    log.warn("Starting Program mySubclass - Object - log4J")
    new(myLog4JSubclass).runAll("subclass String")
  }
}
