package com.anthem.cogx.etl.HiveUM
import java.io.File

import com.anthem.cogx.etl.config.CogxConfigKey
import com.anthem.cogx.etl.helper.{ CogxOperationSession, CogxOperator }
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{ current_timestamp, lit }
import org.apache.spark.sql.functions._
import org.apache.hadoop.security.alias.CredentialProviderFactory
import com.anthem.cogx.etl.helper.CogxOperator
import org.apache.spark.sql.types.StringType
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.rdd.PairRDDFunctions
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import java.io.PrintWriter
import java.io.StringWriter
import org.apache.commons.codec.digest.DigestUtils
import com.anthem.cogx.etl.helper.CogxUMRecord
import com.anthem.cogx.etl.helper.cogxUmInfo
import com.anthem.cogx.etl.helper.cogxUmHistory
import com.anthem.cogx.etl.helper.cogxRecord
import collection.JavaConverters._
import java.io.PrintWriter
import java.io.StringWriter
import com.anthem.cogx.etl.util.CogxCommonUtils.asJSONString
//import com.anthem.cogx.etl.util.CogxCommonUtils.getMapReduceJobConfiguration
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.fs.{ FSDataInputStream, Path }
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.HBaseAdmin
import java.io.InputStream
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.storage.StorageLevel.MEMORY_AND_DISK

/**
 * Created by yuntliu on 1/20/2018.
 */

class CogxHiveUMOperation(confFilePath: String, env: String, queryFileCategory: String) extends CogxOperationSession(confFilePath, env, queryFileCategory) with CogxOperator {

	sc.setLogLevel("info")

	import spark.sql
	import spark.implicits._

	val teradata_table_name = config.getString("teradata_table_name")
	var rowCount: Long = 0l

	def loadData(): Map[String, DataFrame] = {
	  
	  val umHiveDf = spark.sql("select * from ts_ccpcogxph_nogbd_r000_in.UM_RQST")
	  
	  val dataMap = Map("hive_query" -> umHiveDf)
			return dataMap

	}
	
	def loadDatabkp(): Map[String, DataFrame] = {

			val isIncremental = config.getString("isIncremental")

					var umHiveQuery = ""

					if (isIncremental.equalsIgnoreCase("yes")) {
						val umAuditQuery = config.getString("cogx_audit_query")
								val last_load_dtm = spark.sql(umAuditQuery).head().toString()
								umHiveQuery = config.getString("hive_query").replace(CogxConfigKey.sourceDBPlaceHolder, inboundHiveDB).toLowerCase().replace(CogxConfigKey.lastUpdtTm, last_load_dtm)
					} else {
						umHiveQuery = config.getString("hive_query").replaceAll(CogxConfigKey.sourceDBPlaceHolder, inboundHiveDB)
					}
			info(s"[COGX] Reading the queries from config file")
			//			Reading the queries from config file 

			//			Showing the queries read from config file 
			info(s"[COGX] CogX Hive query from properties file is : $umHiveQuery")

			//			Executing the queries
			val umHiveDf = spark.sql(umHiveQuery)

			//			Creating a map of table name and respective dataframes
			val dataMap = Map("hive_query" -> umHiveDf)
			return dataMap

	}

	def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
			val df0 = inDFs.getOrElse("hive_query", null)

					df0.show
					var dataMap = Map(teradata_table_name -> df0)

					println("THe count is " + df0.count)

					return dataMap
	}

	def writeData(outDFs: Map[String, DataFrame]): Unit = {
			val df1 = outDFs.getOrElse(teradata_table_name, null)
					df1.show

	}

	def processData_bkp(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
			val df0 = inDFs.getOrElse("hive_query", null)

					val df1 = df0.columns.foldLeft(df0) { (df, colName) =>
					df.schema(colName).dataType match {
					case StringType => { println(s"%%%%%%% TRIMMING COLUMN ${colName.toLowerCase()} %%%%%% VALUE '${trim(col(colName))}'"); df.withColumn(colName.toLowerCase, trim(col(colName))); }
					case _ => { println("Column " + colName.toLowerCase() + " is not being trimmed"); df.withColumn(colName.toLowerCase, col(colName)); }
					}
			}

			df1.persist(MEMORY_AND_DISK)
			rowCount = df1.count()
			ABC_load_count = rowCount.toLong
			println("INFO: CogX Row Count => " + rowCount)

			val CogxUmHbaseDataSet = df1.as[CogxUMRecord]

					var groupCogxUmHbaseDataSet = CogxUmHbaseDataSet.groupByKey { key =>
					((key.src_sbscrbr_id))
			}

			val groupedUMHbaseDataSet1 = groupCogxUmHbaseDataSet.mapGroups((k, iter) => {
				var cogxUmSet = Set[CogxUMRecord]()
						val cogxUmInfo = iter.map(cogx => new cogxUmInfo(
								new CogxUMRecord(
										cogx.rfrnc_nbr,
										cogx.srvc_line_nbr,
										cogx.clncl_sor_cd,
										cogx.mbrshp_sor_cd,
										cogx.um_srvc_stts_cd,
										cogx.src_um_srvc_stts_cd,
										cogx.src_sbscrbr_id,
										cogx.src_mbr_cd,
										cogx.prmry_diag_cd,
										cogx.rqstd_place_of_srvc_cd,
										cogx.src_rqstd_place_of_srvc_cd,
										cogx.authrzd_place_of_srvc_cd,
										cogx.src_authrzd_place_of_srvc_cd,
										cogx.rqstd_srvc_from_dt,
										cogx.authrzd_srvc_from_dt,
										cogx.rqstd_srvc_to_dt,
										cogx.authrzd_srvc_to_dt,
										cogx.rqstd_proc_srvc_cd,
										cogx.authrzd_proc_srvc_cd,
										cogx.rqstd_qty,
										cogx.authrzd_qty,
										cogx.src_um_prov_id,
										cogx.prov_id,
										cogx.src_um_prov_id_rp,
										cogx.prov_id_rp,
										cogx.src_prov_frst_nm_rp,
										cogx.src_prov_last_nm_rp,
										cogx.src_prov_frst_nm,
										cogx.src_prov_last_nm))).toArray

						for (um <- cogxUmInfo) {
							cogxUmSet += um.cogxUMdata
						}

				val cogxUM = new cogxUmHistory(cogxUmSet.toArray)

						(String.valueOf(k), cogxUM) // getString(0) i.e. hbase_key is the column for the rowkey 
			}).repartition(2000)

					println("groupedUMHbaseDataSet1")
					//groupedUMHbaseDataSet1.show(1000,false)

					val DF3 = groupedUMHbaseDataSet1.map {
					case (key, value) => {
						val digest = DigestUtils.md5Hex(String.valueOf(key))
								val rowKey = new StringBuilder(digest.substring(0, 8)).append(key).toString()
								val holder = asJSONString(value)
								val p = new Put(Bytes.toBytes(rowKey))
								((rowKey), (holder))
					}
			}

			val df1WithAuditColumn = DF3.toDF("rowKey", "jsonData")

					var dataMap = Map(teradata_table_name -> df1WithAuditColumn)
					return dataMap

	}

	def writeDataBkp(outDFs: Map[String, DataFrame]): Unit = {

			/*     var confIT = spark.sparkContext.hadoopConfiguration.iterator()
     println ("*********************************************************")
     while (confIT.hasNext())
     {
       var it = confIT.next()
       if (it.getKey.toString().contains("hbase"))
       {

       println("Key: "+ it.getKey())
       println("Value: " + it.getValue())
       }
     }
     println ("*********************************************************")*/

			val df1 = outDFs.getOrElse(teradata_table_name, null).toDF("rowKey", "jsonData")
					Thread.sleep(10000)
					// df1.persist(StorageLevel.MEMORY_AND_DISK).count
					//df1.show()

					val columnFamily = config.getString("hbase_table_columnfamily")
					val columnName = config.getString("hbase_table_columnname")
					val putRDD = df1.rdd.map(x => {
						val rowKey = x.getAs[String]("rowKey")
								val holder = x.getAs[String]("jsonData")
								//(rowKey, holder)
								val p = new Put(Bytes.toBytes(rowKey))
								p.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes(holder))
								(new org.apache.hadoop.hbase.io.ImmutableBytesWritable, p)
					})

					val hbaseTable = config.getString("hbase_schema") + ":" + config.getString("hbase_table_name")

					if (env.equalsIgnoreCase("local")) {
						//// Run it on local ////
						//// Empty this when run on local
						////////////////////////////////////
						getMapReduceJobConfiguration(hbaseTable)
					} else {
						/// Run it on Cluster now
						new PairRDDFunctions(putRDD).saveAsNewAPIHadoopDataset(getMapReduceJobConfiguration(hbaseTable))
					}

	}

}