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
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

/**
 * Created by yuntliu on 1/20/2018.
 */

class CogxHiveUMOperationDF(confFilePath: String, env: String, queryFileCategory: String) extends CogxOperationSession(confFilePath, env, queryFileCategory) with CogxOperator {

	sc.setLogLevel("info")

	import spark.sql
	import spark.implicits._

	val teradata_table_name = config.getString("teradata_table_name")
	var rowCount: Long = 0l

	def loadData(): Map[String, DataFrame] = {	info(s"[COGX] Reading the queries from config file")
//			Reading the queries from config file 
			val QueryUmRqst = config.getString("query_um_rqst").replaceAll(CogxConfigKey.sourceDBPlaceHolder, inboundHiveDB).toLowerCase()
			val QueryUmSrvc = config.getString("query_um_srvc").replaceAll(CogxConfigKey.sourceDBPlaceHolder, inboundHiveDB).toLowerCase()
			val QueryUmRqstProv = config.getString("query_um_rqst_prov").replaceAll(CogxConfigKey.sourceDBPlaceHolder, inboundHiveDB).toLowerCase()
			val QueryUmSrvcProv = config.getString("query_um_srvc_prov").replaceAll(CogxConfigKey.sourceDBPlaceHolder, inboundHiveDB).toLowerCase()
			val QueryUmSrvcStts = config.getString("query_um_srvc_stts").replaceAll(CogxConfigKey.sourceDBPlaceHolder, inboundHiveDB).toLowerCase()

//			Showing the queries read from config file 
			info(s"[COGX] CogX Query for reading data from um_rqst table is $QueryUmRqst")
			info(s"[COGX] Query for reading data from um_srvc table is $QueryUmSrvc")
			info(s"[COGX] Query for reading data from um_rqst_prov table is $QueryUmRqstProv")
			info(s"[COGX] Query for reading data from um_srvc_prov table is $QueryUmSrvcProv")
			info(s"[COGX] Query for reading data from um_srvc_stts table is $QueryUmSrvcStts")

//			Executing the queries
			val umRqstDf = spark.sql(QueryUmRqst)
			val umSrvcDf = spark.sql(QueryUmSrvc)
			val umRqstProvDf = spark.sql(QueryUmRqstProv)
			val umSrvcProvDf = spark.sql(QueryUmSrvcProv)
			val umSrvcSttsDf = spark.sql(QueryUmSrvcStts)

//			Creating a map of table name and respective dataframes
			val dataMap = Map("um_rqst" -> umRqstDf,
					"um_srvc" -> umSrvcDf,
					"um_rqst_prov" -> umRqstProvDf,
					"um_srvc_prov" -> umSrvcProvDf,
					"um_srvc_stts" -> umSrvcSttsDf)
			return dataMap
			}
	
	
	def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

//	  Reading the elements from input map parameter as table name and dataframe
			    val umRqstDf = inDFs.getOrElse("um_rqst", null)
					val umSrvcDf = inDFs.getOrElse("um_srvc", null)
					val umRqstProvDf = inDFs.getOrElse("um_rqst_prov", null)
					val umSrvcProvDf = inDFs.getOrElse("um_srvc_prov", null)
					val umSrvcSttsDf = inDFs.getOrElse("um_srvc_stts", null)
					
					val umJoinDf = umRqstDf.repartition($"RFRNC_NBR",$"CLNCL_SOR_CD").join(umSrvcDf, umRqstDf("RFRNC_NBR") === umSrvcDf("RFRNC_NBR") && umRqstDf("CLNCL_SOR_CD") === umSrvcDf("CLNCL_SOR_CD") , "inner")
					
					umJoinDf.show
					
					val unReqstJ = umJoinDf.join(umRqstProvDf, umRqstDf("RFRNC_NBR") === umRqstProvDf("RFRNC_NBR") && umRqstDf("CLNCL_SOR_CD") === umRqstProvDf("CLNCL_SOR_CD"),
							"left")
							
					unReqstJ.show
					
					val unSrvcJ = unReqstJ.join(umSrvcProvDf, umRqstDf("RFRNC_NBR") === umSrvcProvDf("RFRNC_NBR") &&
					umSrvcDf("SRVC_LINE_NBR") === umSrvcProvDf("SRVC_LINE_NBR") &&
					umRqstDf("CLNCL_SOR_CD") === umRqstProvDf("CLNCL_SOR_CD") && 
					umSrvcDf("CLNCL_SOR_CD") === umSrvcDf("CLNCL_SOR_CD"),
							"left")
							
							unSrvcJ.show
					val umRPJoin = unSrvcJ.join(umSrvcSttsDf, umRqstDf("RFRNC_NBR") === umSrvcSttsDf("RFRNC_NBR") &&
					umSrvcDf("SRVC_LINE_NBR") === umSrvcSttsDf("SRVC_LINE_NBR") &&
					umRqstDf("CLNCL_SOR_CD") === umSrvcSttsDf("CLNCL_SOR_CD") &&
					umSrvcDf("CLNCL_SOR_CD") === umSrvcSttsDf("CLNCL_SOR_CD"),
							"left")
							umRPJoin.show
							
//							Adding row number as the ROW_ID to the dataframe
					val umJoinDfSel = umRPJoin.select(
							trim(umRqstDf("RFRNC_NBR")).alias("RFRNC_NBR"),
							trim(umSrvcDf("SRVC_LINE_NBR")).alias("SRVC_LINE_NBR"),
							trim(umRqstDf("CLNCL_SOR_CD")).alias("CLNCL_SOR_CD"),
							trim(umRqstDf("MBRSHP_SOR_CD")).alias("MBRSHP_SOR_CD"),
							trim(umSrvcSttsDf("UM_SRVC_STTS_CD")).alias("UM_SRVC_STTS_CD"),
							trim(umSrvcSttsDf("SRC_UM_SRVC_STTS_CD")).alias("SRC_UM_SRVC_STTS_CD"),
							trim(umRqstDf("SRC_SBSCRBR_ID")).alias("SRC_SBSCRBR_ID"),
							trim(umRqstDf("SRC_MBR_CD")).alias("SRC_MBR_CD"),
							trim(umRqstDf("PRMRY_DIAG_CD")).alias("PRMRY_DIAG_CD"),
							trim(umSrvcDf("RQSTD_PLACE_OF_SRVC_CD")).alias("RQSTD_PLACE_OF_SRVC_CD"),
							trim(umSrvcDf("SRC_RQSTD_PLACE_OF_SRVC_CD")).alias("SRC_RQSTD_PLACE_OF_SRVC_CD"),
							trim(umSrvcDf("AUTHRZD_PLACE_OF_SRVC_CD")).alias("AUTHRZD_PLACE_OF_SRVC_CD"),
							trim(umSrvcDf("SRC_AUTHRZD_PLACE_OF_SRVC_CD")).alias("SRC_AUTHRZD_PLACE_OF_SRVC_CD"),
							trim(umSrvcDf("RQSTD_SRVC_FROM_DT")).alias("RQSTD_SRVC_FROM_DT"),
							trim(umSrvcDf("AUTHRZD_SRVC_FROM_DT")).alias("AUTHRZD_SRVC_FROM_DT"),
							trim(umSrvcDf("RQSTD_SRVC_TO_DT")).alias("RQSTD_SRVC_TO_DT"),
							trim(umSrvcDf("AUTHRZD_SRVC_TO_DT")).alias("AUTHRZD_SRVC_TO_DT"),
							trim(umSrvcDf("RQSTD_PROC_SRVC_CD")).alias("RQSTD_PROC_SRVC_CD"),
							trim(umSrvcDf("AUTHRZD_PROC_SRVC_CD")).alias("AUTHRZD_PROC_SRVC_CD"),
							trim(umSrvcDf("RQSTD_QTY")).alias("RQSTD_QTY"),
							trim(umSrvcDf("AUTHRZD_QTY")).alias("AUTHRZD_QTY"),
							trim(umSrvcProvDf("SRC_UM_PROV_ID")).alias("SRC_UM_PROV_ID"),
							trim(umSrvcProvDf("PROV_ID")).alias("PROV_ID"),
							trim(umRqstProvDf("SRC_UM_PROV_ID")).alias("SRC_UM_PROV_ID_RP"),
							trim(umRqstProvDf("PROV_ID")).alias("PROV_ID_RP"),
							trim(umRqstProvDf("SRC_PROV_FRST_NM")).alias("SRC_PROV_FRST_NM_RP"),
							trim(umRqstProvDf("SRC_PROV_LAST_NM")).alias("SRC_PROV_LAST_NM_RP"),
							trim(umSrvcProvDf("SRC_PROV_FRST_NM")).alias("SRC_PROV_FRST_NM"),
							trim(umSrvcProvDf("SRC_PROV_LAST_NM")).alias("SRC_PROV_LAST_NM")).distinct

//						TODO:	This code could be removed as trim has already been applied 
					val df1 = umJoinDfSel.columns.foldLeft(umJoinDfSel) { (df, colName) =>
					df.schema(colName).dataType match {
					case StringType => { println(s"%%%%%%% TRIMMING COLUMN ${colName.toLowerCase()} %%%%%% VALUE '${trim(col(colName))}'"); df.withColumn(colName.toLowerCase, trim(col(colName))); }
					case _ => { info(s"[COGX]Column " + colName.toLowerCase() + " is not being trimmed"); df.withColumn(colName.toLowerCase, col(colName)); }
					}
			}
			    
			    umJoinDfSel.show

			rowCount = df1.count()
					info(s"[COGX]INFO: CogX Row Count => " + rowCount)

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

	def writeData(outDFs: Map[String, DataFrame]): Unit = {
			val df1 = outDFs.getOrElse(teradata_table_name, null)
					df1.show

	}


}