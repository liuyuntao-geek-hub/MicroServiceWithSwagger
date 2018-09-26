package com.anthem.hca.spcp.provider

import com.anthem.hca.spcp.util.SPCPCommonUtils
import org.joda.time.DateTime
import org.joda.time.Minutes
import grizzled.slf4j.Logging
import com.anthem.hca.spcp.config.SparkConfig

object ProviderDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, SPCPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[HPIP-ETL] PCP Geo Code application Started: $startTime")
      (new ProviderOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[HPIP-ETL] MTCLM Completed at: " + DateTime.now())
      info(s"[HPIP-ETL] Time Taken for MTCLM Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } catch {
      case th: Throwable =>
        error("[HPIP-ETL] [main] Exception occurred " + th)
        throw th
    } finally {
      info("[HPIP-ETL] Stopping spark Context")
      SparkConfig.spark.sparkContext.stop()
    }
  }

}
