package com.anthem.hpip.targettin

object TargetTinDriver {

  def main(args: Array[String]): Unit = {

    if (args != null && args.size != 3) {
      throw new IllegalArgumentException(
        s"""TARGET TIN Driver program needs exactly 3 arguments.
       | 1. Configuration file path:
       | 2. Environment:
       | 3. Query File Category""".stripMargin)
    }

    val Array(confFilePath, env,queryFileCategory) = args

    (new TargetTinOperation(confFilePath, env,queryFileCategory)).operation()
  }

}
