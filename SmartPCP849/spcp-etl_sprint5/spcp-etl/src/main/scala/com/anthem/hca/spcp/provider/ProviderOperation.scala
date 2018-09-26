package com.anthem.hca.spcp.provider

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPConstants
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.spark.sql.functions.current_date

import scalaj.http.Http
import scalaj.http.HttpResponse
import javax.jdo.annotations.Join
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import org.apache.spark.sql.types.DataTypes
import com.anthem.hca.spcp.config.ConfigKey

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

  override def extractData(): Map[String, DataFrame] = {
    //Reading the data into Data frames
    val startTime = DateTime.now
    info(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")

    //Reading the queries from config file
    info("Reading the queries from config file")
    val wgspPcpQuery = config.getString("query_wgsp_spcp").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspPcpCntrctQuery = config.getString("query_wgsp_pcp_cntrct").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspPcpAspQuery = config.getString("query_wgsp_pcp_asp").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspPcpPovLangQuery = config.getString("query_wgsp_pcp_prov_lang").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val pcpGeocodesQuery = config.getString("query_pcp_geocodes").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspPcpAltidQuery = config.getString("query_wgsp_pcp_altid").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspSpcltyCdMappingQuery = config.getString("query_wgsp_spclty_cd_mapping").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgsSpcpLangMappingQuery = config.getString("query_wgs_spcp_lang_mapping").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspCpCdMappingQuery = config.getString("query_wgsp_cp_cd_mapping").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val vbpTargetTableQuery = config.getString("vbp_target_table_load_query").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val hcaRankngTableQuery = config.getString("query_hca_pcp_prov_rnkng_mapping").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val tierLevelTableQuery = config.getString("query_tierLevel_table_query").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)

    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP table is: $wgspPcpQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_CNTRCT table is: $wgspPcpCntrctQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_ASP table is: $wgspPcpAspQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_PROV_LANG table is: $wgspPcpPovLangQuery")
    info(s"[SPCP-ETL] Query for reading data from PCP_GEOCODES table is: $pcpGeocodesQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_ALTID table is: $wgspPcpAltidQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_SPCLTY_CD_MAPPING table is: $wgspSpcltyCdMappingQuery")
    info(s"[SPCP-ETL] Query for reading data from WGS_SPCP_LANG_MAPPING table is: $wgsSpcpLangMappingQuery")
    info(s"[SPCP-ETL] Query for reading data from WGS_WGSP_CP_CD_MAPPING table is: $wgspCpCdMappingQuery")
    info(s"[SPCP-ETL] Query for reading data from WGS_SPCP_VBP table is: $vbpTargetTableQuery")
    info(s"[SPCP-ETL] Query for reading data from HCA_RANKNG table is: $hcaRankngTableQuery")
    info(s"[SPCP-ETL] Query for reading data from TIER_LEVEL table is: $tierLevelTableQuery")

    val wgspPcpDF = splicemachineContext.df(wgspPcpQuery)
    val wgspPcpCntrctDF = splicemachineContext.df(wgspPcpCntrctQuery)
    val wgspPcpAspDF = splicemachineContext.df(wgspPcpAspQuery)
    val wgspPcpProvLangDF = splicemachineContext.df(wgspPcpPovLangQuery)
    val pcpGeocodesDF = splicemachineContext.df(pcpGeocodesQuery)
    val wgspPcpAltidDF = splicemachineContext.df(wgspPcpAltidQuery)
    val wgspPcpSpcltyCdMappingDF = splicemachineContext.df(wgspSpcltyCdMappingQuery)
    val wgsSpcpLangMappingDF = splicemachineContext.df(wgsSpcpLangMappingQuery)
    val wgspCpCdMappingDF = splicemachineContext.df(wgspCpCdMappingQuery)
    val vbpTableDF = splicemachineContext.df(vbpTargetTableQuery)
    val hcaRankngDF = splicemachineContext.df(hcaRankngTableQuery)
    val tierLevelTableDF = splicemachineContext.df(tierLevelTableQuery)

    //Creating Map of Inbouond Data frames
    val mapDF = Map("wgspPcp" -> wgspPcpDF, "wgspPcpCntrct" -> wgspPcpCntrctDF,
      "wgspPcpAsp" -> wgspPcpAspDF, "wgspPcpProvLang" -> wgspPcpProvLangDF, "pcpGeocodes" -> pcpGeocodesDF,
      "wgspPcpAltid" -> wgspPcpAltidDF, "wgspPcpSpcltyCdMapping" -> wgspPcpSpcltyCdMappingDF,
      "wgsSpcpLangMapping" -> wgsSpcpLangMappingDF, "wgspCpCdMapping" -> wgspCpCdMappingDF,
      "vbpTargetTable" -> vbpTableDF, "hcaRankngTable" -> hcaRankngDF, "tierLevelTable" -> tierLevelTableDF)

    info(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())
    println("[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }

  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Processing Data Started: $startTime")

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
    val hcaRankngDF = inDFs.getOrElse("hcaRankngTable", null)
    val tierLevelDF = inDFs.getOrElse("tierLevelTable", null)

    //Step 1: Inner Join between the wgspPcpDF and wgspPcpAltidDF
    val pcp_pcpaltid_joinDF = wgspPcpDF.join(wgspPcpAltidDF,
      Seq("PROV_PCP_ID", "HMO_TYPE_CD", "SRC_SOR_CD", "PCP_EFCTV_DT"))
      .select($"PROV_PCP_ID", $"PCP_FRST_NM", $"PCP_MID_NM",
        $"PCP_LAST_NM", $"PROV_PRTY_CD", $"TAX_ID", $"NPI", $"HMO_TYPE_CD",
        $"SRC_SOR_CD", $"PCP_EFCTV_DT")
    println("Step 1 done!")
    //    println("Count is: " + pcp_pcpaltid_joinDF.count())
    //    pcp_pcpaltid_joinDF.printSchema()
    //    pcp_pcpaltid_joinDF.cache()

    //Step 2: Inner Join between wgspPcpAspDF and wgspPcpSpcltyCdMappingDF
    val pcpasp_spclity_joinDF = wgspPcpAspDF.join(wgspPcpSpcltyCdMappingDF,Seq("SPCLTY_CD"))
      .select($"SPCLTY_CD", wgspPcpAspDF.col("WGS_SPCLTY_CD"),
        $"PROV_PCP_ID", $"PRMRY_SPCLTY_IND",
        $"ADRS_TYP_CD", $"ADRS_ID", $"ADRS_EFCTV_DT",
        $"SPCLTY_DESC",
        $"SPCLTY_MNEMONICS",
        $"HMO_TYPE_CD", $"SRC_SOR_CD", $"PCP_EFCTV_DT")
    println("Step 2 done!")
    //    println("Count is: " + pcpasp_spclity_joinDF.count())
    //    pcpasp_spclity_joinDF.printSchema()
    //    pcpasp_spclity_joinDF.cache()

    //Step 3: Inner Join between pcp_pcpaltid_joinDF and pcpasp_spclity_joinDF
    val pcpAltid_pcpAspSpclty_joinDF = pcp_pcpaltid_joinDF.join(pcpasp_spclity_joinDF,
      Seq("PROV_PCP_ID", "HMO_TYPE_CD", "SRC_SOR_CD", "PCP_EFCTV_DT"))
      .select(pcp_pcpaltid_joinDF.col("*"),
        $"SPCLTY_CD", $"WGS_SPCLTY_CD", $"PRMRY_SPCLTY_IND",
        $"ADRS_TYP_CD", $"ADRS_ID", $"ADRS_EFCTV_DT",
        $"SPCLTY_DESC", $"SPCLTY_MNEMONICS")
    println("Step 3 done!")
    //    println("Count is: " + pcpAltid_pcpAspSpclty_joinDF.count())
    //    pcpAltid_pcpAspSpclty_joinDF.printSchema()
    //    pcpAltid_pcpAspSpclty_joinDF.cache()

    //Step 4: Inner join between wgspPcpCntrctDF and wgspCpCdMappingDf
    val cntrct_cpCdMapping_joinDF = wgspPcpCntrctDF.join(wgsCpCdMappingDF,
      wgspPcpCntrctDF.col("CP_TYPE_CD") === wgsCpCdMappingDF.col("CP_TYPE_CD"), "LEFT_OUTER")
      .select(wgspPcpCntrctDF.col("*"),
        when($"ACC_NEW_PATIENT_FLAG" === null, lit("Y")).otherwise($"ACC_NEW_PATIENT_FLAG").as("ACC_NEW_PATIENT_FLAG"))
    println("Step 4 done!")
    //    println("Count is: " + cntrct_cpCdMapping_joinDF.count())
    //    cntrct_cpCdMapping_joinDF.printSchema()
    //    cntrct_cpCdMapping_joinDF.cache()

    //Step 5: Inner Join between pcpAltid_pcpAspSpclty_joinDF and cntrct_cpCdMapping_joinDF
    val pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF = pcpAltid_pcpAspSpclty_joinDF.join(cntrct_cpCdMapping_joinDF,
      Seq("PROV_PCP_ID", "HMO_TYPE_CD", "SRC_SOR_CD", "PCP_EFCTV_DT"))
      .select(pcpAltid_pcpAspSpclty_joinDF.col("*"), $"GRPG_RLTD_PADRS_EFCTV_DT",
        $"GRPG_RLTD_PADRS_TRMNTN_DT", $"MAX_MBR_CNT", $"CURNT_MBR_CNT",
        $"RGNL_NTWK_ID", $"CP_TYPE_CD", $"ACC_NEW_PATIENT_FLAG", $"LEGCY_NTWK_ID")
    println("Step 5 done!")
    //    println("Count is: " + pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF.count())
    //    pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF.printSchema()
    //    pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF.cache()

    //Step 6: Inner Join between pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF and wgspPcpSpcltyCdMappingDF
    val pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF = pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF.
      join(pcpGeocodesDF, Seq("PROV_PCP_ID", "HMO_TYPE_CD", "SRC_SOR_CD", "PCP_EFCTV_DT", "ADRS_TYP_CD", "ADRS_ID", "ADRS_EFCTV_DT"))
      .select(pcpAltidPcpAspSpclty_cntrctCpCdMapping_joinDF.col("*"),
        $"LATD_CORDNT_NBR".cast(DataTypes.createDecimalType(18, 8)),
        $"LNGTD_CORDNT_NBR".cast(DataTypes.createDecimalType(18, 8)),
        $"ADRS_LINE_1_TXT", $"ADRS_LINE_2_TXT", $"ADRS_CITY_NM", $"ADRS_ST_CD",
        $"ADRS_ZIP_CD", $"ADRS_ZIP_PLUS_4_CD", $"PA_CMNCTN_TYP_VALUE", $"ADRS_CNTY_CD")
    println("Step 6 done!")
    //  println("Count is: " + pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF.count())
    //    pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF.printSchema()
    //    pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF.cache()

    //Step 7: Left Join between pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF
    //   and hcaRankngDF 
    val pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF = pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF
      .join(hcaRankngDF,
        (pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF.col("TAX_ID") === hcaRankngDF.col("TAX_ID") &&
          pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF.col("ADRS_ST_CD") === hcaRankngDF.col("ADRS_ST_CD"))
          .or(pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF.col("TAX_ID") === hcaRankngDF.col("TAX_ID")),
        "LEFT_OUTER")
      .select(pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_joinDF.col("*"),
        when($"MDO_RANKING".isNull, lit(0)).otherwise($"MDO_RANKING").as("PCP_RANKG_ID"))
    println("Step 7 done!")
    //    println("Count is: " + pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF.count())
    //    pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF.printSchema()
    //    pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF.cache()

    //Step 8: Inner Join between wgspPcpProvLangDF and wgsSpcpLangMappingDF
    val provLang_LangMapping_joinDF = wgspPcpProvLangDF.join(wgsSpcpLangMappingDF,
      wgspPcpProvLangDF.col("WGS_LANG_CD") === wgsSpcpLangMappingDF.col("WGS_LANG_CODE"), "INNER")
      .select($"WGS_LANG_CD", $"PROV_PCP_ID", $"WGS_LANG",
        $"SRC_SOR_CD", $"HMO_TYPE_CD", $"PCP_EFCTV_DT")
    println("Step 8 done!")
    //    println("Count is: " + provLang_LangMapping_joinDF.count())
    //    provLang_LangMapping_joinDF.printSchema()
    //    provLang_LangMapping_joinDF.cache()

    //Step 9: Performing group by on provLang_LangMapping_joinDF on prov_pcp_id
    //        so that we have all the wgs_lang (comma delimited) for one pcp in same row.
    val provLang_LangMapping_grouped_joinDF = provLang_LangMapping_joinDF.groupBy($"PROV_PCP_ID").
      agg(concat_ws(",", collect_set(trim($"WGS_LANG"))).as("WGS_LANG"),
        concat_ws(",", collect_set(trim($"WGS_LANG_CD"))).as("WGS_LANG_CD"))
    println("Step 9 done!")
    //    println("Count is: " + provLang_LangMapping_grouped_joinDF.count())
    //    provLang_LangMapping_grouped_joinDF.printSchema()
    //    provLang_LangMapping_grouped_joinDF.cache()

    //Step 10: Inner Join between pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF 
    //and provLang_LangMapping_joinDF
    val wgspJoinDF_tmp = pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF.
      join(provLang_LangMapping_joinDF, Seq("PROV_PCP_ID", "HMO_TYPE_CD", "SRC_SOR_CD", "PCP_EFCTV_DT"))
      .select(pcpAltidPcpAspSpclty_cntrctCpCdMapping_pcpGeocodes_hcaRanking_joinDF.col("*"))
    println("Step 10 done!")
    //    println("Count is: " + wgspJoinDF_tmp.count())
    //    wgspJoinDF_tmp.printSchema()
    //    wgspJoinDF_tmp.cache()

    //Step 11: Inner Join between wgspJoinDF_tmp and provLang_LangMapping_grouped_joinDF
    val wgspJoinDF = wgspJoinDF_tmp.join(provLang_LangMapping_grouped_joinDF, Seq("PROV_PCP_ID"))
      .select(wgspJoinDF_tmp.col("*"),
        provLang_LangMapping_grouped_joinDF.col("WGS_LANG").as("PCP_LANG"),
        provLang_LangMapping_grouped_joinDF.col("WGS_LANG_CD"))
    println("Step 11 done!")
    //    println("Count is: " + wgspJoinDF.count())
    //    wgspJoinDF.printSchema()
    //    wgspJoinDF.cache()

    //Step 12: Left Join between wgspJoinDF and vbpTargetTableDF
    val pims_vbp_joinDF = wgspJoinDF.join(vbpTargetTableDF,
      (wgspJoinDF("NPI")) === (vbpTargetTableDF("IP_NPI")) &&
        (wgspJoinDF("TAX_ID")) === (vbpTargetTableDF("PROV_ORG_TAX_ID")), "left")
      .select(wgspJoinDF.col("*"), $"IP_NPI", $"PROV_ORG_TAX_ID")
    println("Step 12 done!")
    //    println("Count is: " + pims_vbp_joinDF.count())
    //    pims_vbp_joinDF.printSchema()
    //    pims_vbp_joinDF.cache()

    //Step 13: Set the VBP flag value as 'N' where there is a match, else set the flag as 'Y'
    val provVBPDF = pims_vbp_joinDF.withColumn("VBP_FLAG",
      when($"IP_NPI".isNull && $"PROV_ORG_TAX_ID".isNull, lit("N")).otherwise(lit("Y")))
      .withColumn("LAST_UPDTD_DTM", current_timestamp())
      .drop("IP_NPI").drop("PROV_ORG_TAX_ID")
      .drop("SRC_SOR_CD").drop("PCP_EFCTV_DT")
      .drop("MDO_RANKING").drop("ADRS_TYP_CD")
      .drop("ADRS_ID").drop("ADRS_EFCTV_DT")
    println("Step 13 done!")
    //    println("Count is: " + provVBPDF.count())
    //    provVBPDF.printSchema()
    //    provVBPDF.cache()

    //Step 14: Join with tierLevelDF to get the tier level column
    val finalDF = provVBPDF.join(tierLevelDF, provVBPDF.col("LEGCY_NTWK_ID") === tierLevelDF.col("LGCY_NTWK_ID"),
      "LEFT_OUTER").select(provVBPDF.col("*"), when($"TIER_LEVEL".isNull, lit(1)).otherwise($"TIER_LEVEL")
        .as("TIER_LEVEL")).drop("LEGCY_NTWK_ID")

    println("Step 14 done! Final dataframe generated!")
    //    println("Count is: " + finalDF.count())
    //    finalDF.printSchema()
    //    finalDF.cache()
       finalDF.show()

    val finalPcpDFMap = Map("targetTableProvider" -> finalDF)
    finalPcpDFMap

  }

  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val startTime = DateTime.now
    info(s"[SPCP-ETL] Loading Data Started: $startTime")

    val providerTargetTable = config.getString("providerTargetTable")
    val targetTablePcpDF = outDFs.getOrElse("targetTableProvider", null)

    // targetTablePcpDF.show
    splicemachineContext.insert(targetTablePcpDF, spcpSpliceDb + """.""" + providerTargetTable)
    println("PROVIDER RECORDS INSERTED")
  }

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
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDb + "." + spcpAuditTable)

  }

  @Override
  def afterLoadData() {

    var listBuffer = ListBuffer[Audit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), duration, "completed")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, current_timestamp())
    val spcpAuditDFWithUppercaseCol = SPCPDataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDb + "." + spcpAuditTable)

  }

}