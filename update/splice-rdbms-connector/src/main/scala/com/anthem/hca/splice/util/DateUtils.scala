package com.anthem.hca.splice.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time
import org.apache.spark.SparkConf
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.joda.time.Minutes
import java.sql.Timestamp

object DateUtils {

  def currentTimestamp(): Timestamp = {
    val currentTime = new Timestamp(System.currentTimeMillis())
    (currentTime)
  }
  
}