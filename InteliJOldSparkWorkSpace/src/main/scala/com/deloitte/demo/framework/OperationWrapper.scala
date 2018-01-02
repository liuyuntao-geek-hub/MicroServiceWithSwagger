package com.deloitte.demo.framework

import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.log4j.PropertyConfigurator

/**
  * Created by yuntliu on 11/8/2017.
  */
class OperationWrapper {

  var AppName = StringKey.localAppName
  var ExecMaster = StringKey.localMaster


  def processExecParam(args:Array[String])={

    if (args.length==1)
      {
        AppName=args(0)
      }
    else if (args.length==2)
      {
        AppName=args(0)
        ExecMaster=args(1)
      }


  }

}
