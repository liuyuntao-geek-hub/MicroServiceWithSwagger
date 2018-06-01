package com.anthem.hpip.config

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.SparkSession

object Spark2Config extends Serializable {

val warehouseLocation = "file:${system:user.dir}/spark-warehouse"

 /////////// When Run on Local ///////////////

   // val spark = SparkSession.builder().appName("FSI").master("local[*]").getOrCreate()


 
 ///////////  End of When Run on Local ///////////////

/////////////////// When Run on Cluster /////////////////////


 val spark = SparkSession
    .builder()
   // .config("spark.sql.warehouse.dir", warehouseLocation)
    .config("hive.exec.dynamic.partition", "true")
    .config("hive.exec.dynamic.partition.mode", "nonstrict")
    .config("spark.sql.parquet.compression.codec", "snappy")
    .config("hive.warehouse.data.skipTrash", "true")
    .config("spark.sql.parquet.writeLegacyFormat", "true")
    .enableHiveSupport()
    .getOrCreate()

    
   
/////////////////// End of When Run on Cluster /////////////////////
    
  //  config("spark.sql.shuffle.partitions", 6)

  
 
  spark.sparkContext.hadoopConfiguration.set("parquet.enable.summary-metadata", "false")
  lazy val hdfs: FileSystem = FileSystem.get(spark.sparkContext.hadoopConfiguration)
}