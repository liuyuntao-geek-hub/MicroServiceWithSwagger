package com.anthem.cogx.etl.HiveBDF
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
import com.anthem.cogx.etl.helper.CogxClaimRecord
import com.anthem.cogx.etl.helper.cogxClaimInfo
import com.anthem.cogx.etl.helper.cogxClaimHistory
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


class CogxHiveBDFOperation(confFilePath: String, env: String, queryFileCategory: String) extends CogxOperationSession(confFilePath, env, queryFileCategory) with CogxOperator {

	sc.setLogLevel("info")

	import spark.sql
	import spark.implicits._

	val teradata_table_name = config.getString("teradata_table_name")
	var rowCount: Long = 0l
	val columnFamily = config.getString("hbase_table_columnfamily")
    val columnName = config.getString("hbase_table_columnname")

	def loadData(): Map[String, DataFrame] = {	info(s"[COGX] Reading the queries from config file")
	  
//			Reading the queries from config file 
			val QueryClmWgsGncclmp = config.getString("query_clm_wgs_gncclmp")
			val QueryClmWgsGncdtlp = config.getString("query_clm_wgs_gncdtlp")
			val QueryClmWgsGncnatpEa2 = config.getString("query_clm_wgs_gncnatp_ea2")

//			Showing the queries read from config file 
			info(s"[COGX] CogX Query for reading data from query_clm_wgs_gncclmp table is $QueryClmWgsGncclmp")
			info(s"[COGX] Query for reading data from query_clm_wgs_gncdtlp table is $QueryClmWgsGncdtlp")
			info(s"[COGX] Query for reading data from query_clm_wgs_gncnatp_ea2 table is $QueryClmWgsGncnatpEa2")
			

//			Executing the queries
			val ClmWgsGncclmpDf = spark.sql(QueryClmWgsGncclmp)
			val ClmWgsGncdtlpDf = spark.sql(QueryClmWgsGncdtlp)
			val ClmWgsGncnatpEa2Df = spark.sql(QueryClmWgsGncnatpEa2)
			

//			Creating a map of table name and respective dataframes
			val dataMap = Map("clm_wgs_gncclmp" -> ClmWgsGncclmpDf,
					"clm_wgs_gncdtlp" -> ClmWgsGncdtlpDf,
					"clm_wgs_gncnatp_ea2" -> ClmWgsGncnatpEa2Df)
			return dataMap
			}
	
	
	def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

//	  Reading the elements from input map parameter as table name and dataframe
			    val ClmWgsGncclmpDf = inDFs.getOrElse("clm_wgs_gncclmp", null).createOrReplaceTempView("ClmWgsGncclmpDf")
					val ClmWgsGncdtlpDf = inDFs.getOrElse("clm_wgs_gncdtlp", null).createOrReplaceTempView("ClmWgsGncdtlpDf")
					val ClmWgsGncnatpEa2Df = inDFs.getOrElse("clm_wgs_gncnatp_ea2", null).createOrReplaceTempView("ClmWgsGncnatpEa2Df")
					
					
                val clmwgsgncclmpSQL = """SELECT 
                                                hdr.ddc_cd_dcn as ddc_cd_dcn,
                                                hdr.ddc_cd_dcn_cc as ddc_cd_dcn_cc, 
                                                hdr.ddc_cd_itm_cde as ddc_cd_itm_cde, 
                                                hdr.ddc_cd_clm_compl_dte as ddc_cd_clm_compl_dte, 
                                                hdr.ddc_cd_clm_pay_act_1 as ddc_cd_clm_pay_act_1, 
                                                hdr.ddc_cd_clm_pay_act_2_6 as ddc_cd_clm_pay_act_2_6, 
                                                Concat(hdr.ddc_cd_cert_nbr1, hdr.ddc_cd_cert_nbr2, hdr.ddc_cd_cert_nbr3) as member_id, 
                                                hdr.ddc_cd_pat_mbr_cde as ddc_cd_pat_mbr_cde, 
                                                hdr.ddc_cd_grp_nbr as ddc_cd_grp_nbr, 
                                                hdr.ddc_cd_svc_from_dte as ddc_cd_svc_from_dte, 
                                                hdr.ddc_cd_svc_thru_dte as ddc_cd_svc_thru_dte, 
                                                hdr.ddc_cd_prvdr_tax_id as ddc_cd_prvdr_tax_id, 
                                                hdr.ddc_cd_prvdr_nme as ddc_cd_prvdr_nme, 
                                                hdr.ddc_cd_prvdr_sec_nme as ddc_cd_prvdr_sec_nme, 
                                                hdr.ddc_cd_prvdr_spclty_cde as ddc_cd_prvdr_spclty_cde, 
                                                Concat(hdr.ddc_cd_prvdr_lic_alpha, hdr.ddc_cd_prvdr_lic_nmrc) AS PROV_LCNS_CD, 
                                                hdr.ddc_cd_tot_chrg_amt as ddc_cd_tot_chrg_amt,
                                                hdr.ddc_cd_med_rec_nbr_2 as ddc_cd_med_rec_nbr_2, 
                                                hdr.ddc_cd_med_rec_nbr as ddc_cd_med_rec_nbr, 
                                                hdr.ddc_cd_icda_cde_1 as ddc_cd_icda_cde_1, 
                                                hdr.ddc_cd_icda_cde_2 as ddc_cd_icda_cde_2, 
                                                hdr.ddc_cd_icda_cde_3 as ddc_cd_icda_cde_3, 
                                                hdr.ddc_cd_icda_cde_4 as ddc_cd_icda_cde_4, 
                                                hdr.ddc_cd_icda_cde_5 as ddc_cd_icda_cde_5, 
                                                CASE 
                                                  WHEN ( hdr.ddc_cd_its_home_ind = 'Y' 
                                                         AND hdr.ddc_cd_its_orig_sccf_nbr_new <> '*' 
                                                         AND hdr.ddc_cd_its_host_prvdr_ind IN ( 'P', 'Y' ) ) 
                                                        OR hdr.ddc_cd_prvdr_ind NOT IN ( 'D', 'N' ) THEN 'PAR' 
                                                  ELSE 'NON-PAR' 
                                                END AS PRVDR_STATUS, 
                                                CASE 
                                                  WHEN hdr.ddc_cd_clm_type IN ( 'MA', 'PA', 'PC', 'MM', 'PM' ) THEN 
                                                  'PROF' 
                                                  WHEN hdr.ddc_cd_clm_type IN ( 'IA', 'IC', 'ID' ) THEN 'INPT' 
                                                  WHEN hdr.ddc_cd_clm_type IN ( 'OA', 'OC', 'OD' ) THEN 'OUTPT' 
                                                  WHEN hdr.ddc_cd_clm_type IN ( 'SA', 'SC' ) THEN 'SN' 
                                                  ELSE hdr.ddc_cd_clm_type 
                                                END AS CLAIM_TYPE, 
                                                hdr.load_ingstn_id as load_ingstn_id,
                                                hdr.load_dtm as load_dtm
                                                FROM ClmWgsGncclmpDf hdr 
                                                WHERE  hdr.ddc_cd_itm_cde = '80' 
                                                AND hdr.ddc_cd_clm_compl_dte >= Add_months(CURRENT_DATE, -36) """
												
												
												
 				val clmHeaderDF = spark.sql(clmwgsgncclmpSQL).repartition(1000)

				val clmwgsgncdtlpSQL = """select 
                                                dtl.gnchiios_hclm_dcn as gnchiios_hclm_dcn,
                                                dtl.gnchiios_hclm_item_cde as gnchiios_hclm_item_cde,
                                                dtl.ddc_dtl_lne_nbr as ddc_dtl_lne_nbr, 
                                                dtl.ddc_dtl_icda_pntr_1 as ddc_dtl_icda_pntr_1, 
                                                dtl.ddc_dtl_prcdr_cde as ddc_dtl_prcdr_cde, 
                                                dtl.ddc_dtl_svc_cde_1_3 as ddc_dtl_svc_cde_1_3, 
                                                dtl.ddc_dtl_proc_svc_cls_1 as ddc_dtl_proc_svc_cls_1, 
                                                dtl.ddc_dtl_proc_svc_cls_2 as ddc_dtl_proc_svc_cls_2, 
                                                dtl.ddc_dtl_proc_svc_cls_3 as ddc_dtl_proc_svc_cls_3, 
                                                dtl.ddc_dtl_pcodec_hcpcs_cde as ddc_dtl_pcodec_hcpcs_cde, 
                                                dtl.ddc_dtl_blld_amt as ddc_dtl_blld_amt, 
                                                dtl.ddc_dtl_unts_occur as ddc_dtl_unts_occur, 
                                                dtl.ddc_dtl_units_occur as ddc_dtl_units_occur, 
                                                dtl.ddc_dtl_prcdr_modfr_cde as ddc_dtl_prcdr_modfr_cde, 
                                                dtl.ddc_dtl_pcodec_hcpcs_mod as ddc_dtl_pcodec_hcpcs_mod, 
                                                dtl.ddc_dtl_mod_cde_1 as ddc_dtl_mod_cde_1, 
                                                dtl.ddc_dtl_mod_cde_2 as ddc_dtl_mod_cde_2, 
                                                dtl.ddc_dtl_mod_cde_3 as ddc_dtl_mod_cde_3, 
                                                dtl.ddc_dtl_hcfa_pt_cde as ddc_dtl_hcfa_pt_cde, 
                                                dtl.ddc_dtl_pt_cde as ddc_dtl_pt_cde, 
                                                dtl.ddc_dtl_elig_expsn_amt as ddc_dtl_elig_expsn_amt, 
                                                dtl.ddc_dtl_svc_from_dte AS SRVC_FROM_DT_DTL, 
                                                dtl.ddc_dtl_svc_thru_dte AS SRVC_TO_DT_DTL
                                                from ClmWgsGncdtlpDf dtl where dtl.gnchiios_hclm_item_cde='80'"""
				
					val clmDetailDF = spark.sql(clmwgsgncdtlpSQL).repartition(1000)

					
					val clmwgsgncnatpea2SQL = """SELECT
                                                EA2.ddc_nat_ea2_type_of_bill,
                                                EA2.gnchiios_hclm_dcn,
                                                EA2.gnchiios_hclm_dcn_cc,
                                                EA2.gnchiios_hclm_item_cde 
                                                FROM 
                                                ClmWgsGncnatpEa2Df EA2 
                                                WHERE EA2.gnchiios_hclm_item_cde = '80' """
					
					val clmgncnatpea2DF = spark.sql(clmwgsgncnatpea2SQL).repartition(1000)
					
					var bdfCombineClaimsDF = clmHeaderDF.as("header").join(clmDetailDF.as("detail"), $"header.ddc_cd_dcn" === $"gnchiios_hclm_dcn", "inner")
					.join(clmgncnatpea2DF.as("ea2"), $"header.ddc_cd_dcn" === $"ea2.gnchiios_hclm_dcn" and $"header.ddc_cd_dcn_cc" === $"ea2.gnchiios_hclm_dcn_cc", "inner")
					.select("header.ddc_cd_dcn", "header.ddc_cd_itm_cde", "header.ddc_cd_clm_compl_dte", "header.ddc_cd_clm_pay_act_1", "header.ddc_cd_clm_pay_act_2_6", "header.member_id",
                    "header.ddc_cd_pat_mbr_cde", "header.ddc_cd_grp_nbr", "header.ddc_cd_svc_from_dte", "header.ddc_cd_svc_thru_dte", "header.ddc_cd_prvdr_tax_id",
                    "header.ddc_cd_prvdr_nme", "header.ddc_cd_prvdr_sec_nme", "header.ddc_cd_prvdr_spclty_cde", "header.prov_lcns_cd", "header.ddc_cd_tot_chrg_amt",
                    "ea2.ddc_nat_ea2_type_of_bill", "header.ddc_cd_med_rec_nbr_2", "header.ddc_cd_med_rec_nbr", "header.ddc_cd_icda_cde_1", "header.ddc_cd_icda_cde_2",
                    "header.ddc_cd_icda_cde_3", "header.ddc_cd_icda_cde_4", "header.ddc_cd_icda_cde_5", "header.prvdr_status", "header.claim_type",
                    "detail.ddc_dtl_lne_nbr", "detail.ddc_dtl_icda_pntr_1", "detail.ddc_dtl_prcdr_cde", "detail.ddc_dtl_svc_cde_1_3", "detail.ddc_dtl_proc_svc_cls_1",
                    "detail.ddc_dtl_proc_svc_cls_2", "detail.ddc_dtl_proc_svc_cls_3", "detail.ddc_dtl_pcodec_hcpcs_cde", "detail.ddc_dtl_blld_amt", "detail.ddc_dtl_unts_occur",
                    "detail.ddc_dtl_units_occur", "detail.ddc_dtl_prcdr_modfr_cde", "detail.ddc_dtl_pcodec_hcpcs_mod", "detail.ddc_dtl_mod_cde_1", "detail.ddc_dtl_mod_cde_2",
                    "detail.ddc_dtl_mod_cde_3", "detail.ddc_dtl_hcfa_pt_cde", "detail.ddc_dtl_pt_cde", "detail.ddc_dtl_elig_expsn_amt", "detail.srvc_from_dt_dtl",
                    "detail.srvc_to_dt_dtl", "header.load_ingstn_id", "header.load_dtm").repartition(2000)
					
					

					var bdfClaimsPartition = Window.partitionBy("ddc_cd_dcn", "ddc_cd_itm_cde", "ddc_dtl_lne_nbr").orderBy(col("load_dtm").desc)
					
					bdfCombineClaimsDF = bdfCombineClaimsDF.withColumn("rank", rank().over(bdfClaimsPartition)).filter("rank=1")


					rowCount = bdfCombineClaimsDF.count()
					info(s"[COGX]INFO: CogX Row Count => " + rowCount)

					var CogaClaimDataset = bdfCombineClaimsDF.as[CogxClaimRecord]

					var groupCogxClaimBDFHbaseDataSet = CogaClaimDataset.groupByKey { key =>
					(key.member_id, key.ddc_cd_pat_mbr_cde, key.ddc_cd_svc_from_dte, key.ddc_cd_svc_thru_dte)
					}

					val groupedmemberHbaseDataSet1 = groupCogxClaimBDFHbaseDataSet.mapGroups((k, iter) => {
					var cogxClaimSet = Set[CogxClaimRecord]()
					val cogxClaimInfo = iter.map(cogx => new cogxClaimInfo(
					new CogxClaimRecord(
                    cogx.ddc_cd_dcn, cogx.ddc_cd_itm_cde, cogx.ddc_cd_clm_compl_dte, cogx.ddc_cd_clm_pay_act_1, cogx.ddc_cd_clm_pay_act_2_6, cogx.member_id, cogx.ddc_cd_pat_mbr_cde, cogx.ddc_cd_grp_nbr, cogx.ddc_cd_svc_from_dte, cogx.ddc_cd_svc_thru_dte, cogx.ddc_cd_prvdr_tax_id,
                    cogx.ddc_cd_prvdr_nme, cogx.ddc_cd_prvdr_sec_nme, cogx.ddc_cd_prvdr_spclty_cde, cogx.prov_lcns_cd, cogx.ddc_cd_tot_chrg_amt,
                    cogx.ddc_nat_ea2_type_of_bill, cogx.ddc_cd_med_rec_nbr_2, cogx.ddc_cd_med_rec_nbr, cogx.ddc_cd_icda_cde_1, cogx.ddc_cd_icda_cde_2,
                    cogx.ddc_cd_icda_cde_3, cogx.ddc_cd_icda_cde_4, cogx.ddc_cd_icda_cde_5, cogx.prvdr_status, cogx.claim_type,
                    cogx.ddc_dtl_lne_nbr, cogx.ddc_dtl_icda_pntr_1, cogx.ddc_dtl_prcdr_cde, cogx.ddc_dtl_svc_cde_1_3, cogx.ddc_dtl_proc_svc_cls_1,
                    cogx.ddc_dtl_proc_svc_cls_2, cogx.ddc_dtl_proc_svc_cls_3, cogx.ddc_dtl_pcodec_hcpcs_cde, cogx.ddc_dtl_blld_amt, cogx.ddc_dtl_unts_occur,
                    cogx.ddc_dtl_units_occur, cogx.ddc_dtl_prcdr_modfr_cde, cogx.ddc_dtl_pcodec_hcpcs_mod, cogx.ddc_dtl_mod_cde_1, cogx.ddc_dtl_mod_cde_2,
                    cogx.ddc_dtl_mod_cde_3, cogx.ddc_dtl_hcfa_pt_cde, cogx.ddc_dtl_pt_cde, cogx.ddc_dtl_elig_expsn_amt, cogx.srvc_from_dt_dtl,
                    cogx.srvc_to_dt_dtl, cogx.load_ingstn_id))).toArray

                    for (claim <- cogxClaimInfo) {
                      cogxClaimSet += claim.cogxClaimdata
                    }

					val cogxClaim = new cogxClaimHistory(cogxClaimSet.toArray)

					(String.valueOf(k), cogxClaim)
					}).repartition(2000)

					val DF3 = groupedmemberHbaseDataSet1.map {
					case (key, value) => {
                    val digest = DigestUtils.md5Hex(String.valueOf(key))
                    val rowKey = new StringBuilder(digest.substring(0, 8)).append(key.replaceAll("(", "").replaceAll(")", "")).toString()
                    val holder = asJSONString(value)
                     ((rowKey),(holder))
        }
      }
					
					val df1WithAuditColumn =  DF3.toDF("rowKey", "jsonData")
					
					var dataMap = Map(teradata_table_name->df1WithAuditColumn)
          return dataMap
					
					
	}

	def writeData(outDFs: Map[String, DataFrame]): Unit = {
			
	  val df1 = outDFs.getOrElse(teradata_table_name, null).toDF("rowKey", "jsonData")
	  
	  val putRDD = df1.rdd.map(x =>  {
          val rowKey = x.getAs[String]("rowKey")
          val holder = x.getAs[String]("jsonData")
          val p = new Put(Bytes.toBytes(rowKey))
          p.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes(holder))
          (new org.apache.hadoop.hbase.io.ImmutableBytesWritable, p)
        }
      )

      val hbaseTable = config.getString("hbase_schema") + ":" + config.getString("hbase_table_name")
      new PairRDDFunctions(putRDD).saveAsNewAPIHadoopDataset(getMapReduceJobConfiguration(hbaseTable))

	}


}