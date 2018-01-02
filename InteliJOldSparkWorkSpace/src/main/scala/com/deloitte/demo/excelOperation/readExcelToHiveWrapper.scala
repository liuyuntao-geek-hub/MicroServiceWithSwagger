package com.deloitte.demo.excelOperation
import com.deloitte.demo.framework.OperationWrapper
/**
  * Created by yuntliu on 12/3/2017.
  *
  * This solution is using crealytics = Not really working because the apple throw Garbage collection Error:
  *   Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded

  *       <groupId>com.crealytics</groupId>
      <artifactId>spark-excel_2.10</artifactId>
  *
  */
object readExcelToHiveWrapper extends OperationWrapper{
  def main(args: Array[String]): Unit = {
    processExecParam(args)
    (new readExcelToHiveOperation(AppName = "FirstTestApp", master = ExecMaster
    )).operation()
  }
}
