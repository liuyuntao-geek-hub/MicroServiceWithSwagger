package com.anthem.hca.splice.util

import org.apache.spark.sql.DataFrame

object DataFrameUtils {

  def columnsInUpper(df: DataFrame): DataFrame = {
    val dfWithUppercaseCol = df.toDF(df.columns map (_.toUpperCase()): _*)
    (dfWithUppercaseCol)
  }

}