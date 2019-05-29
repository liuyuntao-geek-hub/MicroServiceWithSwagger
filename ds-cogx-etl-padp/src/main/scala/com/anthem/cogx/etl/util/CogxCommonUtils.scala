package com.anthem.cogx.etl.util

import com.google.gson.GsonBuilder
import org.apache.avro.io.EncoderFactory
import org.apache.avro.reflect.ReflectData
import org.apache.avro.reflect.ReflectDatumWriter
import java.io.ByteArrayOutputStream
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableMapReduceUtil, TableOutputFormat}

object CogxCommonUtils {

  def argsErrorMsg(): String = {
    """Rendering Spend Driver program needs exactly 3 arguments.
       | 1. Configuration file path
       | 2. Environment
       | 3. Query File Category""".stripMargin
  }
  
  
    def asJSONString(aType: java.lang.Object): String = {
    val gson = new GsonBuilder().serializeNulls().create();
    gson.toJson(aType);
  }
    

}