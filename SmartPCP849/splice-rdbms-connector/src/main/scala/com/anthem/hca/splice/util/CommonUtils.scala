package com.anthem.hca.spliceexport.util

object CommonUtils {

  def exportJobArgsErrorMsg(): String = {
		  """This program needs exactly 4 arguments.
		  | 1. Configuration file path
		  | 2. Environment
		  | 3. Target View/Table Name""".stripMargin
  }
}