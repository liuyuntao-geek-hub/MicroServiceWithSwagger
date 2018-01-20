package com.anthem.hpip.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object DateUtils {

  def getLastDayOfTheMonth(date: String): String = {
    val formatter = new SimpleDateFormat("yyyyMMdd")

    //TODO:Try using JODA api
    val dt = formatter.parse(date)
    val calendar = Calendar.getInstance()
    calendar.setTime(dt)

    calendar.add(Calendar.MONTH, 1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.add(Calendar.DATE, -1)

    val lastDay = calendar.getTime()
    formatter.format(lastDay)
  }
  def getCurrentDate(): String = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val date = new Date()
    val today = dateFormat.format(date)
    (today)
  }

 
 /* 
  //this main method for utility testing
   def main(args: Array[String]): Unit = {
    println(getCurrentDate)
  }*/
}