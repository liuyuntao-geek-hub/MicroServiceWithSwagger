package com.deloitte.demo.hiveOperation

import com.deloitte.demo.framework.OperationWrapper

/**
  * Created by yuntliu on 11/8/2017.
  */
object hiveWriteWrapper extends OperationWrapper{
  def main(args: Array[String]): Unit = {

    processExecParam(args)
    (new hiveWriteOperation(AppName = "HiveWriteOperation", master = ExecMaster
    )).operation()
  }
}
