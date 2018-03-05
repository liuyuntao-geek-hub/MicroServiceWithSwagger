package com.anthem.smartpcp.drools.util

object CommonUtils {

  def argsErrorMsg(): String = {
    """Rendering Spend Driver program needs exactly 3 arguments.
       | 1. Configuration file path
       | 2. Environment
       | 3. Query File Category""".stripMargin
  }
}