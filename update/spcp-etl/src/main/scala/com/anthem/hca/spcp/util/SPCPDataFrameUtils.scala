package com.anthem.hca.spcp.util

import org.apache.spark.sql.DataFrame

object SPCPDataFrameUtils {

  def columnsInUpper(df: DataFrame): DataFrame = {
    val dfWithUppercaseCol = df.toDF(df.columns map (_.toUpperCase()): _*)
    (dfWithUppercaseCol)
  }

}