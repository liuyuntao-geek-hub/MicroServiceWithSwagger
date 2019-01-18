package com.anthem.hca.spcp.provider.pcpgeocode

import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.config.SparkConfig
import com.anthem.hca.spcp.util.SPCPCommonUtils

import grizzled.slf4j.Logging

object PCPGeoCodeDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, SPCPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[SPCP-ETL] PCP Geo Code application Started: $startTime")
      (new PCPGeoCodeOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[SPCP-ETL] PCP Geocode Completed at: " + DateTime.now())
      info(s"[SPCP-ETL] Time Taken for PCPGEOCODE Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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
