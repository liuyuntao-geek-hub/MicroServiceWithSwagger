package com.anthem.hpip.renderingSpend

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.max
import org.apache.spark.sql.functions.substring
import org.apache.spark.sql.functions.when
import org.apache.spark.sql.functions.year
import org.apache.spark.sql.Row
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.{ DateTimeZone }
import org.joda.time.format.DateTimeFormat
import org.apache.spark.sql.functions.current_timestamp
import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.OperationSession
import com.anthem.hpip.helper.Operator
import org.apache.spark.sql.functions.lit
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer
import com.anthem.hpip.util.DateUtils

case class hpipAudit(program: String, user_id: String, app_id: String, start_time: String, app_duration: String, status: String)

class RenderingSpendOperation(configPath: String, env: String, queryFileCategory: String) extends OperationSession(configPath, env, queryFileCategory) with Operator {
 

  import spark.implicits._

  var datafrmeArray = ArrayBuffer[DataFrame]()
  var dataFrameArray = new ArrayBuffer[DataFrame]()
  var columnMapIndexValue = Map[String, Int]()

   //Audit 
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time:DateTime = DateTime.now()
  var start = ""
  
  var listBuffer = ListBuffer[hpipAudit]()

  @Override def beforeLoadData() {

    program = sc.appName
    user_id = sc.sparkUser
    app_id = sc.applicationId
   
    start_time = DateTime.now()
    start = DateUtils.getCurrentDateTime
      
    val warehouseHiveDB = config.getString("warehouse-hive-db")

    listBuffer += hpipAudit(program, user_id, app_id, start, "0min", "Started")
    val hpipAuditDF = listBuffer.toDF().withColumn("last_updt_dtm", lit(current_timestamp()))
    hpipAuditDF.printSchema()
    hpipAuditDF.write.insertInto(warehouseHiveDB + """.""" + "hpip_audit")
  }

  @Override def afterWriteData() {
    val warehouseHiveDB = config.getString("warehouse-hive-db")
    var listBuffer = ListBuffer[hpipAudit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += hpipAudit(program, user_id, app_id, start, duration, "completed")
    val hpipAuditDF = listBuffer.toDF().withColumn("last_updt_dtm", current_timestamp())
    hpipAuditDF.printSchema()
    hpipAuditDF.show
    hpipAuditDF.write.insertInto(warehouseHiveDB + """.""" + "hpip_audit")
  }
  def loadData(): Map[String, DataFrame] = {

			//Reading the data into Data frames
			val startTime = DateTime.now
					info(s"[HPIP-ETL] The loading of Data started with start time at :  + $startTime")

					val mtclmQuery = config.getString("query_mtclm").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
					info(s"[HPIP-ETL] Query for reading data from CLM table is $mtclmQuery")
					val clmLineQuery = config.getString("query_clm_line").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
					info(s"[HPIP-ETL] Query for reading data from CLM table is $clmLineQuery")
					val mtclmCoaQuery = config.getString("query_mtclm_coa").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
					info(s"[HPIP-ETL] Query for reading data from CLM table is $mtclmCoaQuery")
					val clmLinePrvQuery = config.getString("query_clm_line_prv").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
					info(s"[HPIP-ETL] Query for reading data from CLM table is $clmLinePrvQuery")
					val clmPrvQuery = config.getString("query_clm_prov").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
					info(s"[HPIP-ETL] Query for reading data from CLM table is $clmPrvQuery")
					val prlQuery = config.getString("query_prl").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
					info(s"[HPIP-ETL] Query for reading data from CLM table is $prlQuery")
					val taxIdProvQuery = config.getString("query_tax_id_prov").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
					info(s"[HPIP-ETL] Query for reading data from CLM table is $taxIdProvQuery")
					val taxIdQuery = config.getString("query_tax_id").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
					info(s"[HPIP-ETL] Query for reading data from CLM table is $taxIdQuery")

					//Reading queries to be triggered from config file
					info(s"[HPIP-ETL]Reading the queries from config file")

					val mtclmDF = spark.sql(mtclmQuery).repartition(200)
					val clmLineDF = spark.sql(clmLineQuery).repartition(200)
					val mtclmCoaDF = spark.sql(mtclmCoaQuery).repartition(200)
					val clmLineProvDF = spark.sql(clmLinePrvQuery).repartition(200)
					val clmProvDF = spark.sql(clmPrvQuery).repartition(200)
					val prlDF = spark.sql(prlQuery).repartition(200)
					val taxIdProvDF = spark.sql(taxIdProvQuery).repartition(200)
					val taxIdDF = spark.sql(taxIdQuery).repartition(200)

					val mapDF = Map("mtclm" -> mtclmDF, "clm_line" -> clmLineDF, "mtclm_coa" -> mtclmCoaDF, "clm_line_prov" -> clmLineProvDF, "clm_prov" -> clmProvDF, "prl" -> prlDF, "tax_id_prov" -> taxIdProvDF, "tax_id" -> taxIdDF)

					info(s"[HPIP-ETL] Loading data completed at : " + DateTime.now())
					info(s"[HPIP-ETL] Time Taken for loading the data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
					mapDF
	}

	def processData(inMapDF: Map[String, DataFrame]): Map[String, DataFrame] = {

			val startTime = DateTime.now
					info(s"[HPIP-ETL] Processing Data Started: $startTime")
					//Processing the data
					//Reading the data frames as elements from Map

					val mtclmDF = inMapDF.getOrElse("mtclm", null)
					val clmLineDF = inMapDF.getOrElse("clm_line", null)
					val mtclmCoaDF = inMapDF.getOrElse("mtclm_coa", null)
					val clmLineProvDF = inMapDF.getOrElse("clm_line_prov", null)
					val clmProvDF = inMapDF.getOrElse("clm_prov", null)
					val prlDF = inMapDF.getOrElse("prl", null)
					val taxIdProvDF = inMapDF.getOrElse("tax_id_prov", null)
					val taxIdDF = inMapDF.getOrElse("tax_id", null)

					val paidDt = config.getString("paid_dt")
					info(s"[HPIP-ETL] The Paid Date value read from config file is paidDt =  $paidDt")

					val effdt12 = config.getString("effdt12")
					info(s"[HPIP-ETL] The effective Date value read from config file is effdt12 =  $effdt12")

					val enddt = config.getString("enddt")
					info(s"[HPIP-ETL] The end Date value read from config file is enddt =  $enddt")

					//Creating a List of pol codes,Non8xxxxLabCodes

					val pathForPolCodes = config.getString("path_PolCodes")
					val PolCodes: List[String] = sc.textFile(pathForPolCodes).collect.toList
					info(s"[HPIP-ETL] PolCodes : $PolCodes ")
					println(s"[HPIP-ETL] PolCodes : $PolCodes ")

					val pathForNon8xxxxLabCodes = config.getString("path_Non8xxxxLabCodes")
					val Non8xxxxLabCodes: List[String] = sc.textFile(pathForNon8xxxxLabCodes).collect.toList
					info(s"[HPIP-ETL] Non8xxxxLabCodes : $Non8xxxxLabCodes ")
					println(s"[HPIP-ETL] Non8xxxxLabCodes : $Non8xxxxLabCodes ")

					//For creating &userid..CLMKEY_tmp ( First volatile table in SAS script )

					info(s"For creating &userid..CLMKEY_tmp ( First volatile table in SAS script )")
					println(s"For creating &userid..CLMKEY_tmp ( First volatile table in SAS script )")

					val mtclmFiltered = mtclmDF.filter(mtclmDF("srvc_rndrg_type_cd").isin("PHYSN", "PANCL", "HOSP", "FANCL"))
					info(s"[HPIP-ETL] mtclmFiltered :  ")

					val clmLineFiltered = clmLineDF.filter(clmLineDF("clm_line_stts_cd").isin("APRVD", "PD", "APRZB", "PDZB", "VOID") && (((substring(clmLineDF("hlth_srvc_cd"), 1, 1)) === "8") && clmLineDF("hlth_srvc_cd").isin(PolCodes: _*) === false) || clmLineDF("hlth_srvc_cd").isin(Non8xxxxLabCodes: _*))
					info(s"[HPIP-ETL] clmLineFiltered :  ")

					val mtclmCoaFiltered = mtclmCoaDF.filter((substring(mtclmCoaDF("prod_cf_cd"), 1, 1).isin("M", "P", "B") || mtclmCoaDF("prod_cf_cd").isin("TPPMC", "TADVZ", "TAOPZ")) && ((mtclmCoaDF("prod_cf_cd").isin("BSEAT", "BSVEA")) === false))
					info(s"[HPIP-ETL] mtclmCoaFiltered :  ")

					val mtclmDFJoinClmLineDF = mtclmFiltered.join(clmLineFiltered, mtclmFiltered("clm_adjstmnt_key") === clmLineFiltered("clm_adjstmnt_key") && mtclmFiltered("adjdctn_dt") === clmLineFiltered("adjdctn_dt") && mtclmFiltered("clm_sor_cd") === clmLineFiltered("clm_sor_cd"), "inner").select(mtclmFiltered("clm_adjstmnt_key"), mtclmFiltered("clm_sor_cd"), clmLineFiltered("clm_line_nbr"), mtclmFiltered("adjdctn_dt"), mtclmFiltered("srvc_rndrg_type_cd"), mtclmFiltered("gl_post_dt"), clmLineFiltered("clm_line_stts_cd"), clmLineFiltered("inpat_cd"), clmLineFiltered("clm_line_srvc_strt_dt"), clmLineFiltered("hlth_srvc_cd"), mtclmFiltered("admt_dt"), mtclmFiltered("src_billg_tax_id"))
					info(s"[HPIP-ETL] mtclmDFJoinClmLineDF : ")

					val mtclmDFJoinClmLineDFJoinMtclmCoaDF = mtclmDFJoinClmLineDF.join(mtclmCoaFiltered, mtclmDFJoinClmLineDF("clm_adjstmnt_key") === mtclmCoaFiltered("clm_adjstmnt_key") && mtclmDFJoinClmLineDF("clm_line_nbr") === mtclmCoaFiltered("clm_line_nbr"), "left")
					info(s"[HPIP-ETL] mtclmDFJoinClmLineDFJoinMtclmCoaDF : ")

					val clmkeyTmp = mtclmDFJoinClmLineDFJoinMtclmCoaDF.select(mtclmDFJoinClmLineDF("clm_adjstmnt_key"), mtclmDFJoinClmLineDF("clm_sor_cd"), mtclmDFJoinClmLineDF("clm_line_nbr"), mtclmDFJoinClmLineDF("adjdctn_dt"), mtclmDFJoinClmLineDF("srvc_rndrg_type_cd"), mtclmDFJoinClmLineDF("gl_post_dt"), mtclmDFJoinClmLineDF("clm_line_stts_cd"), mtclmDFJoinClmLineDF("inpat_cd"), mtclmDFJoinClmLineDF("clm_line_srvc_strt_dt"), mtclmDFJoinClmLineDF("hlth_srvc_cd"), mtclmCoaFiltered("mbu_cf_cd"), mtclmCoaFiltered("prod_cf_cd"), mtclmDFJoinClmLineDF("admt_dt"), mtclmDFJoinClmLineDF("src_billg_tax_id"))
					info(s"[HPIP-ETL] clmkeyTmp :  ")

					val clmkeyTmpFiltered = clmkeyTmp.filter((when((clmkeyTmp("inpat_cd") === "Y") && (clmkeyTmp("srvc_rndrg_type_cd").isin("HOSP", "FANCL")) && (year(clmkeyTmp("admt_dt")).!==(8888)) && (clmkeyTmp("mbu_cf_cd").!==("SRVAZZ") && clmkeyTmp("prod_cf_cd").!==("MFVHR")), clmkeyTmp("admt_dt")).otherwise(clmkeyTmp("clm_line_srvc_strt_dt"))).between(effdt12, enddt))
					info(s"[HPIP-ETL] clmkeyTmpFiltered :  ")

					val clmkeyTmpFilteredSelect = clmkeyTmpFiltered.select(clmkeyTmpFiltered("clm_adjstmnt_key"), clmkeyTmpFiltered("clm_sor_cd")).distinct
					info(s"[HPIP-ETL] clmkeyTmpFilteredSelect :  ")

					//For creating &sysuserid.._tmp_TINs_pass1
					//DAta frames joining mtclm,clm_line and mtclm_coa are not same as above 

					val mtclmFilteredForPass1 = mtclmDF.filter(mtclmDF("srvc_rndrg_type_cd").isin("PHYSN", "PANCL", "HOSP", "FANCL", "PHMCY"))
					info(s"[HPIP-ETL] mtclmFilteredForPass1 :  ")

					val clmLineFilteredForPass1 = clmLineDF.filter(clmLineDF("clm_line_stts_cd").isin("APRVD", "PD", "APRZB", "PDZB", "VOID") && (clmLineDF("hlth_srvc_cd").!==("ADA")) && (clmLineDF("clm_line_encntr_cd").isin("N", "NA")) && (clmLineDF("clm_line_srvc_strt_dt")).between(effdt12, enddt) && (((substring(clmLineDF("hlth_srvc_cd"), 1, 1)) === "8") && clmLineDF("hlth_srvc_cd").isin(PolCodes: _*) === false) || clmLineDF("hlth_srvc_cd").isin(Non8xxxxLabCodes: _*))
					info(s"[HPIP-ETL] clmLineFilteredForPass1 :  ")

					val mtclmDFJoinClmLineDFForPass1 = mtclmFilteredForPass1.join(clmLineFilteredForPass1, mtclmFilteredForPass1("clm_adjstmnt_key") === clmLineFilteredForPass1("clm_adjstmnt_key") && mtclmFilteredForPass1("adjdctn_dt") === clmLineFilteredForPass1("adjdctn_dt") && mtclmFilteredForPass1("clm_sor_cd") === clmLineFilteredForPass1("clm_sor_cd"), "inner").select(mtclmFilteredForPass1("clm_adjstmnt_key"), mtclmFilteredForPass1("clm_sor_cd"), clmLineFilteredForPass1("clm_line_nbr"), mtclmFilteredForPass1("adjdctn_dt"), mtclmFilteredForPass1("srvc_rndrg_type_cd"), mtclmFilteredForPass1("gl_post_dt"), clmLineFilteredForPass1("clm_line_stts_cd"), clmLineFilteredForPass1("inpat_cd"), clmLineFilteredForPass1("clm_line_srvc_strt_dt"), clmLineFilteredForPass1("hlth_srvc_cd"), mtclmFilteredForPass1("admt_dt"), mtclmFilteredForPass1("src_billg_tax_id"), clmLineFilteredForPass1("hlth_srvc_type_cd"), clmLineFilteredForPass1("clm_line_encntr_cd"), mtclmFilteredForPass1("clm_its_host_cd"))
					info(s"[HPIP-ETL] mtclmDFJoinClmLineDFForPass1 :  ")

					info(s"[HPIP-ETL] Unpersisting the dataframes ")
					println(s"[HPIP-ETL] unpersisting the dataframes ")

					mtclmDF.unpersist()
					clmLineDF.unpersist()
					mtclmCoaDF.unpersist()

					val mtclmDFJoinClmLineDFJoinMtclmCoaDFForPass1 = mtclmDFJoinClmLineDFForPass1.join(mtclmCoaFiltered, mtclmDFJoinClmLineDFForPass1("clm_adjstmnt_key") === mtclmCoaFiltered("clm_adjstmnt_key") && mtclmDFJoinClmLineDFForPass1("clm_line_nbr") === mtclmCoaFiltered("clm_line_nbr"), "left")
					info(s"[HPIP-ETL] mtclmDFJoinClmLineDFJoinMtclmCoaDFForPass1 :  ")

					val clmkeyTmpForPass1 = mtclmDFJoinClmLineDFJoinMtclmCoaDFForPass1.select(mtclmDFJoinClmLineDFForPass1("clm_adjstmnt_key"), mtclmDFJoinClmLineDFForPass1("clm_sor_cd"), mtclmDFJoinClmLineDFForPass1("clm_line_nbr"), mtclmDFJoinClmLineDFForPass1("adjdctn_dt"), mtclmDFJoinClmLineDFForPass1("srvc_rndrg_type_cd"), mtclmDFJoinClmLineDFForPass1("gl_post_dt"), mtclmDFJoinClmLineDFForPass1("clm_line_stts_cd"), mtclmDFJoinClmLineDFForPass1("inpat_cd"), mtclmDFJoinClmLineDFForPass1("clm_line_srvc_strt_dt"), mtclmDFJoinClmLineDFForPass1("hlth_srvc_cd"), mtclmCoaFiltered("mbu_cf_cd"), mtclmCoaFiltered("prod_cf_cd"), mtclmDFJoinClmLineDFForPass1("admt_dt"), mtclmDFJoinClmLineDFForPass1("src_billg_tax_id"), mtclmDFJoinClmLineDFForPass1("hlth_srvc_type_cd"), mtclmDFJoinClmLineDFForPass1("clm_line_encntr_cd"), mtclmDFJoinClmLineDFForPass1("clm_its_host_cd"))
					info(s"[HPIP-ETL] clmkeyTmpForPass1 :  ")

					val joinclmKeyTmpForPass1 = clmkeyTmpForPass1.join(clmkeyTmpFilteredSelect, clmkeyTmpForPass1("clm_adjstmnt_key") === clmkeyTmpFilteredSelect("clm_adjstmnt_key") && clmkeyTmpForPass1("clm_sor_cd") === clmkeyTmpFilteredSelect("clm_sor_cd")).select(clmkeyTmpForPass1("clm_adjstmnt_key"), clmkeyTmpForPass1("clm_sor_cd"), clmkeyTmpForPass1("clm_line_nbr"), clmkeyTmpForPass1("adjdctn_dt"), clmkeyTmpForPass1("srvc_rndrg_type_cd"), clmkeyTmpForPass1("gl_post_dt"), clmkeyTmpForPass1("clm_line_stts_cd"), clmkeyTmpForPass1("inpat_cd"), clmkeyTmpForPass1("clm_line_srvc_strt_dt"), clmkeyTmpForPass1("hlth_srvc_cd"), clmkeyTmpForPass1("mbu_cf_cd"), clmkeyTmpForPass1("prod_cf_cd"), clmkeyTmpForPass1("admt_dt"), clmkeyTmpForPass1("src_billg_tax_id"), clmkeyTmpForPass1("hlth_srvc_type_cd"), clmkeyTmpForPass1("clm_line_encntr_cd"), clmkeyTmpForPass1("clm_its_host_cd"))
					info(s"[HPIP-ETL] joinclmKeyTmpForPass1 :  ")

					val joinclmKeyTmpForPass1JoinClmProv = joinclmKeyTmpForPass1.join(clmProvDF, joinclmKeyTmpForPass1("clm_sor_cd") === clmProvDF("clm_sor_cd") && joinclmKeyTmpForPass1("clm_adjstmnt_key") === clmProvDF("clm_adjstmnt_key") && clmProvDF("clm_prov_id_type_cd") === "TAX", "left").select(joinclmKeyTmpForPass1("clm_adjstmnt_key"), joinclmKeyTmpForPass1("clm_sor_cd"), joinclmKeyTmpForPass1("clm_line_nbr"), joinclmKeyTmpForPass1("adjdctn_dt"), joinclmKeyTmpForPass1("srvc_rndrg_type_cd"), joinclmKeyTmpForPass1("gl_post_dt"), joinclmKeyTmpForPass1("clm_line_stts_cd"), joinclmKeyTmpForPass1("inpat_cd"), joinclmKeyTmpForPass1("clm_line_srvc_strt_dt"), joinclmKeyTmpForPass1("hlth_srvc_cd"), joinclmKeyTmpForPass1("mbu_cf_cd"), joinclmKeyTmpForPass1("prod_cf_cd"), joinclmKeyTmpForPass1("admt_dt"), joinclmKeyTmpForPass1("src_billg_tax_id"), clmProvDF("clm_prov_role_cd"), joinclmKeyTmpForPass1("hlth_srvc_type_cd"), joinclmKeyTmpForPass1("clm_line_encntr_cd"), clmProvDF("src_clm_prov_id"), joinclmKeyTmpForPass1("clm_its_host_cd"))
					info(s"[HPIP-ETL] joinclmKeyTmpForPass1JoinClmProv :  ")

					val withColumnTin = joinclmKeyTmpForPass1JoinClmProv.withColumn("tin", when(joinclmKeyTmpForPass1JoinClmProv("src_billg_tax_id").between("000000001", "999999998"), (joinclmKeyTmpForPass1JoinClmProv("src_billg_tax_id"))) when (substring(joinclmKeyTmpForPass1JoinClmProv("src_clm_prov_id"), 1, 9).between("000000001", "999999998") && joinclmKeyTmpForPass1JoinClmProv("clm_prov_role_cd") === "04", (substring(joinclmKeyTmpForPass1JoinClmProv("src_clm_prov_id"), 1, 9))) when (substring(joinclmKeyTmpForPass1JoinClmProv("src_clm_prov_id"), 1, 9).between("000000001", "999999998") && joinclmKeyTmpForPass1JoinClmProv("clm_prov_role_cd") === "10", (substring(joinclmKeyTmpForPass1JoinClmProv("src_clm_prov_id"), 1, 9))) when (joinclmKeyTmpForPass1JoinClmProv("clm_sor_cd") === 896 && (substring(joinclmKeyTmpForPass1JoinClmProv("src_clm_prov_id"), 1, 9).between("000000001", "999999998")) && joinclmKeyTmpForPass1JoinClmProv("clm_prov_role_cd") === "10", (substring(joinclmKeyTmpForPass1JoinClmProv("src_clm_prov_id"), 1, 9))) otherwise (""))
					info(s"[HPIP-ETL] withColumnTin :  ")

					val tmpTinsPass1 = withColumnTin.filter(withColumnTin("srvc_rndrg_type_cd").isin("PHYSN", "PANCL", "HOSP", "FANCL") && withColumnTin("clm_line_stts_cd").isin("APRVD", "PD", "APRZB", "PDZB", "VOID") && withColumnTin("hlth_srvc_type_cd").!==("ADA") && withColumnTin("clm_line_encntr_cd").isin("N", "NA") && withColumnTin("clm_line_srvc_strt_dt").between(effdt12, enddt) && withColumnTin("gl_post_dt") <= paidDt && ((substring(withColumnTin("prod_cf_cd"), 1, 1).isin("M", "P", "B") || withColumnTin("prod_cf_cd").isin("TPPMC", "TADVZ", "TAOPZ")) && (withColumnTin("prod_cf_cd").isin("BSEAT", "BSVEA") === false)))
					info(s"[HPIP-ETL] tmpTinsPass1 :  ")

					val tmpTinsPass1Select = tmpTinsPass1.select(tmpTinsPass1("clm_adjstmnt_key"), tmpTinsPass1("clm_sor_cd"), tmpTinsPass1("clm_its_host_cd"), tmpTinsPass1("clm_line_srvc_strt_dt"), tmpTinsPass1("tin")).distinct
					info(s"[HPIP-ETL] tmpTinsPass1Select :  ")

					val tmpTinsPass1JoinclmLineProv = tmpTinsPass1.join(clmLineProvDF, tmpTinsPass1("clm_sor_cd") === clmLineProvDF("clm_sor_cd") && tmpTinsPass1("clm_adjstmnt_key") === clmLineProvDF("clm_adjstmnt_key") && clmLineProvDF("clm_line_prov_role_cd") === "09" && (clmLineProvDF("prov_id").isin("UNK", "NA", "") === false) && (clmLineProvDF("prov_sor_cd").isin("UNK", "NA", "") === false), "inner").filter((tmpTinsPass1("tin") >= 1) && (tmpTinsPass1("tin") <= 999999998)).select(tmpTinsPass1("clm_adjstmnt_key"), tmpTinsPass1("clm_sor_cd"), tmpTinsPass1("clm_its_host_cd"), tmpTinsPass1("clm_line_srvc_strt_dt"), clmLineProvDF("prov_id"), clmLineProvDF("prov_sor_cd"), clmLineProvDF("clm_line_prov_id_type_cd"), tmpTinsPass1("clm_its_host_cd"), tmpTinsPass1("tin")).distinct
					info(s"[HPIP-ETL] tmpTinsPass1JoinclmLineProv :  ")

					val tmpTinsForPass2 = tmpTinsPass1JoinclmLineProv

					info(s"[HPIP-ETL] tmpTinsForPass2 :  ")

					//For creating&sysuserid.._tmp_TINs_FOR_pass2_2 
					//combine the above one tmpTinsPass2 with tmpTinsPass2_2

					val tmpTinsForPass2_2 = tmpTinsForPass2.select(tmpTinsForPass2("clm_adjstmnt_key"), tmpTinsForPass2("clm_sor_cd"), tmpTinsForPass2("clm_its_host_cd"), tmpTinsForPass2("clm_line_srvc_strt_dt")).groupBy(tmpTinsForPass2("clm_adjstmnt_key"), tmpTinsForPass2("clm_sor_cd"), tmpTinsForPass2("clm_its_host_cd")).agg(max(tmpTinsForPass2("clm_line_srvc_strt_dt")).alias("clm_line_srvc_strt_dt"))
					info(s"[HPIP-ETL] tmpTinsForPass2_2 :  ")

					// For creating &FLDR2..tmp_TINs_pass2

					val tmpTinsJoinclmPrv = tmpTinsForPass2_2.join(clmLineProvDF, tmpTinsForPass2_2("clm_sor_cd") === clmLineProvDF("clm_sor_cd") && tmpTinsForPass2_2("clm_adjstmnt_key") === clmLineProvDF("clm_adjstmnt_key") && clmLineProvDF("clm_line_prov_role_cd") === "09" && tmpTinsForPass2_2("clm_line_srvc_strt_dt").between(effdt12, enddt), "left").select(clmLineProvDF("prov_id"), clmLineProvDF("prov_sor_cd"), tmpTinsForPass2_2("clm_adjstmnt_key"), tmpTinsForPass2_2("clm_sor_cd"), tmpTinsForPass2_2("clm_its_host_cd"), tmpTinsForPass2_2("clm_line_srvc_strt_dt"))
					info(s"[HPIP-ETL] tmpTinsJoinclmPrv :  ")

					val tmpTinsJoinclmPrvJoinPrl = tmpTinsJoinclmPrv.join(prlDF, tmpTinsJoinclmPrv("prov_id") === prlDF("prov_id") && tmpTinsJoinclmPrv("prov_sor_cd") === prlDF("prov_sor_cd") && prlDF("rcrd_stts_cd").!==("DEL") && (tmpTinsJoinclmPrv("clm_line_srvc_strt_dt").between(prlDF("prl_efctv_dt"), prlDF("prl_trmntn_dt"))) && (prlDF("prl_type_cd").isin("01", "26")), "left_outer").select(tmpTinsJoinclmPrv("prov_id"), tmpTinsJoinclmPrv("prov_sor_cd"), tmpTinsJoinclmPrv("clm_adjstmnt_key"), tmpTinsJoinclmPrv("clm_sor_cd"), tmpTinsJoinclmPrv("clm_its_host_cd"), tmpTinsJoinclmPrv("clm_line_srvc_strt_dt"), prlDF("parnt_prov_id"))
					info(s"[HPIP-ETL] tmpTinsForPass2 :  ")

					val tmpTinsJoinclmPrvJoinPrlJoinTIP = tmpTinsJoinclmPrvJoinPrl.join(taxIdProvDF, tmpTinsJoinclmPrvJoinPrl("parnt_prov_id") === taxIdProvDF("prov_id") && tmpTinsJoinclmPrvJoinPrl("prov_sor_cd") === taxIdProvDF("prov_sor_cd") && (tmpTinsJoinclmPrvJoinPrl("clm_line_srvc_strt_dt").between(taxIdProvDF("tax_id_prov_efctv_dt"), taxIdProvDF("tax_id_prov_trmntn_dt"))) && taxIdProvDF("tax_id_prov_efctv_dt") < taxIdProvDF("tax_id_prov_trmntn_dt") && taxIdProvDF("rcrd_stts_cd").!==("DEL"), "left_outer").select(tmpTinsJoinclmPrvJoinPrl("prov_id"), tmpTinsJoinclmPrvJoinPrl("prov_sor_cd"), tmpTinsJoinclmPrvJoinPrl("clm_adjstmnt_key"), tmpTinsJoinclmPrvJoinPrl("clm_sor_cd"), tmpTinsJoinclmPrvJoinPrl("clm_its_host_cd"), tmpTinsJoinclmPrvJoinPrl("clm_line_srvc_strt_dt"), tmpTinsJoinclmPrvJoinPrl("parnt_prov_id"), taxIdProvDF("tax_id_key"))
					info(s"[HPIP-ETL] tmpTinsForPass2 :  ")

					val tmpTinsJoinclmPrvJoinPrlJoinTIPJoinTI = tmpTinsJoinclmPrvJoinPrlJoinTIP.join(taxIdDF, tmpTinsJoinclmPrvJoinPrlJoinTIP("tax_id_key") === taxIdDF("tax_id_key") && tmpTinsJoinclmPrvJoinPrlJoinTIP("prov_sor_cd") === taxIdDF("prov_sor_cd") && tmpTinsJoinclmPrvJoinPrlJoinTIP("clm_line_srvc_strt_dt") <= taxIdDF("tax_id_trmntn_dt") && taxIdDF("rcrd_stts_cd").!==("DEL"), "left_outer")
					info(s"[HPIP-ETL] tmpTinsForPass2 :  ")

					val tmpTinsPass2 = tmpTinsJoinclmPrvJoinPrlJoinTIPJoinTI.select(tmpTinsJoinclmPrvJoinPrlJoinTIPJoinTI("clm_adjstmnt_key"), tmpTinsJoinclmPrvJoinPrlJoinTIPJoinTI("clm_sor_cd"), tmpTinsJoinclmPrvJoinPrlJoinTIPJoinTI("clm_its_host_cd"), taxIdDF("tax_id"), taxIdDF("tax_id_ein_or_ssn_cd")).groupBy(tmpTinsJoinclmPrvJoinPrlJoinTIPJoinTI("clm_adjstmnt_key"), tmpTinsJoinclmPrvJoinPrlJoinTIPJoinTI("clm_sor_cd"), tmpTinsJoinclmPrvJoinPrlJoinTIPJoinTI("clm_its_host_cd"), taxIdDF("tax_id_ein_or_ssn_cd")).agg(max(taxIdDF("tax_id")).alias("tax_id"))
					info(s"[HPIP-ETL] tmpTinsPass2 :  ")

					val tmpTinsPass2WithColumnTin = tmpTinsPass2.withColumn("tin", (when(tmpTinsPass2("tax_id").between("000000001", "999999998") && tmpTinsPass2("tax_id_ein_or_ssn_cd") === "E", (tmpTinsPass2("tax_id"))) when (tmpTinsPass2("tax_id").between("000000001", "999999998") && tmpTinsPass2("tax_id_ein_or_ssn_cd") === "S", (tmpTinsPass2("tax_id"))) otherwise ("")))
					info(s"[HPIP-ETL] tmpTinsForPass2 :  ")

					val tinCrosswlkHzDF_1 = tmpTinsPass2WithColumnTin.select((tmpTinsPass2WithColumnTin("clm_adjstmnt_key")), tmpTinsPass2WithColumnTin("clm_sor_cd"), tmpTinsPass2WithColumnTin("clm_its_host_cd"), tmpTinsPass2WithColumnTin("tin")).distinct()
					info(s"[HPIP-ETL] tinCrosswlkHzDF_1 :  ")

					val tinCrosswlkHzDF_1_filtered = tinCrosswlkHzDF_1.filter((tinCrosswlkHzDF_1("tin").isNotNull && (tinCrosswlkHzDF_1("tin").isin("UNK", "", "NA") === false)))
					info(s"[HPIP-ETL] tinCrosswlkHzDF_1_filtered :  ")

					val tinCrosswlkHzDF_2 = tmpTinsPass1.select((tmpTinsPass1("clm_adjstmnt_key")), tmpTinsPass1("clm_sor_cd"), tmpTinsPass1("clm_its_host_cd"), tmpTinsPass1("tin")).distinct()
					info(s"[HPIP-ETL] tinCrosswlkHzDF_2 :  ")

					val tinCrosswlkHzDF_2_filtered = tinCrosswlkHzDF_2.filter((tinCrosswlkHzDF_2("tin").isNotNull && (tinCrosswlkHzDF_2("tin").isin("UNK", "", "NA") === false)))
					info(s"[HPIP-ETL] tinCrosswlkHzDF_2_filtered :  ")

					val tinCrosswlkHzDFUnion = tinCrosswlkHzDF_1_filtered.union(tinCrosswlkHzDF_2_filtered)
					info(s"[HPIP-ETL] tinCrosswlkHzDFUnion :  ")
					
					val tinCrosswlkHzDF = tinCrosswlkHzDFUnion.select(tinCrosswlkHzDFUnion("clm_adjstmnt_key"), tinCrosswlkHzDFUnion("clm_its_host_cd"), tinCrosswlkHzDFUnion("tin"), tinCrosswlkHzDFUnion("clm_sor_cd")).distinct()
					info(s"[HPIP-ETL] tinCrosswlkHzDF :  ")
					
					val tinCrosswlkHzDF_NoDuplicates = tinCrosswlkHzDF.dropDuplicates(Array("clm_adjstmnt_key","clm_its_host_cd","tin","clm_sor_cd"))
					
					info(s"[HPIP-ETL] processing() Data Completed at: " + DateTime.now())
					info(s"[HPIP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

					val mapDF = Map("tmp_TINs_pass1" -> tmpTinsPass1Select, "tmp_TINs_pass2" -> tinCrosswlkHzDF_1, "tin_xwalk_hz" -> tinCrosswlkHzDF_NoDuplicates)
					mapDF
	}

	def writeData(map: Map[String, DataFrame]): Unit = {

			//Writing the dataframe to a table in Hive

			val startTime = DateTime.now()
					info(s"[HPIP-ETL] Writing Dataframes to Hive started at: $startTime")
					map.foreach(x => {

						val tablename = x._1

								val startTime = DateTime.now()
								info(s"[HPIP-ETL] Writing Dataframes to Hive table $tablename started at: $startTime")
								val hiveDB = config.getString("inbound-hive-db")
								info(s"[HPIP-ETL] Hive In-bound Database schema is $hiveDB")
								val warehouseHiveDB = config.getString("warehouse-hive-db")
								info(s"[HPIP-ETL] Warehouse Database Schema is $warehouseHiveDB")
								val stagingHiveDB = config.getString("stage-hive-db")
								info(s"[HPIP-ETL] Target tablename is $tablename")

								//Displaying the sample of data  
								val df = x._2
								info(s"[HPIP-ETL] Showing the contents of df")
								df.printSchema()
								//								df.show(false)

								//For writing the final table to the target schema
								if (tablename.equalsIgnoreCase("tin_xwalk_hz")) {

									//truncating the existing table 
									spark.sql("truncate table " + warehouseHiveDB + """.""" + tablename)

									//Reading the partition collumn from config file
									val partitionColumn = config.getString("tinsXWlk_partition_col").toLowerCase()
									info(s"[HPIP-ETL] Partition column name: $partitionColumn")

									//Creating the table in Hive
									//Setting the properties in hive for dynamic partitioning

									spark.conf.set("hive.exec.dynamic.partition", "true")
									spark.conf.set("hive.exec.dynamic.partition.mode", "nonstrict")

									//Writing into parttioned hive tavle in target schema

									df.write.mode("overwrite").partitionBy(partitionColumn).insertInto(warehouseHiveDB + """.""" + tablename)
									info(s"[HPIP-ETL] Table created as $warehouseHiveDB.$tablename")

								} else {

									if (config.getString("renderingSpendStagingFlag").equals("true")) {
										//For writing the staging tables to the staging schema
										info(s"[HPIP-ETL] writing the staging tables to the staging schema")
										//truncating the existing table 
										spark.sql("truncate table " + stagingHiveDB + """.""" + tablename)

										//Creating the table in Hive
										df.write.mode("overwrite").insertInto(stagingHiveDB + """.""" + tablename)
										info(s"[HPIP-ETL] Table created as $stagingHiveDB.$tablename")
									} else {
										info(s"[HPIP-ETL] Not materializing the staging tables")
									}

								}

						info(s"[HPIP-ETL] writing the data to target table $tablename is Completed at: " + DateTime.now())
						info(s"[HPIP-ETL] Time Taken for writing the data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

					})

					info(s"[HPIP-ETL] writing the data to target tables is Completed at: " + DateTime.now())
					info(s"[HPIP-ETL] Time Taken for writing the data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
					varianceCalculation(map)
	}

	def varianceCalculation(targetTableDFs: Map[String, DataFrame]): Unit = {
			//Calculating the variance percentage of row count with the previous count

			val startTime = DateTime.now()
					info(s"[HPIP-ETL] started varaince logic at : $startTime")

					//Reading the required variables
					val warehouseHiveDB = config.getString("warehouse-hive-db")
					val tablename = config.getString("tinsXWlk_target_table")
					info(s"[HPIP-ETL] Warehouse Database Schema is $warehouseHiveDB")
					info(s"[HPIP-ETL] Target tablename is $tablename")

					val varianceTableName = config.getString("variance_table").toLowerCase
					info(s"[HPIP-ETL] Variance tale name is : $varianceTableName")

					val subjectArea = config.getString("sas_program_name_renderingSpend")
					info(s"[HPIP-ETL] subject Area is : $subjectArea")

					val vdfcurrent = targetTableDFs.getOrElse(tablename, null)
					val thresholdTinsXWlk = config.getString("threshold_tinsXWlk").toDouble
					info(s"[HPIP-ETL] The threshold variance for $tablename is thresholdTinsXWlk")

					val thresholdtinsXWlkDefault = config.getString("threshold_tinsXWlk_default").toDouble
					info(s"[HPIP-ETL] The default threshold variance for $tablename is thresholdtinsXWlkDefault")
					val targetDB = config.getString("warehouse-hive-db")
					info(s"[HPIP-ETL] target Database schema is : $targetDB")

					//Reading the previous data query depending on the table name
					val previous_variance_data_query = config.getString("previous_variance_data_query_tinsXWlk").replaceAll(ConfigKey.warehouseDBPlaceHolder, targetDB).toLowerCase()
					info(s"[HPIP-ETL] previous_variance_data_query $previous_variance_data_query")

					//Checking if the threshold is crossed for the variance
					val check_threshold = (x: Double, y: Double) => { if (x > y || x < -y) "true" else "false" }
					val prvVarDF = spark.sql(previous_variance_data_query)

							import spark.implicits._

							//Getting the record count fot the current data load
							val varianceCount = vdfcurrent.count().toDouble
							info(s"[HPIP-ETL] he record count for the current load is $varianceCount")

							if (prvVarDF.head(1).isEmpty) {

								//Appending the values to the variance data frame for the first time insertion into variance table
								info(s"Appending the values to the variance data frame for the first time insertion into variance table")
								val toBeInserted = Seq((tablename, "NA", "NA", "NA", "row count", "count(*)", varianceCount, thresholdtinsXWlkDefault, thresholdTinsXWlk, "false", subjectArea))
								val varianceDF = toBeInserted.map(a => { (TINSXWLkVarianceSchema(a._1, a._2, a._3, a._4, a._5, a._6, a._7, a._8, a._9, a._10, a._11)) }).toDF
								//Inserting into the variance table
								info(s"[HPIP-ETL] Inserting the values to the variance data frame for the first time")
								varianceDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)
								//showing the contents of variance data frame
								info(s"[HPIP-ETL] Showing the contents of Data frame for variance")
								//								varianceDF.show()

							} else {
							  import spark.implicits._
								// Getting the previous record count from the dataset
							   val prvCount = prvVarDF.select($"operationValue").first().toString().toDouble
//								val prvCount = prvVarDF.map(f => f.get(6)).first().toString().toDouble
										//	val prvCount = (i:Int)=>prvVarDF.rdd.zipWithIndex.filter(_._2==i).map(_._1).first().toString().split(",")(6)
										info(s"[HPIP-ETL] The old record count is $prvCount")

										//Appending the values to the variance data frame
										val varianceDF = prvVarDF.map(f => TINSXWLkVarianceSchema(tablename, "NA", "NA", "NA", "row count", "count(*)", varianceCount, (((varianceCount - prvCount) / prvCount) * 100), thresholdTinsXWlk, check_threshold((((varianceCount - prvCount) / varianceCount) * 100), thresholdTinsXWlk), subjectArea)).toDF()
										//Inserting into the variance table
										varianceDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)
										info(s"[HPIP-ETL] variance() logic Completed at :" + DateTime.now())
										info(s"[HPIP-ETL] Time Taken varaince() logic :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

										//showing the contents of variance data frame
										info(s"[HPIP-ETL] Showing the contents of Data frame for variance")
										//										varianceDF.show()

							}

					info(s"[HPIP-ETL] variance() logic Completed at :" + DateTime.now())
					info(s"[HPIP-ETL] Time Taken varaince() logic :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
	}

}