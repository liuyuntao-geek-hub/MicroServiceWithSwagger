package com.deloitte.demo.hiveOperation

import com.deloitte.demo.framework.OperationWrapper

/**
  * Created by yuntliu on 11/30/2017.
  */
object csvHoHiveWrapper extends OperationWrapper{
  def main(args: Array[String]): Unit = {

    processExecParam(args)
    (new csvToHiveOperation(AppName = "HiveToCSV", master = ExecMaster
    )).operation()
  }

}
