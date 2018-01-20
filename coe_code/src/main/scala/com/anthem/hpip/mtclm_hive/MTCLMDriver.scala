package com.anthem.hpip.mtclm_hive

object MTCLMDriver {

  def main(args: Array[String]): Unit = {
     if (args != null && args.size != 2) {
      //      args.foreach( println )
      throw new IllegalArgumentException(
        s"""MTCLM Driver program needs exactly 2 arguments.
       | 1. Configuration file path:
       | 2. Environment:
       | 3. Query File Category""".stripMargin)
    }

    val Array(confFilePath, env,queryFileCategory) = args

    (new MTCLMOperation(confFilePath, env,queryFileCategory)).operation()
  }
}
