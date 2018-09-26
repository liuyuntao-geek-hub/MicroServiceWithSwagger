package com.anthem.hca.spcp.export

import com.anthem.hca.spcp.util.SPCPCommonUtils
import org.joda.time.DateTime
import org.joda.time.Minutes
import grizzled.slf4j.Logging
import com.anthem.hca.spcp.config.SparkConfig

object ExportDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 4, SPCPCommonUtils.exportJobArgsErrorMsg)

    val Array(confFilePath, env, queryFileCategory,tblName) = args
    try {
      val startTime = DateTime.now
      info(s"[SPCP-ETL] PCP Geo Code application Started: $startTime")
      (new ExportOperation(confFilePath, env, queryFileCategory,tblName.toLowerCase())).operation()
      info(s"[SPCP-ETL] PCP Geocode Completed at: " + DateTime.now())
      info(s"[SPCP-ETL] Time Taken for MTCLM Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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
