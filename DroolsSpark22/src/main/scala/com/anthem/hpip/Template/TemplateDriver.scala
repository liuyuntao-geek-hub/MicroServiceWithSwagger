package com.anthem.hpip.Template


import grizzled.slf4j.Logging
import com.anthem.hpip.util.HPIPCommonUtils
import com.anthem.hpip.config.Spark2Config
import org.joda.time.DateTime
import org.joda.time.Minutes
/**
  * Created by yuntliu on 1/20/2018.


  */
object TemplateDriver  extends Logging {
  
/*  def main(args: Array[String]): Unit = {
    //    (new PharmacyOperation(appName = "SpendFileProcessing", master = "local[2]",
    //      propertyFile = "application.properties")).operation()
    if (args != null && args.size != 3) {
      throw new IllegalArgumentException(
        s"""Pharmacy Driver program needs exactly 3 arguments.
           | 1. Configuration file path:
           | 2. Environment:
           | 3. Query File Category""".stripMargin)
    }

    val Array(confFilePath, env,queryFileCategory) = args

    (new TemplateOperation(confFilePath, env,queryFileCategory)).operation()
  }
  */
  
  
  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, HPIPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[HPIP-ETL] Template application Started: $startTime")
      (new TemplateOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[HPIP-ETL] Template Completed at: " + DateTime.now())
      info(s"[HPIP-ETL] Time Taken for Template Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } catch {
      case th: Throwable =>
        error("[HPIP-ETL] [main] Exception occurred " + th)
        throw th
    } finally {
      info("[HPIP-ETL] Stopping spark Context")
      Spark2Config.spark.sparkContext.stop()
    }
  }
  
  
  
}