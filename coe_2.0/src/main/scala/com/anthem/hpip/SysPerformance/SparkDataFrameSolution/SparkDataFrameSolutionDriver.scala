package com.anthem.hpip.SysPerformance.SparkDataFrameSolution

import grizzled.slf4j.Logging
import com.anthem.hpip.util.HPIPCommonUtils
import com.anthem.hpip.config.Spark2Config
import org.joda.time.DateTime
import org.joda.time.Minutes
/**
  *
  * 
  */


object SparkDataFrameSolutionDriver extends Logging {
  
  
  
  
  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, HPIPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[HPIP-ETL] Template application Started: $startTime")
      (new SparkDataFrameSolutionOperation(confFilePath, env, queryFileCategory)).operation()
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
