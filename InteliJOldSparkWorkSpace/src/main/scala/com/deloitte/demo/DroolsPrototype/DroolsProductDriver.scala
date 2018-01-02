package com.deloitte.demo.DroolsPrototype
import com.deloitte.demo.framework.OperationWrapper
/**
  * Created by yuntliu on 12/7/2017.
  */
object DroolsProductDriver extends OperationWrapper{
  def main(args: Array[String]): Unit = {
    processExecParam(args)
    (new DroolsProductOperation(AppName = "FirstTestApp", master = ExecMaster
    )).operation()
  }
}
