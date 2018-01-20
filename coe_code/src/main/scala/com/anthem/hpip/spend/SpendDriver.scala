package com.anthem.hpip.spend

object SpendDriver {

  def main(args: Array[String]): Unit = {

    if (args != null && args.size != 3) {
      //      args.foreach( println )
      throw new IllegalArgumentException(
        s"""Spend Driver program needs exactly 3 arguments.
       | 1. Configuration file path:
       | 2. Environment:
       | 3. Query File Category""".stripMargin)
    }
    val Array(confFilePath, env,queryFileCategory) = args

    (new SpendOperation(confFilePath, env,queryFileCategory)).operation()
  }
}
