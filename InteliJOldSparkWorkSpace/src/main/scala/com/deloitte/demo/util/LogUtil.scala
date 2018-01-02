package com.deloitte.demo.util

import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.log4j.PropertyConfigurator

/**
  * Created by yuntliu on 11/12/2017.
  */
object LogUtil {
  ///////// Logging setup and current directory //////////////
  def initlog(log4jConfig: String): Unit = {
    val DATE_FORMAT = "yyyy-MM-dd";
    var sdf = new SimpleDateFormat(DATE_FORMAT);
    var now = Calendar.getInstance();
    System.setProperty("log4j.date", sdf.format(now.getTime()));
    if (log4jConfig != null) {
      PropertyConfigurator.configure(log4jConfig);
    }
  }
}
