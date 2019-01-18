/*
 * Copyright (c) 2017, Anthem Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE THIS FILE HEADER.
 *
 */
package com.anthem.hca.spcp.config

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.hadoop.log.LogLevel
import org.apache.log4j.Level

object SparkConfig extends Serializable {

  val conf = new SparkConf()
  conf.set("spark.serializer", "com.splicemachine.serializer.SpliceKryoSerializer")
  conf.set("spark.kryo.registrator", "com.splicemachine.derby.impl.SpliceSparkKryoRegistrator")

  val spark = SparkSession.builder().config(conf).getOrCreate()

  lazy val hdfs: FileSystem = FileSystem.get(spark.sparkContext.hadoopConfiguration)

}