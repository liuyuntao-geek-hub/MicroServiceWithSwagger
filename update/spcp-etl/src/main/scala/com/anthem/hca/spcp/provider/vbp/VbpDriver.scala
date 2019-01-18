package com.anthem.hca.spcp.provider.vbp

import com.anthem.hca.spcp.util.SPCPCommonUtils
import org.joda.time.DateTime
import org.joda.time.Minutes
import grizzled.slf4j.Logging
import com.anthem.hca.spcp.config.SparkConfig

object VbpDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, SPCPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[SPCP-ETL] VBP application Started: $startTime")
      (new VbpOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[SPCP-ETL] VBP Completed at: " + DateTime.now())
      info(s"[SPCP-ETL] Time Taken for VBP Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } catch {
      case th: Throwable =>
        error("[SPCP-ETL] [main] Exception occurred " + th)
        throw th
    } finally {
      info("[SPCP-ETL] Stopping spark Context")
      SparkConfig.spark.sparkContext.stop()
    }
  }

}