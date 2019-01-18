package com.anthem.hca.splice.util

object CommonUtils {

  def exportJobArgsErrorMsg(): String = {
		  """This program needs exactly 3 arguments.
		  | 1. Configuration file path
		  | 2. Environment
		  | 3. Target View/Table Name""".stripMargin
  }
}