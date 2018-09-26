package com.anthem.hca.spcp.provider.providerinfo

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{ collect_set, collect_list }
import org.apache.spark.sql.functions.concat_ws
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.trim
import org.apache.spark.sql.functions.when
import org.apache.spark.sql.types.DataTypes
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.config.ConfigKey
import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import com.anthem.hca.spcp.util.SpcpMailUtils

/**
 * This class is meant to perform Provider tables denormalization.
 * It is meant for populating the target Provider table with all the
 * necessary columns data from various source and mapping tables.
 *
 * The sorce consists of 5 WGSP tables, 1 PCP_GEOCODES target table
 * and 4 mapping tables.
 *
 * @author  Priya Chowdhury (AF56159)
 *
 */

class ProviderOperation(configPath: String, env: String, queryFileCategory: String)
    extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var start = ""
  var listBuffer = ListBuffer[Audit]()

  //storing the table name 
  val providerTargetTable = config.getString("providerTargetTable")

  //reading the sendNtfctnMail value from the config file
  val sendNtfctnMail = config.getBoolean("sendNtfctnMail")

  /*
   * This method is meant to load the data from splice tables to Spark Dataframes 
   * based on the queries in the properties file.
   * 
   * @param   None
   * @return  Map[String, DataFrame]
   */

  override def extractData(): Map[String, DataFrame] = {
    //Reading the data into Data frames
    val startTime = DateTime.now
    info(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")

    //Reading the queries from config file
    info("Reading the queries from config file")
    val wgspPcpQuery = config.getString("query_wgsp_pcp").toLowerCase().replace(ConfigKey.cdlWgspDBPlaceHolder, cdlWgspDB)
    val wgspPcpCntrctQuery = config.getString("query_wgsp_pcp_cntrct").replace(ConfigKey.cdlWgspDBPlaceHolder, cdlWgspDB)
    val wgspPcpAspQuery = config.getString("query_wgsp_pcp_asp").replace(ConfigKey.cdlWgspDBPlaceHolder, cdlWgspDB)
    val wgspPcpPovLangQuery = config.getString("query_wgsp_pcp_prov_lang").toLowerCase().replace(ConfigKey.cdlWgspDBPlaceHolder, cdlWgspDB)
    val wgspPcpAltidQuery = config.getString("query_wgsp_pcp_altid").toLowerCase().replace(ConfigKey.cdlWgspDBPlaceHolder, cdlWgspDB)

    val pcpGeocodesQuery = config.getString("query_pcp_geocodes").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspSpcltyCdMappingQuery = config.getString("query_wgsp_spclty_cd_mapping").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgsSpcpLangMappingQuery = config.getString("query_wgs_spcp_lang_mapping").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspCpCdMappingQuery = config.getString("query_wgsp_cp_cd_mapping").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val vbpTargetTableQuery = config.getString("query_vbp_target_table_load").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)

    //thrhld_table_load="select * from <spcp_splice_db>.SPCP_THRSHLD_TEST"
    val thrshldTblLoadQuery = config.getString("thrhld_table_load").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)

    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP table is: $wgspPcpQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_CNTRCT table is: $wgspPcpCntrctQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_ASP table is: $wgspPcpAspQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_PROV_LANG table is: $wgspPcpPovLangQuery")
    info(s"[SPCP-ETL] Query for reading data from PCP_GEOCODES table is: $pcpGeocodesQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_ALTID table is: $wgspPcpAltidQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_SPCLTY_CD_MAPPING table is: $wgspSpcltyCdMappingQuery")
    info(s"[SPCP-ETL] Query for reading data from WGS_SPCP_LANG_MAPPING table is: $wgsSpcpLangMappingQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_CP_CD_MAPPING table is: $wgspCpCdMappingQuery")
    info(s"[SPCP-ETL] Query for reading data from WGS_SPCP_VBP table is: $vbpTargetTableQuery")
    info(s"[SPCP-ETL] Query for reading data from WGS_SPCP_VBP table is: $thrshldTblLoadQuery")

    //Loading data into dataframes 
    val wgspPcpDF = splicemachineContext.internalDf(wgspPcpQuery)
    val wgspPcpCntrctDF = splicemachineContext.internalDf(wgspPcpCntrctQuery)
    val wgspPcpAspDF = splicemachineContext.internalDf(wgspPcpAspQuery)
    val wgspPcpProvLangDF = splicemachineContext.internalDf(wgspPcpPovLangQuery)
    val pcpGeocodesDF = splicemachineContext.internalDf(pcpGeocodesQuery)
    val wgspPcpAltidDF = splicemachineContext.internalDf(wgspPcpAltidQuery)
    val wgspPcpSpcltyCdMappingDF = splicemachineContext.internalDf(wgspSpcltyCdMappingQuery)
    val wgsSpcpLangMappingDF = splicemachineContext.internalDf(wgsSpcpLangMappingQuery)
    val wgspCpCdMappingDF = splicemachineContext.internalDf(wgspCpCdMappingQuery)
    val vbpTableDF = splicemachineContext.internalDf(vbpTargetTableQuery)
    val spcpThrhldDF = splicemachineContext.internalDf(thrshldTblLoadQuery)

    //Creating Map of Inbouond Data frames
    val mapDF = Map("wgspPcp" -> wgspPcpDF, "wgspPcpCntrct" -> wgspPcpCntrctDF,
      "wgspPcpAsp" -> wgspPcpAspDF, "wgspPcpProvLang" -> wgspPcpProvLangDF, "pcpGeocodes" -> pcpGeocodesDF,
      "wgspPcpAltid" -> wgspPcpAltidDF, "wgspPcpSpcltyCdMapping" -> wgspPcpSpcltyCdMappingDF,
      "wgsSpcpLangMapping" -> wgsSpcpLangMappingDF, "wgspCpCdMapping" -> wgspCpCdMappingDF,
      "vbpTargetTable" -> vbpTableDF, "spcpThrshldTbl" -> spcpThrhldDF)

    info(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }

  /*
   * This method takes care of all the transformation and derivation logic
   * 
   * @param   Map[String, DataFrame]
   * @return  Map[String, DataFrame]
   * 
   */
  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Processing Data Started: $startTime")

    //Retrieving the dataframes based on respective keys
    val wgspPcpDF = inDFs.getOrElse("wgspPcp", null)
    val wgspPcpCntrctDF = inDFs.getOrElse("wgspPcpCntrct", null)
    val wgspPcpAspDF = inDFs.getOrElse("wgspPcpAsp", null)
    val wgspPcpProvLangDF = inDFs.getOrElse("wgspPcpProvLang", null)
    val pcpGeocodesDF = inDFs.getOrElse("pcpGeocodes", null)
    val wgspPcpAltidDF = inDFs.getOrElse("wgspPcpAltid", null)
    val wgspPcpSpcltyCdMappingDF = inDFs.getOrElse("wgspPcpSpcltyCdMapping", null)
    val wgsSpcpLangMappingDF = inDFs.getOrElse("wgsSpcpLangMapping", null)
    val wgsCpCdMappingDF = inDFs.getOrElse("wgspCpCdMapping", null)
    val vbpTargetTableDF = inDFs.getOrElse("vbpTargetTable", null)
    val spcpThrshldDF = inDFs.getOrElse("spcpThrshldTbl", null)

    val minRowCnt: Integer = spcpThrshldDF.filter($"table_nm" === providerTargetTable).select($"min_cnt").head().getInt(0)
    val maxRowCnt: Integer = spcpThrshldDF.filter($"table_nm" === providerTargetTable).select($"max_cnt").head().getInt(0)

    //Step 1: Inner Join between the wgspPcpDF and wgspPcpAltidDF
    val pcp_pcpaltid_joinDF = wgspPcpDF.join(wgspPcpAltidDF,
      Seq("prov_pcp_id", "hmo_type_cd", "src_sor_cd", "pcp_efctv_dt"))
      .select($"prov_pcp_id", $"pcp_frst_nm", $"pcp_mid_nm",
        $"pcp_last_nm", $"prov_prty_cd", $"tax_id", $"npi", $"hmo_type_cd",
        $"src_sor_cd", $"pcp_efctv_dt")
    info("Step 1: Inner Join between the wgspPcpDF and wgspPcpAltidDF done!")

    //Step 2: Inner Join between wgspPcpAspDF and wgspPcpSpcltyCdMappingDF
    val pcpasp_spclity_joinDF = wgspPcpAspDF.join(wgspPcpSpcltyCdMappingDF, Seq("spclty_cd"))
      .select($"spclty_cd", wgspPcpAspDF.col("wgs_spclty_cd"),
        $"prov_pcp_id", $"prmry_spclty_ind",
        $"adrs_typ_cd", $"adrs_id", $"adrs_efctv_dt",
        $"spclty_desc", $"spclty_mnemonics",
        $"hmo_type_cd", $"src_sor_cd", $"pcp_efctv_dt")
    info("Step 2: Inner Join between wgspPcpAspDF and wgspPcpSpcltyCdMappingDF done!")

    //Step 3: Inner Join between pcp_pcpaltid_joinDF and pcpasp_spclity_joinDF
    val pcpAltid_pcpAspSpclty_joinDF = pcp_pcpaltid_joinDF.join(pcpasp_spclity_joinDF,
      Seq("prov_pcp_id", "hmo_type_cd", "src_sor_cd", "pcp_efctv_dt"))
      .select(pcp_pcpaltid_joinDF.col("*"),
        $"spclty_cd", $"wgs_spclty_cd", $"prmry_spclty_ind",
        $"adrs_typ_cd", $"adrs_id", $"adrs_efctv_dt",
        $"spclty_desc", $"spclty_mnemonics")
    info("Step 3: Inner Join between pcp_pcpaltid_joinDF and pcpasp_spclity_joinDF done!")

    //Step 4: Left Outer join between wgspPcpCntrctDF and wgspCpCdMappingDf
    val cntrct_cpCdMapping_joinDF = wgspPcpCntrctDF.join(wgsCpCdMappingDF,
      Seq("cp_type_cd"), "LEFT_OUTER")
      .select(wgspPcpCntrctDF.col("*"),
        when($"acc_new_patient_flag" === null || $"acc_new_patient_flag" === "" ||
          wgsCpCdMappingDF.col("acc_new_patient_flag").isNull || $"acc_new_patient_flag" === "null", lit("Y"))
          .otherwise($"acc_new_patient_flag").as("acc_new_patient_flag"))
    info("Step 4: Left Outer join between wgspPcpCntrctDF and wgspCpCdMappingDf done!")

    //Step 5: Inner Join between pcpAltid_pcpAspSpclty_joinDF and cntrct_cpCdMapping_joinDF
    val pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF = pcpAltid_pcpAspSpclty_joinDF.join(cntrct_cpCdMapping_joinDF,
      Seq("prov_pcp_id", "hmo_type_cd", "src_sor_cd", "pcp_efctv_dt"))
      .select(pcpAltid_pcpAspSpclty_joinDF.col("*"), $"grpg_rltd_padrs_efctv_dt",
        $"grpg_rltd_padrs_trmntn_dt", $"max_mbr_cnt", $"curnt_mbr_cnt",
        $"rgnl_ntwk_id", $"cp_type_cd", $"acc_new_patient_flag", $"legcy_ntwk_id")
    info("Step 5: Inner Join between pcpAltid_pcpAspSpclty_joinDF and cntrct_cpCdMapping_joinDF done!")

    //Step 6: Inner Join between pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF and pcpGeocodesDF
    val pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF = pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF.
      join(pcpGeocodesDF, Seq("prov_pcp_id", "hmo_type_cd", "src_sor_cd", "pcp_efctv_dt", "adrs_typ_cd", "adrs_id", "adrs_efctv_dt"))
      .select(pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF.col("*"),
        $"latd_cordnt_nbr".cast(DataTypes.createDecimalType(18, 8)),
        $"lngtd_cordnt_nbr".cast(DataTypes.createDecimalType(18, 8)),
        $"adrs_line_1_txt", $"adrs_line_2_txt", $"adrs_city_nm", $"adrs_st_cd",
        $"adrs_zip_cd", $"adrs_zip_plus_4_cd", $"pa_cmnctn_typ_value", $"adrs_cnty_cd")
    info("Step 6: Inner Join between pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF and pcpGeocodesDF done!")

    //Step 7: Left Join between pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF
    //   and hcaRankngDF 
    val pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF = pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF
      .withColumn("pcp_rankg_id", lit(0))
    info("Step 7: Appending PCP_RANKG_ID column to pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF done!")

    //Step 8: Inner Join between wgspPcpProvLangDF and wgsSpcpLangMappingDF
    val provLang_LangMapping_joinDF = wgspPcpProvLangDF.join(wgsSpcpLangMappingDF,
      wgspPcpProvLangDF.col("ip_lang_cd") === wgsSpcpLangMappingDF.col("ent_lang_cd"), "LEFT")
      .select($"iso_3_cd", $"prov_pcp_id", $"pcp_language",
        $"src_sor_cd", $"hmo_type_cd", $"pcp_efctv_dt")
    info("Step 8: Inner Join between wgspPcpProvLangDF and wgsSpcpLangMappingDF done!")

    //Step 9: Performing group by on provLang_LangMapping_joinDF on prov_pcp_id
    //        so that we have all the wgs_lang (comma delimited) for one pcp in same row.
    val provLang_LangMapping_grouped_joinDF = provLang_LangMapping_joinDF.groupBy($"prov_pcp_id").
      agg(concat_ws(",", collect_set(trim($"pcp_language"))).as("pcp_lang"),
        concat_ws(",", collect_set(trim($"iso_3_cd"))).as("iso_3_cd"))
    info("Step 9: Performing group by on provLang_LangMapping_joinDF on prov_pcp_id so that we have all "
      + "the wgs_lang (comma delimited) for one pcp in same row done!")

    //Step 10: Inner Join between pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF 
    //         and provLang_LangMapping_joinDF
    val wgspJoinDF_tmp = pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF.
      join(provLang_LangMapping_joinDF, Seq("prov_pcp_id", "hmo_type_cd", "src_sor_cd", "pcp_efctv_dt"))
      .select(pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF.col("*"))
    info("Step 10: Inner Join between pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF and provLang_LangMapping_joinDF done!")

    //Step 11: Inner Join between wgspJoinDF_tmp and provLang_LangMapping_grouped_joinDF
    val wgspJoinDF = wgspJoinDF_tmp.join(provLang_LangMapping_grouped_joinDF, Seq("prov_pcp_id"))
      .select(wgspJoinDF_tmp.col("*"),
        provLang_LangMapping_grouped_joinDF.col("pcp_lang"),
        provLang_LangMapping_grouped_joinDF.col("iso_3_cd"))
    info("Step 11: Inner Join between wgspJoinDF_tmp and provLang_LangMapping_grouped_joinDF done!")

    //Step 12: Left Join between wgspJoinDF and vbpTargetTableDF
    val pims_vbp_joinDF = wgspJoinDF.join(vbpTargetTableDF,
      (wgspJoinDF("npi")) === (vbpTargetTableDF("ip_npi")) &&
        (wgspJoinDF("tax_id")) === (vbpTargetTableDF("prov_org_tax_id")), "left")
      .select(wgspJoinDF.col("*"), $"ip_npi", $"prov_org_tax_id")
    info("Step 12: Left Join between wgspJoinDF and vbpTargetTableDF done!")

    //Step 13: Set the VBP flag value as 'N' where there is a match, else set the flag as 'Y'
    val provVBPDF = pims_vbp_joinDF.withColumn("vbp_flag",
      when($"ip_npi".isNull && $"prov_org_tax_id".isNull, lit("N")).otherwise(lit("Y")))
      .withColumn("tier_level", lit(1))
      .drop("ip_npi").drop("prov_org_tax_id")
      .drop("src_sor_cd").drop("pcp_efctv_dt")
      .drop("mdo_ranking").drop("adrs_typ_cd")
      .drop("adrs_id").drop("adrs_efctv_dt").drop("legcy_ntwk_id").distinct()
    info("Step 13: Set the VBP flag value as 'N' where there is a match, else set the flag as 'Y' done!")

    //Step 14: Doing a group by and aggregate to get all the specialities for
    // a particular PCP in one row.
    val provVBP_GroupedDF = provVBPDF.groupBy($"prov_pcp_id", $"rgnl_ntwk_id", $"npi").
      agg(concat_ws(",", collect_set(trim($"spclty_cd"))).as("spclty_cd"),
        concat_ws(",", collect_set(trim($"wgs_spclty_cd"))).as("wgs_spclty_cd"),
        concat_ws(",", collect_set(trim($"spclty_desc"))).as("spclty_desc"),
        concat_ws(",", collect_set(trim($"spclty_mnemonics"))).as("spclty_mnemonics"),
        concat_ws(",", collect_list(trim($"prmry_spclty_ind"))).as("prmry_spclty_ind"))
    info("Step 14: Doing a group by and aggregate to get all the specialities for a particular PCP in one row done!")

    //Step 15: Left join between provVBPDF and provVBP_GroupedDF to get all the required columns
    val finalProvDF = provVBP_GroupedDF.join(provVBPDF,
      Seq("prov_pcp_id", "rgnl_ntwk_id", "npi"), "LEFT")
      .select(provVBP_GroupedDF("prov_pcp_id"), $"grpg_rltd_padrs_efctv_dt", $"grpg_rltd_padrs_trmntn_dt", $"adrs_zip_cd", $"adrs_zip_plus_4_cd",
        $"latd_cordnt_nbr", $"lngtd_cordnt_nbr", $"adrs_cnty_cd", $"pcp_rankg_id", provVBP_GroupedDF("rgnl_ntwk_id"), provVBP_GroupedDF("spclty_cd"),
        provVBP_GroupedDF("wgs_spclty_cd"), provVBP_GroupedDF("spclty_desc"), provVBP_GroupedDF("spclty_mnemonics"), provVBP_GroupedDF("prmry_spclty_ind"),
        $"max_mbr_cnt", $"curnt_mbr_cnt", $"cp_type_cd", $"acc_new_patient_flag", $"iso_3_cd", $"pcp_lang",
        $"vbp_flag",
        when($"hmo_type_cd" equalTo lit("7568"), lit("PCP")).
          otherwise(when($"hmo_type_cd" equalTo lit("7569"), lit("PMG")).otherwise($"hmo_type_cd")).as("hmo_type_cd"),
        $"pcp_frst_nm", $"pcp_mid_nm", $"pcp_last_nm", $"adrs_line_1_txt",
        $"adrs_line_2_txt", $"adrs_city_nm", $"adrs_st_cd", $"pa_cmnctn_typ_value", $"prov_prty_cd",
        provVBP_GroupedDF("npi"), $"tax_id", $"tier_level")
      .withColumn("last_updtd_dtm", current_timestamp()).distinct()
    info("Step 15: Left join between provVBPDF and provVBP_GroupedDF to get all the required columns done! Final Dataframe generated!")

    var finalPcpDFMap: Map[String, DataFrame] = null
    
    if (sendNtfctnMail) {
      val finalDfCnt = finalProvDF.count()
      info("Provider Dataframe count is " + finalDfCnt)
      if ((finalDfCnt < minRowCnt) || (finalDfCnt > maxRowCnt)) {
        finalPcpDFMap = Map("targetTableProvider" -> null)
        info("Provider data not within threshold limits")
        println("Provider data not within threshold limits")
        val mailDF = spark.sparkContext.parallelize(List((providerTargetTable, minRowCnt, maxRowCnt, finalDfCnt)))
          .toDF("Table_Nm", "Min_Cnt", "Max_Cnt", "Current_Record_Cnt")
        mailDF.show()
        SpcpMailUtils.sendMail(config, mailDF)
      } else {
        finalPcpDFMap = Map("targetTableProvider" -> finalProvDF)
      }
    }
    
    else {
        finalPcpDFMap = Map("targetTableProvider" -> finalProvDF)
      }

    finalPcpDFMap

  }

  /*
   * This method truncates the target table and reloads it with the new data.
   * 
   * @param   Map[String, DataFrame]
   * @return  None
   */
  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Loading Data Started: $startTime")

    val targetTablePcpDF = outDFs.getOrElse("targetTableProvider", null)

    if (targetTablePcpDF == null) {
      info("PROVIDER RECORDS CANNOT BE INSERTED")
    } else {
      val provDf = SPCPDataFrameUtils.columnsInUpper(targetTablePcpDF)
      splicemachineContext.execute(s"delete from $spcpSpliceDB.$providerTargetTable")
      info(s"$spcpSpliceDB.$providerTargetTable has been deleted")

      splicemachineContext.insert(provDf, spcpSpliceDB + """.""" + providerTargetTable)
      info("PROVIDER RECORDS INSERTED")
    }
  }

  /*
 * Audit log entry at the start of Provider ETL process.
 */
  @Override
  def beforeExtractData() {
    program = sc.appName
    user_id = sc.sparkUser
    app_id = sc.applicationId

    start_time = DateTime.now()

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), "0min", "Started")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, lit(current_timestamp()))

    val spcpAuditDFWithUppercaseCol = SPCPDataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show

    //using upsert to avoid insert failure while job retry , also trying to limit job retries to 1
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDB + "." + spcpAuditTable)

  }

  /*
 * Audit log entry at the end of Provider ETL process.
 */
  @Override
  def afterLoadData() {

    var listBuffer = ListBuffer[Audit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), duration, "completed")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, current_timestamp())
    val spcpAuditDFWithUppercaseCol = SPCPDataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDB + "." + spcpAuditTable)

  }

}