package com.deloitte.demo.prototype.HBaseOperation

import java.io.IOException
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark._
import org.apache.spark.rdd.NewHadoopRDD
import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor}
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.HTable;

/**
  * Created by yuntliu on 11/26/2017.
  */
class SimpleDirectInsertData {

}
object SimpleDirectInsertData{
  def main(args: Array[String]): Unit = {
    val config = HBaseConfiguration.create
    config.set("hbase.zookeeper.quorum", "10.118.36.103")

    val tableName = "Anthem_PADP:customer"
    config.set(TableInputFormat.INPUT_TABLE, tableName)
    val hTable = new HTable(config, "Anthem_PADP:customer")
    // Instantiating Put class
    // accepts a row name.
    val p = new Put(Bytes.toBytes("Debt Manager_Dennis_1_fromSpark"))
    p.add(Bytes.toBytes("personalID"), Bytes.toBytes("some_source_rowID"), Bytes.toBytes("some New Column"))
    p.add(Bytes.toBytes("personalID"), Bytes.toBytes("first_name"), Bytes.toBytes("Dennis"))
    p.add(Bytes.toBytes("personalID"), Bytes.toBytes("last_name"), Bytes.toBytes("Sullivan"))
    p.add(Bytes.toBytes("personalID"), Bytes.toBytes("street"), Bytes.toBytes("232 Dryden Terrace"))
    p.add(Bytes.toBytes("personalID"), Bytes.toBytes("city"), Bytes.toBytes("Appleton"))
    p.add(Bytes.toBytes("personalID"), Bytes.toBytes("state"), Bytes.toBytes("Wisconsin"))
    p.add(Bytes.toBytes("personalID"), Bytes.toBytes("zip"), Bytes.toBytes("54915"))
    p.add(Bytes.toBytes("personalID"), Bytes.toBytes("phone"), Bytes.toBytes("1-(920)529-3924"))
    p.add(Bytes.toBytes("personalContact"), Bytes.toBytes("email"), Bytes.toBytes("dsullivan1@ucla.edu"))
    p.add(Bytes.toBytes("personalContact"), Bytes.toBytes("source"), Bytes.toBytes("Debt Manager"))
    p.add(Bytes.toBytes("personalContact"), Bytes.toBytes("rowID"), Bytes.toBytes(0))
    // Saving the put Instance to the HTable.
    hTable.put(p)
    System.out.println("data inserted")
    // closing HTable

    hTable.flushCommits()

    hTable.close()
  }
}