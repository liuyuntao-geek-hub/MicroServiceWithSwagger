package com.anthem.smartpcp.drools.dfnodroolsnosplice

import grizzled.slf4j.Logging
import com.anthem.smartpcp.drools.util.CommonUtils
import com.anthem.smartpcp.drools.config.Spark2Config
import org.joda.time.DateTime
import org.joda.time.Minutes


object NoDroolsSpliceDriver   extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, CommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[No-Drools-Splice] Template application Started: $startTime")
      (new NoDroolsSpliceOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[No-Drools-Splice] Template Completed at: " + DateTime.now())
      info(s"[No-Drools-Splice] Time Taken for Template Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } catch {
      case th: Throwable =>
        error("[No-Drools-Splice] [main] Exception occurred " + th)
        throw th
    } finally {
      info("[No-Drools-Splice] Stopping spark Context")
      Spark2Config.spark.sparkContext.stop()
    }
  }



}
