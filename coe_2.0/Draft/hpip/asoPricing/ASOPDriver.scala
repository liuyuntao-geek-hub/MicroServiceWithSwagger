package com.anthem.hpip.asoPricing

/**
  * Created by yuntliu on 11/19/2017.
  */
object ASOPDriver {
  def main(args: Array[String]): Unit = {
    //    (new PharmacyOperation(appName = "SpendFileProcessing", master = "local[2]",
    //      propertyFile = "application.properties")).operation()
    if (args != null && args.size != 3) {
      throw new IllegalArgumentException(
        s"""Pharmacy Driver program needs exactly 3 arguments.
           | 1. Configuration file path:
           | 2. Environment:
           | 3. Query File Category""".stripMargin)
    }

    val Array(confFilePath, env,queryFileCategory) = args

    (new ASOPOperation(confFilePath, env,queryFileCategory)).operation()
  }
}
