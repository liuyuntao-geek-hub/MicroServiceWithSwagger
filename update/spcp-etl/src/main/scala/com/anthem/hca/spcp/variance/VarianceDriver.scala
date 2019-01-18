package com.anthem.hca.spcp.variance

import com.anthem.hca.spcp.util.SPCPCommonUtils
import org.joda.time.DateTime
import org.joda.time.Minutes
import grizzled.slf4j.Logging
import com.anthem.hca.spcp.config.SparkConfig

object VarianceDriver extends Logging {

  def main(args: Array[String]): Unit = {

    require(args != null && args.size == 4, SPCPCommonUtils.argsErrorMsg)

    val Array(confFilePath, env, queryFileCategory,tableName) = args
    try {
      val startTime = DateTime.now
      info(s"[SPCP-ETL] Prodcuct Plan application Started: $startTime")
      (new VarianceOperation(confFilePath, env, queryFileCategory,tableName)).operation()
      info(s"[SPCP-ETL] Prodcuct Plan Completed at: " + DateTime.now())
      info(s"[SPCP-ETL] Time Taken for Prodcuct Plan Completion :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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
