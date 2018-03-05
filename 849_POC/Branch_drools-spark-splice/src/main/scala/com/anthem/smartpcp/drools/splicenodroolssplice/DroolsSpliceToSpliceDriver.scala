package com.anthem.smartpcp.drools.splicenodroolssplice

import grizzled.slf4j.Logging
import com.anthem.smartpcp.drools.util.CommonUtils
import com.anthem.smartpcp.drools.config.Spark2Config
import org.joda.time.DateTime
import org.joda.time.Minutes

object DroolsSpliceToSpliceDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, CommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[HPIP-ETL] Template application Started: $startTime")
      (new DroolsSpliceToSpliceOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[HPIP-ETL] Template Completed at: " + DateTime.now())
      info(s"[HPIP-ETL] Time Taken for Template Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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
