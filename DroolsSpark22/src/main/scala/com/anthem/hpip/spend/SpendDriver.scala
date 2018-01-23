package com.anthem.hpip.spend

import com.anthem.hpip.util.HPIPCommonUtils
import com.anthem.hpip.config.Spark2Config
import org.joda.time.DateTime
import org.joda.time.Minutes
import grizzled.slf4j.Logging

object SpendDriver extends Logging {
 


  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, HPIPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[HPIP-ETL] Spend application Started: $startTime")
      (new SpendOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[HPIP-ETL] Spend Completed at: " + DateTime.now())
      info(s"[HPIP-ETL] Time Taken for Spend Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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
