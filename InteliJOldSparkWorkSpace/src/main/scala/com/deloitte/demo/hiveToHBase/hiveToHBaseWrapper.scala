package com.deloitte.demo.hiveToHBase
import com.deloitte.demo.framework.OperationWrapper

/**
  * Created by yuntliu on 12/5/2017.
  */
object hiveToHBaseWrapper extends OperationWrapper{
  def main(args: Array[String]): Unit = {

    processExecParam(args)
    (new hiveToHBaseOperation(AppName = "csvToHBase", master = ExecMaster
    )).operation()
  }
}
