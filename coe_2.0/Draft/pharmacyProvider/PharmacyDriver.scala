package com.anthem.hpip.pharmacyProvider

/**
 * Created by yuntliu on 11/9/2017.
 */
object PharmacyDriver {
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

    (new PharmacyOperation(confFilePath, env,queryFileCategory)).operation()
  }
}
