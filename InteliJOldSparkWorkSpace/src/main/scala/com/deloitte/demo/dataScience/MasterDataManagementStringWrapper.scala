package com.deloitte.demo.dataScience

import com.deloitte.demo.framework.OperationWrapper

/**
  * Created by yuntliu on 11/8/2017.
  */
object MasterDataManagementStringWrapper extends OperationWrapper{
  def main(args: Array[String]): Unit = {

    processExecParam(args)
    (new MasterDataManagementStringOperation(AppName = "FirstTestApp", master = ExecMaster
    )).operation()
  }
}
