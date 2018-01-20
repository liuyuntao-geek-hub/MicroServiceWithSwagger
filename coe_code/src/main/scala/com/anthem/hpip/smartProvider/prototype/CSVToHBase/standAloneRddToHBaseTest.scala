package com.anthem.hpip.smartProvider.prototype.CSVToHive

import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue}
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{HFileOutputFormat, TableInputFormat}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * Created by yuntliu on 12/6/2017.
  */
object standAloneRddToHBaseTest {

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
