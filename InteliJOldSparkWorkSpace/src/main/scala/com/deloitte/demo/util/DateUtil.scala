package com.deloitte.demo.util

/**
  * Created by yuntliu on 11/8/2017.
  */

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

object DateUtil {

  /**
    * Returns the current data in the requested Date format
    */
  def getCurrentDate(format:String):String = {
    DateTimeFormat.forPattern(format).print(new DateTime());
  }

  def getPriorDateLookingBack(format:String,lookbackMonths:Int):String = {
    DateTimeFormat.forPattern(format).print(new DateTime().minusMonths(lookbackMonths));
  }

  def getHourOfDay():Int = {
    new DateTime().hourOfDay().get
  }
}
