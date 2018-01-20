package com.anthem.hpip.mtclm

import grizzled.slf4j.Logging

object MTCLMDriver extends Logging {

  def main(args: Array[String]): Unit = {

    if (args != null && args.size != 3) {
      //      args.foreach( println )
      throw new IllegalArgumentException(
        s"""MTCLM Driver program needs exactly 3 arguments.
       | 1. Configuration file path
       | 2. Environment
       | 3. Query File Category""".stripMargin)
    }

    val Array(configPath, env, queryFileCategory) = args

    info(configPath)
    info(env)
    info(queryFileCategory)

    (new MTCLMOperation(configPath, env, queryFileCategory)).operation()
  }
}
