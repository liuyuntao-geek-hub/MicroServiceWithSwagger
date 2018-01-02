package com.deloitte.demo.programTemplate

import com.deloitte.demo.framework.OperationWrapper

/**
  * Created by yuntliu on 11/8/2017.
  */
object templateWrapper extends OperationWrapper{
  def main(args: Array[String]): Unit = {
    processExecParam(args)
    (new templateOperation(AppName = "FirstTestApp", master = ExecMaster
    )).operation()
  }
}
