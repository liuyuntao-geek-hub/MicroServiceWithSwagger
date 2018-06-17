package com.anthem.hpip.SysPerformance.createTestingTable


import grizzled.slf4j.Logging
import com.anthem.hpip.util.HPIPCommonUtils
import com.anthem.hpip.config.Spark2Config
import org.joda.time.DateTime
import org.joda.time.Minutes
/**
  * Created by yuntliu on 5/26/2018.
  */



object createTestingTableDriver  extends Logging{
  
    def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, HPIPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[HPIP-SysPerformance] SysPerformance application Started: $startTime")
      (new createTestingTableOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[HPIP-SysPerformance] SysPerformance Completed at: " + DateTime.now())
      info(s"[HPIP-SysPerformance] Time Taken for SysPerformance Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } catch {
      case th: Throwable =>
        error("[HPIP-SysPerformance] [main] Exception occurred " + th)
        throw th
    } finally {
      info("[HPIP-SysPerformance] Stopping spark Context")
      Spark2Config.spark.sparkContext.stop()
    }
    }
}