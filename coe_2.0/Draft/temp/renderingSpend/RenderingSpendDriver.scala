package com.anthem.hpip.renderingSpend
import grizzled.slf4j.Logging
import com.anthem.hpip.util.HPIPCommonUtils._
import com.anthem.hpip.config.Spark2Config
import com.anthem.hpip.util.HPIPCommonUtils
import org.joda.time.DateTime
import org.joda.time.Minutes

object RenderingSpendDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, HPIPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[HPIP-ETL] RenderingSpend application Started: $startTime")
      (new RenderingSpendOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[HPIP-ETL] RenderingSpend Completed at: " + DateTime.now())
      info(s"[HPIP-ETL] Time Taken for RenderingSpend Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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
