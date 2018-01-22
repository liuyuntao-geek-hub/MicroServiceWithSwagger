package com.anthem.hpip.targettin

import grizzled.slf4j.Logging
import org.joda.time.DateTime
import org.joda.time.Minutes
import com.anthem.hpip.util.HPIPCommonUtils
import com.anthem.hpip.config.Spark2Config

object TargetTinDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, HPIPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[HPIP-ETL] TargetTIN application Started: $startTime")
      (new TargetTinOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[HPIP-ETL] TargetedTin Completed at: " + DateTime.now())
      info(s"[HPIP-ETL] Time Taken for TargetedTin Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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
