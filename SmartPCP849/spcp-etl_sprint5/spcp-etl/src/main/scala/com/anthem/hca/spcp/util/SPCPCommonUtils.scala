package com.anthem.hca.spcp.util

object SPCPCommonUtils {

  def argsErrorMsg(): String = {
    """This program needs exactly 3 arguments.
       | 1. Configuration file path
       | 2. Environment
       | 3. Query File Category""".stripMargin
  }
  
  def exportJobArgsErrorMsg(): String = {
		  """This program needs exactly 4 arguments.
		  | 1. Configuration file path
		  | 2. Environment
		  | 3. Query File Category
		  | 4. SQL Server Table Name""".stripMargin
  }
}