package com.anthem.hca.splice.export.upsert

import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.splice.config.SparkConfig
import com.anthem.hca.splice.util.CommonUtils

import grizzled.slf4j.Logging

object Splice2RDBMSUpsertDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, CommonUtils.exportJobArgsErrorMsg)

    val Array(confFilePath,env,tblName) = args
    try {
      val startTime = DateTime.now
      info(s"[SPLICE-RDBMS_CONNECTOR] PCP Geo Code application Started: $startTime")
      (new Splice2RDBMSUpsertOperation(confFilePath,env, tblName)).operation()
      info(s"[SPLICE-RDBMS_CONNECTOR] PCP Geocode Completed at: " + DateTime.now())
      info(s"[SPLICE-RDBMS_CONNECTOR] Time Taken for MTCLM Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } catch {
      case th: Throwable =>
        error("[SPLICE-RDBMS_CONNECTOR] [main] Exception occurred " + th)
        throw th
    } finally {
      info("[SPLICE-RDBMS_CONNECTOR] Stopping spark Context")
      SparkConfig.spark.sparkContext.stop()
    }
  }

}
