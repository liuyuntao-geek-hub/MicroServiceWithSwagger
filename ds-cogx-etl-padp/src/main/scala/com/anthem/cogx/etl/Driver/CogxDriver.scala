package com.anthem.cogx.etl.Driver

import scala.collection.mutable._
import grizzled.slf4j.Logging
import com.anthem.cogx.etl.util.CogxCommonUtils
import com.anthem.cogx.etl.config.CogxSpark2Config
import org.joda.time.DateTime
import org.joda.time.Minutes
import com.anthem.hpip.Template.CogxTemplateOperation

object CogxDriver extends Logging {
  
    def main(args: Array[String]): Unit = {

    require(args != null && args.size == 3, CogxCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory) = args
    try {
      val startTime = DateTime.now
      info(s"Cogx application Started: $startTime")
      (new CogxTemplateOperation(confFilePath, env, queryFileCategory)).operation()
      info(s"Cogx Completed at: " + DateTime.now())
      info(s"CogxTime Taken for Template Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } catch {
      case th: Throwable =>
        error("Cogx [main] Exception occurred " + th)
        throw th
    } finally {
      info("Cogx Stopping spark Context")
      CogxSpark2Config.spark.sparkContext.stop()
    }
  }
  
}