/*
 * Copyright (c) 2017, Anthem Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE THIS FILE HEADER.
 *
*/
package com.anthem.hpip.config

import org.apache.hadoop.fs.FileSystem

trait AppConfigTrait extends Serializable {
  val hdfs: FileSystem
  var hiveWriteFileFormat: String = "parquet"
}