package com.anthem.smartpcp.drools.dfdroolssplice

import grizzled.slf4j.Logging
import com.anthem.smartpcp.drools.util.CommonUtils
import com.anthem.smartpcp.drools.config.Spark2Config
import org.joda.time.DateTime
import org.joda.time.Minutes


object DroolsDFDriver   extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, CommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"[Drools-DF] Template application Started: $startTime")
      (new DroolsDFOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"[Drools-DF] Template Completed at: " + DateTime.now())
      info(s"[Drools-DF] Time Taken for Template Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } catch {
      case th: Throwable =>
        error("[Drools-DF] [main] Exception occurred " + th)
        throw th
    } finally {
      info("[Drools-DF] Stopping spark Context")
      Spark2Config.spark.sparkContext.stop()
    }
  }



}
