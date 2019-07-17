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

			val isIncremental = config.getString("isIncremental")

					var umHiveQuery = config.getString("hive_query").replace(CogxConfigKey.sourceDBPlaceHolder, inboundHiveDB).replace("<<HiveFilter>>", config.getString("hive_filter"))

					if (isIncremental.equalsIgnoreCase("yes")) {
						////// Modify the code to add data filter parameters in the umHiveQuery

						var umAuditQuery = config.getString("cogx_audit_query")

								var last_load_dtm = config.getString("default_incremental_startdt")
								umAuditQuery = umAuditQuery.replace("<<programName>>", programName)
								println("Audit Query => :" + umAuditQuery)

								if (env.equalsIgnoreCase("local") || config.getString("force_default_incremental").equalsIgnoreCase("yes")) {
								} else {
									/// Run on Cluster - Need to disable on local
//									val dataDF = spark.sql("SELECT DISTINCT trim(r.rfrnc_nbr) AS rfrnc_nbr,trim(s.srvc_line_nbr) AS srvc_line_nbr,trim(r.clncl_sor_cd) AS clncl_sor_cd,trim(r.mbrshp_sor_cd) AS mbrshp_sor_cd,trim(stts.um_srvc_stts_cd) AS um_srvc_stts_cd,trim(stts.src_um_srvc_stts_cd) AS src_um_srvc_stts_cd,trim(r.src_sbscrbr_id) AS src_sbscrbr_id,trim(r.src_mbr_cd) AS src_mbr_cd,trim(r.prmry_diag_cd) AS prmry_diag_cd,trim(s.rqstd_place_of_srvc_cd) AS rqstd_place_of_srvc_cd,trim(s.src_rqstd_place_of_srvc_cd) AS src_rqstd_place_of_srvc_cd, trim(s.authrzd_place_of_srvc_cd) AS authrzd_place_of_srvc_cd,trim(s.src_authrzd_place_of_srvc_cd) AS src_authrzd_place_of_srvc_cd,s.rqstd_srvc_from_dt AS rqstd_srvc_from_dt, s.authrzd_srvc_from_dt AS authrzd_srvc_from_dt,s.rqstd_srvc_to_dt AS rqstd_srvc_to_dt,s.authrzd_srvc_to_dt AS authrzd_srvc_to_dt,trim(s.rqstd_proc_srvc_cd) AS rqstd_proc_srvc_cd,trim(s.authrzd_proc_srvc_cd) AS authrzd_proc_srvc_cd,s.rqstd_qty AS rqstd_qty,s.authrzd_qty AS authrzd_qty,trim(sp.src_um_prov_id) AS src_um_prov_id,trim(sp.prov_id) AS prov_id,trim(rp.src_um_prov_id) AS src_um_prov_id_rp,trim(rp.prov_id) AS prov_id_rp,trim(rp.src_prov_frst_nm) AS src_prov_frst_nm_rp,trim(rp.src_prov_last_nm) AS src_prov_last_nm_rp,trim(sp.src_prov_frst_nm) AS src_prov_frst_nm,trim(sp.src_prov_last_nm) AS src_prov_last_nm FROM ts_ccpcogxph_nogbd_r000_in.um_rqst r INNER JOIN (SELECT src_sbscrbr_id FROM ts_ccpcogxph_nogbd_r000_in.um_rqst AS ur WHERE trim(mbrshp_sor_cd) IN ('808', '815', '886', '823') AND length(trim(src_sbscrbr_id))=9 AND trim(rcrd_stts_cd)<> 'dup' AND trim(ur.CDH_LOAD_LOG_KEY) IN (SELECT trim(CDH_LOAD_LOG_KEY) FROM ts_CDLEDWDPH_NOGBD_R000_SG.cdh_load_log WHERE trim(sub_process_nm)='UM_RQST' AND trim(pblsh_ind)='Y' AND cast(date_format(to_date(from_unixtime(unix_timestamp(load_strt_dtm,'yyyy:MM:dd:HH:MM:SS'))),'yyyyMMdd') AS int) >= 20190501)) a ON a.src_sbscrbr_id=r.src_sbscrbr_id INNER JOIN ts_ccpcogxph_nogbd_r000_in.um_srvc s ON r.rfrnc_nbr = s.rfrnc_nbr AND r.clncl_sor_cd = s.clncl_sor_cd LEFT JOIN ts_ccpcogxph_nogbd_r000_in.um_rqst_prov rp ON r.rfrnc_nbr = rp.rfrnc_nbr AND r.clncl_sor_cd = rp.clncl_sor_cd LEFT JOIN ts_ccpcogxph_nogbd_r000_in.um_srvc_prov sp ON r.rfrnc_nbr = sp.rfrnc_nbr AND s.srvc_line_nbr = sp.srvc_line_nbr AND r.clncl_sor_cd = sp.clncl_sor_cd AND s.clncl_sor_cd = sp.clncl_sor_cd LEFT JOIN ts_ccpcogxph_nogbd_r000_in.um_srvc_stts stts ON r.rfrnc_nbr = stts.rfrnc_nbr AND s.srvc_line_nbr = stts.srvc_line_nbr AND r.clncl_sor_cd = stts.clncl_sor_cd AND s.clncl_sor_cd = stts.clncl_sor_cd WHERE r.mbrshp_sor_cd IN ('808','815','886','823') AND length(trim(r.src_sbscrbr_id))=9 AND r.rcrd_stts_cd<> 'dup'")
									val dataDF = spark.sql(umHiveQuery)
								  dataDF.show()
											println("count: " + dataDF.count)
											if (dataDF.head(1).isEmpty) {
												println("There is no previously completed loading. Set the load time to : " + last_load_dtm)
											} else {
												val dataDFString = dataDF.head().toString()
														println("last_load_dtm: " + dataDFString)
														if (dataDFString.contains("null")) {}
														else {
															last_load_dtm = dataDFString.substring(0, 10).replace("-", "").substring(1, 9)
														}
											}
								}

						println("Last Load Date: " + last_load_dtm)
						umHiveQuery = umHiveQuery.replace("<<load_date>>", last_load_dtm)
						println("new SQL: " + umHiveQuery)
					}
					
			//			Showing the queries read from config file 
			println(s"[COGX] CogX Hive query from properties file is : $umHiveQuery")

			//			Executing the queries
			val umHiveDf = spark.sql(umHiveQuery)

			//			Creating a map of table name and respective dataframes
			val dataMap = Map("hive_query" -> umHiveDf)
			return dataMap

	}

	def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
			val df0 = inDFs.getOrElse("hive_query", null)

					val df1 = df0.columns.foldLeft(df0) { (df, colName) =>
					df.schema(colName).dataType match {
					case StringType => { //println(s"%%%%%%% TRIMMING COLUMN ${colName.toLowerCase()} %%%%%% VALUE '${trim(col(colName))}'"); 
						df.withColumn(colName.toLowerCase, trim(col(colName)));
					}
					case _ => { // println("Column " + colName.toLowerCase() + " is not being trimmed"); 
						df.withColumn(colName.toLowerCase, col(colName));
					}
					}
			}

			df1.persist(MEMORY_AND_DISK)
			rowCount = df1.count()
			ABC_load_count = rowCount.toLong
			println(s"[COGX] Row Count => " + rowCount)

			val repartitionNum = config.getInt("repartitionNum")
			val ArraySizeLimit = config.getInt("ArraySizeLimit")

			val CogxUmHbaseDataSet = df1.as[CogxUMRecord].repartition(repartitionNum)

			val RDDSet = CogxUmHbaseDataSet.rdd.repartition(repartitionNum).map(record => (record.src_sbscrbr_id, Set(record))).reduceByKey((a, b) => {
				if (a.size <= ArraySizeLimit) { a ++ b }
				else { print("======== Oversized subscriber (over size 3000):" + a.last.src_sbscrbr_id); a }
			}).repartition(repartitionNum)
			println("RDDset Count:" + RDDSet.count())
			var DSSet = RDDSet.map(k => { (new StringBuilder((DigestUtils.md5Hex(String.valueOf(k._1))).substring(0, 8)).append(k._1).toString(), asJSONString(new cogxUmHistory(k._2.toArray))) }).repartition(repartitionNum)
			println("DSSet count: " + DSSet.count())
			var newDF = DSSet.toDF("rowKey", "jsonData").repartition(repartitionNum)

			println("New DataSet: ")
			println("===========================================")
			//newDF.foreach(x=>{println("rowKey:"+x.getString(0) + "=> jsonData: " +  x.getString(1)  ) })
			println("Total New Count: " + newDF.count())

			println("Original DataSet: ")
			println("===========================================")
			//newDF.foreach(x=>{println("rowKey:"+x.getString(0) + "=> jsonData: " +  x.getString(1)  ) })
			println("Total original Count: " + newDF.count())

			////////////////////////////////////// End of original Groupbykey ////////////////////////////      
//
//								var groupCogxUmHbaseDataSet = CogxUmHbaseDataSet.groupByKey { key =>
//								((key.src_sbscrbr_id))
//						}
//			
//						val groupedUMHbaseDataSet1 = groupCogxUmHbaseDataSet.mapGroups((k, iter) => {
//							var cogxUmSet = Set[CogxUMRecord]()
//									val cogxUmInfo = iter.map(cogx => new cogxUmInfo(
//											new CogxUMRecord(
//													cogx.rfrnc_nbr,
//													cogx.srvc_line_nbr,
//													cogx.clncl_sor_cd,
//													cogx.mbrshp_sor_cd,
//													cogx.um_srvc_stts_cd,
//													cogx.src_um_srvc_stts_cd,
//													cogx.src_sbscrbr_id,
//													cogx.src_mbr_cd,
//													cogx.prmry_diag_cd,
//													cogx.rqstd_place_of_srvc_cd,
//													cogx.src_rqstd_place_of_srvc_cd,
//													cogx.authrzd_place_of_srvc_cd,
//													cogx.src_authrzd_place_of_srvc_cd,
//													cogx.rqstd_srvc_from_dt,
//													cogx.authrzd_srvc_from_dt,
//													cogx.rqstd_srvc_to_dt,
//													cogx.authrzd_srvc_to_dt,
//													cogx.rqstd_proc_srvc_cd,
//													cogx.authrzd_proc_srvc_cd,
//													cogx.rqstd_qty,
//													cogx.authrzd_qty,
//													cogx.src_um_prov_id,
//													cogx.prov_id,
//													cogx.src_um_prov_id_rp,
//													cogx.prov_id_rp,
//													cogx.src_prov_frst_nm_rp,
//													cogx.src_prov_last_nm_rp,
//													cogx.src_prov_frst_nm,
//													cogx.src_prov_last_nm))).toArray
//			
//									for (um <- cogxUmInfo) {
//										cogxUmSet += um.cogxUMdata
//									}
//			
//							val cogxUM = new cogxUmHistory(cogxUmSet.toArray)
//			
//									(String.valueOf(k), cogxUM) // getString(0) i.e. hbase_key is the column for the rowkey 
//						}).repartition(2000)
//			
//								println("groupedUMHbaseDataSet1")
//								//groupedUMHbaseDataSet1.show(1000,false)
//			
//								val DF3 = groupedUMHbaseDataSet1.map {
//								case (key, value) => {
//									val digest = DigestUtils.md5Hex(String.valueOf(key))
//											val rowKey = new StringBuilder(digest.substring(0, 8)).append(key).toString()
//											val holder = asJSONString(value)
//											val p = new Put(Bytes.toBytes(rowKey))
//											((rowKey), (holder))
//								}
//						}
//			
//						val df1WithAuditColumn = DF3.toDF("rowKey", "jsonData")
			
			var dataMap = Map(teradata_table_name -> newDF)
			return dataMap

	}

	def writeData(outDFs: Map[String, DataFrame]): Unit = {

			val df1 = outDFs.getOrElse(teradata_table_name, null).toDF("rowKey", "jsonData")

					// df1.persist(StorageLevel.MEMORY_AND_DISK).count
					df1.show()
					val hbaseCount = df1.count()
					println(s"[COGX]Loading to HBase count: " + hbaseCount)

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
						getMapReduceJobConfiguration(hbaseTable)
					} else {
						/// Run it on Cluster now
						new PairRDDFunctions(putRDD).saveAsNewAPIHadoopDataset(getMapReduceJobConfiguration(hbaseTable))
						println("Loadde to HBase count: " + hbaseCount)
					}

	}

}