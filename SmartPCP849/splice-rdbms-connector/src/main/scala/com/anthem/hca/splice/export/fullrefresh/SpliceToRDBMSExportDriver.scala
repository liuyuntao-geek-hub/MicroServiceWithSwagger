package com.anthem.hca.splice.export.fullrefresh

import org.joda.time.DateTime
import org.joda.time.Minutes
import grizzled.slf4j.Logging
import com.anthem.hca.spliceexport.util.CommonUtils
import com.anthem.hca.splice.config.SparkConfig

object SpliceToRDBMSExportDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, CommonUtils.exportJobArgsErrorMsg)

    val Array(confFilePath,env, tblName) = args
    try {
      val startTime = DateTime.now
      info(s"[SPCP-ETL] Splice Export application Started: $startTime")
      (new SpliceToRDBMSExportOperation(confFilePath,env, tblName)).operation()
      info(s"[SPCP-ETL] Splice Export Completed at: " + DateTime.now())
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
