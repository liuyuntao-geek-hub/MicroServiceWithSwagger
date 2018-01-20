/*
 * Copyright (c) 2017, Anthem Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE THIS FILE HEADER.
 *
*/
package com.anthem.hpip.config

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.storage.StorageLevel

object SparkConfig extends Serializable {

  val conf = new SparkConf()
    .set("hive.execution.engine", "spark")
    .set("spark.acls.enable", "true")

  val sc = new SparkContext(conf)
  sc.hadoopConfiguration.set("parquet.enable.summary-metadata", "false")

  val hiveContext = new HiveContext(sc)
  hiveContext.setConf("hive.exec.dynamic.partition", "true")
  hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")
  hiveContext.setConf("spark.sql.parquet.compression.codec", "snappy")
  hiveContext.setConf("hive.warehouse.data.skipTrash", "true")

  //TODO- Niraml code review
  //RDD compression
  //for persit use storage level as MEMORY_
  //Tungsten engine

  StorageLevel

  lazy val sqlContext = new SQLContext(sc)

  lazy val hdfs: FileSystem = FileSystem.get(sc.hadoopConfiguration)

}