package com.deloitte.demo.prototype.HBaseOperation

import org.apache.spark._
import org.apache.spark.rdd.{NewHadoopRDD, RDD}
import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor}
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.hbase.KeyValue
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles

/**
  * Created by yuntliu on 11/26/2017.
  */
class SparkRddToHFileInsert {

}

object SparkRddToHFileInsert{

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("SparkToHFileToHBase").setMaster("local[2]")
    val sc = SparkContext.getOrCreate(conf);

    val config = HBaseConfiguration.create
    config.set("hbase.zookeeper.quorum", "10.118.36.103")

    val tableName = "Anthem_PADP:customer"
    config.set(TableInputFormat.INPUT_TABLE, tableName)
    val hTable = new HTable(config, "Anthem_PADP:customer")

    val job = Job.getInstance(config)
    job.setMapOutputKeyClass (classOf[ImmutableBytesWritable])
    job.setMapOutputValueClass (classOf[KeyValue])
    HFileOutputFormat.configureIncrementalLoad (job, hTable)

    val num = sc.parallelize(11 to 19)
    val rdd:RDD[(ImmutableBytesWritable, KeyValue)] = num.map(x=>{
      val kv: KeyValue = new KeyValue(Bytes.toBytes(s"Debt Manager_Dennis_${x}_fromSpark"), "personalID".getBytes(), "first_name".getBytes(), s"Dennis_${x}".getBytes() )
      (new ImmutableBytesWritable(Bytes.toBytes(x)), kv)
    })
// hadoop fs -mkdir /user/yuntliu/HbaseWorkShop
    //hadoop fs -mkdir /user/yuntliu/HbaseWorkShop/HFileHome/tmp

  //  rdd.saveAsNewAPIHadoopFile("hdfs:///user/yuntliu/HbaseWorkShop/HFileHome/tmp", classOf[ImmutableBytesWritable], classOf[KeyValue], classOf[HFileOutputFormat], config)
    rdd.saveAsNewAPIHadoopFile("hdfs:///user/yuntliu/HbaseWorkShop/HFileHome/tmp", classOf[ImmutableBytesWritable], classOf[KeyValue], classOf[HFileOutputFormat], job.getConfiguration)


/*

    val bulkLoader = new LoadIncrementalHFiles(config)
    bulkLoader.doBulkLoad(new Path("hdfs:///user/yuntliu/HbaseWorkShop/HFileHome/tmp"), hTable)

*/

    // Instantiating Put class

  }
}
