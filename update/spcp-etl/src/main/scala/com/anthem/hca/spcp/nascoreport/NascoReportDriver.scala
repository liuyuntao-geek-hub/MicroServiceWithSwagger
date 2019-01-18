package com.anthem.hca.spcp.nascoreport

import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.config.SparkConfig
import com.anthem.hca.spcp.util.SPCPCommonUtils

import grizzled.slf4j.Logging

object NascoReportDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, SPCPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[SPCP-ETL] NANSCO Reporting ETL application Started: $startTime")
      (new NascoReportOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[SPCP-ETL] NANSCO Reporting ETL Completed at: " + DateTime.now())
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
