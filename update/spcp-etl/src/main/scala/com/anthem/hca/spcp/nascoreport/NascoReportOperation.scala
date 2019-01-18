package com.anthem.hca.spcp.nascoreport

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.when
import org.apache.spark.sql.types.DateType
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.config.ConfigKey
import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import org.apache.spark.storage.StorageLevel
import com.anthem.hca.spcp.util.SpcpMailUtils

/**
 * This class is meant to perform the NASCO Report query.
 *
 * The source consists of  tables,
 * and  lookup tables.
 *
 *
 */

class NascoReportOperation(configPath: String, env: String, queryFileCategory: String)
  extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._
  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var start = ""
  var listBuffer = ListBuffer[Audit]()

  //reading the reporting target table name from config file
  val reportingTargetTable = config.getString("nascoReportTable")

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

    //Driver table
    val mbrPcpQuery = config.getString("query_mbr_pcp_nasco").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    //member lookup table
    val mbrQuery = config.getString("query_mbr_reprtng").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val mpeQuery = config.getString("query_mpe").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val hcaRankingQuery = config.getString("query_hca_prov_ranking").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val providerQuery = config.getString("query_provider").replace(ConfigKey.cdlWgspAllphiDBPlaceHolder, cdlWgspAllphiDB)
    val rcodeMappingQuery = config.getString("rcode_mapping_query").replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)


    val prodOfrgQuery = config.getString("query_prod_ofrg").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val prchsrOrgQuery = config.getString("query_prchsr_org").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val prchsrOrgDmgrphcQuery = config.getString("query_prchsr_org_dmgrphc").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val bnftPkgQuery = config.getString("query_bnft_pkg").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val prodQuery = config.getString("query_prod").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val hmoSpclstPcpIdQuery = config.getString("query_hmo_spclst_pcp_id").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val ntwkProvQuery = config.getString("query_ntwk_prov").replace(ConfigKey.cdlEdwdAllphiDBPlaceHolder, cdlEdwdAllphiDB)
    val wgspPcpAdrsQuery = config.getString("query_rpt_wgsp_pcp_adrs").replace(ConfigKey.cdlWgspAllphiDBPlaceHolder, cdlWgspAllphiDB)


    info(s"[SPCP-ETL] Query for reading data from MBR_PCP table is: $mbrPcpQuery")
    info(s"[SPCP-ETL] Query for reading data from MPE table is: $mpeQuery")
    info(s"[SPCP-ETL] Query for reading data from MBR table is: $mbrQuery")
    info(s"[SPCP-ETL] Query for reading data from HCA_RANKING table is: $hcaRankingQuery")
    info(s"[SPCP-ETL] Query for reading data from PROVIDER_INFO table is: $providerQuery")
    info(s"[SPCP-ETL] Query for reading data from RCODE_MAPPING table is: $rcodeMappingQuery")
    info(s"[SPCP-ETL] Query for reading data from wgsp_pcp_adrs table is: $wgspPcpAdrsQuery")

    val mbrPcpDF = splicemachineContext.internalDf(mbrPcpQuery)
    val mpeDF = splicemachineContext.internalDf(mpeQuery)
    val mbrDF = splicemachineContext.internalDf(mbrQuery)
    val hcaRankingDF = splicemachineContext.internalDf(hcaRankingQuery)
    val providerDF = splicemachineContext.internalDf(providerQuery)
    val rcodeMappingDF = splicemachineContext.internalDf(rcodeMappingQuery)

    val prodOfrgDF = splicemachineContext.internalDf(prodOfrgQuery)
    val prchsrOrgDF = splicemachineContext.internalDf(prchsrOrgQuery)
    val prchsrOrgDmgrphcDF = splicemachineContext.internalDf(prchsrOrgDmgrphcQuery)
    val bnftPkgDF = splicemachineContext.internalDf(bnftPkgQuery)
    val prodDF = splicemachineContext.internalDf(prodQuery)
    val hmoSpclstPcpIdDF = splicemachineContext.internalDf(hmoSpclstPcpIdQuery)
    val ntwkProvDF = splicemachineContext.internalDf(ntwkProvQuery)
    val wgspPcpAdrsDF = splicemachineContext.internalDf(wgspPcpAdrsQuery)

    val mapDF = Map("mbrPcp" -> mbrPcpDF, "rcodeMapping" -> rcodeMappingDF,
      "mpe" -> mpeDF, "mbr" -> mbrDF, "hcaRanking" -> hcaRankingDF,
      "provider" -> providerDF, "prodOfrg" -> prodOfrgDF,
      "prchsrOrg" -> prchsrOrgDF,
      "prchsrOrgDmgrphc" -> prchsrOrgDmgrphcDF,
      "bnftPkg" -> bnftPkgDF,
      "prod" -> prodDF,
      "hmoSpclstPcpId" -> hmoSpclstPcpIdDF,
      "ntwkProv" -> ntwkProvDF, "wgspPcpAdrs" -> wgspPcpAdrsDF)

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

    val mbrPcpDF = inDFs.getOrElse("mbrPcp", null)
    //    val cmpnyCfDF = inDFs.getOrElse("cmpnyCf", null)
    //    val mpeCoaDFRaw = inDFs.getOrElse("mpeCoa", null)
    val mpeDF = inDFs.getOrElse("mpe", null)
    val mbrDF = inDFs.getOrElse("mbr", null)
    val hcaRankingDF = inDFs.getOrElse("hcaRanking", null)
    val providerDF = inDFs.getOrElse("provider", null)
    //    val prodHrzntlHrchyDF = inDFs.getOrElse("prodHrzntlHrchy", null)
    //    val mbuHrzntlHrchyDF = inDFs.getOrElse("mbuHrzntlHrchy", null)
    val pcpAsgnmntRcodeMappingDF = inDFs.getOrElse("rcodeMapping", null)

    val prodOfrgDF = inDFs.getOrElse("prodOfrg", null)
    val prchsrOrgDF = inDFs.getOrElse("prchsrOrg", null)
    val prchsrOrgDmgrphcDF = inDFs.getOrElse("prchsrOrgDmgrphc", null)
    val bnftPkgDF = inDFs.getOrElse("bnftPkg", null)
    val prodDF = inDFs.getOrElse("prod", null)
    val hmoSpclstPcpIdDF = inDFs.getOrElse("hmoSpclstPcpId", null)
    val ntwkProvDF = inDFs.getOrElse("ntwkProv", null)
    val wgspPcpAdrsDF = inDFs.getOrElse("wgspPcpAdrs", null)

    //Step1: Left join between mbrPcpDF and pcpAsgnmntRcodeMappingDF
    val mbrPcp_rcodeMapping_joinDF = mbrPcpDF.join(
      pcpAsgnmntRcodeMappingDF,
      mbrPcpDF("src_pcp_asgnmnt_mthd_cd") === pcpAsgnmntRcodeMappingDF("rcode"), "inner")
    info("Step1: Inner join between mbrPcpDF and pcpAsgnmntRcodeMappingDF done.")

    //Step2: Left join between mbrPcpDF and mbrDF
    val mbrPcp_mbr_joinDF = mbrPcp_rcodeMapping_joinDF.join(mbrDF, Seq("mbr_key", "mbrshp_sor_Cd"), "left")
    info("Step2: Left join between mbrPcpDF and mbrDF Done.")

    val mbrPcp_mbr_mpe_joinDF = mbrPcp_mbr_joinDF.join(
      mpeDF,
      mpeDF("mbr_key") === mbrPcpDF("mbr_key") &&
        mpeDF("prod_ofrg_key") === mbrPcpDF("prod_ofrg_key") &&
        mpeDF("mbrshp_sor_cd") === mbrPcpDF("mbrshp_sor_cd") &&
        mpeDF("mbr_prod_enrlmnt_efctv_dt") === mbrPcpDF("mbr_prod_enrlmnt_efctv_dt"), "left")

    //Step6: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF and providerInfoDF

    val wgspPcp_PCPAdrsDF = providerDF.
      join(wgspPcpAdrsDF, Seq("prov_pcp_id", "src_sor_cd", "pcp_efctv_dt" /*, "adrs_typ_cd", "adrs_id" , "adrs_efctv_dt"*/ ))

    val mbrPcp_mb_mpe_pi_joinDF = mbrPcp_mbr_mpe_joinDF.join(
      wgspPcp_PCPAdrsDF,
      mbrPcpDF("pcp_id") === providerDF("prov_pcp_id"), "left")
    info("Step6: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_joinDF and providerInfoDF Done.")

    val mbrPcp_mbr_mpe_pi_hcarnk_joinDF = mbrPcp_mb_mpe_pi_joinDF.join(
      hcaRankingDF,
      providerDF("tax_id") === hcaRankingDF("tax_id") && wgspPcpAdrsDF("adrs_st_cd") === hcaRankingDF("adrs_st_cd"), //TODO: this is missing
      "left")

    val mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_joinDF = mbrPcp_mbr_mpe_pi_hcarnk_joinDF.join(
      prodOfrgDF,
      mpeDF("prod_ofrg_key") === prodOfrgDF("prod_ofrg_key"), "left")//.persist()

//    mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_joinDF.printSchema()
//    mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_joinDF.show()

    val prchsrOrgGrpDF = prchsrOrgDF.filter($"prchsr_org_type_cd" === "03")

    val mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_joinDF = mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_joinDF.join(
      prchsrOrgGrpDF.alias("A"),
      prodOfrgDF("mbrshp_sor_cd") === col("A.mbrshp_sor_cd")
        && prodOfrgDF("prchsr_org_nbr") === col("A.prchsr_org_nbr")
        && prodOfrgDF("prchsr_org_type_cd") === col("A.prchsr_org_type_cd"), "left")

    mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_joinDF.printSchema()

    val prchsrOrgSubGrpDF = prchsrOrgDF.filter($"prchsr_org_type_cd" === "04")

    val mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_prchOrgSub_joinDF = mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_joinDF.join(
      prchsrOrgSubGrpDF.alias("B"),
      prodOfrgDF("mbrshp_sor_cd") === col("B.mbrshp_sor_cd")
        && prodOfrgDF("rltd_prchsr_org_nbr") === col("B.prchsr_org_nbr")
        && prodOfrgDF("rltd_prchsr_org_type_cd") === col("B.prchsr_org_type_cd"), "left")

    mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_prchOrgSub_joinDF.printSchema()

    //Demographic table
    val prchsrOrgDmgGrpDF = prchsrOrgDmgrphcDF.filter($"prchsr_org_type_cd" === "03")
    val mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_prchOrgSub_pdmg_joinDF = mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_prchOrgSub_joinDF.join(
      prchsrOrgDmgGrpDF.alias("C"),
      prodOfrgDF("mbrshp_sor_cd") === col("C.mbrshp_sor_cd")
        && prodOfrgDF("prchsr_org_nbr") === col("C.prchsr_org_nbr")
        && prodOfrgDF("prchsr_org_type_cd") === col("C.prchsr_org_type_cd"), "left")

    mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_prchOrgSub_pdmg_joinDF.printSchema()

    val prchsrOrgDmgSubGrpDF = prchsrOrgDmgrphcDF.filter($"prchsr_org_type_cd" === "04")

    val prchDF = mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_prchOrgSub_pdmg_joinDF.join(
      prchsrOrgDmgSubGrpDF.alias("D"),
      prodOfrgDF("mbrshp_sor_cd") === col("D.mbrshp_sor_cd")
        && prodOfrgDF("rltd_prchsr_org_nbr") === col("D.prchsr_org_nbr")
        && prodOfrgDF("rltd_prchsr_org_type_cd") === col("D.prchsr_org_type_cd"), "left")
    mbrPcp_mbr_mpe_pi_hcaRnk_prodOfrg_prchOrg_prchOrgSub_pdmg_joinDF.printSchema()
    val prch_bnft_pkg_joinDF = prchDF.join(bnftPkgDF, bnftPkgDF("bnft_pkg_key") === prodOfrgDF("bnft_pkg_key"), "left")

    val prch_bnft_pkg_prod_joinDF = prch_bnft_pkg_joinDF.join(prodDF, prodDF("prod_id") === bnftPkgDF("prod_id") &&
      prodDF("prod_sor_cd") === bnftPkgDF("prod_sor_cd"), "left")

    val prch_bnft_pkg_prod_hmo_joinDF = prch_bnft_pkg_prod_joinDF.join(hmoSpclstPcpIdDF, hmoSpclstPcpIdDF("spclst_pcp_id") ===
      mbrPcpDF("pcp_id"), "left")

    val prch_bnft_pkg_prod_hmo_ntwk_joinDF = prch_bnft_pkg_prod_hmo_joinDF.join(ntwkProvDF, hmoSpclstPcpIdDF("prov_id") ===
      ntwkProvDF("prov_id"), "left")

    //Step8: Left join between mbrPcp_mbr_mpeCoa_CmpnyCf_mpe_provInfo_hcaRanking_joinDF and prodHrzntlHrchyDF

    prch_bnft_pkg_prod_hmo_ntwk_joinDF.printSchema()

    val finalReportingDF = prch_bnft_pkg_prod_hmo_ntwk_joinDF
      .select(
        lit(1).as("rpt_id"),
        mbrPcpDF("mbr_key"),
        mpeDF("hc_id"),
        mbrDF("sbscrbr_id"),
        mbrDF("mbr_sqnc_nbr"),
        mbrPcpDF("mbr_prod_enrlmnt_efctv_dt"),
        mbrDF("frst_nm"),
        mbrDF("last_nm"),
        mbrPcpDF("sor_dtm").cast(DateType).as("assignment_dt"),
        mbrPcpDF("src_pcp_asgnmnt_mthd_cd"),
        when(mbrPcpDF("src_pcp_asgnmnt_mthd_cd") === pcpAsgnmntRcodeMappingDF("rcode"), pcpAsgnmntRcodeMappingDF("assignment_type"))
          otherwise (lit("NA")) as ("assignment_type"),
        mbrPcpDF("pcp_efctv_dt"),
        providerDF("tax_id"),
        hcaRankingDF("tax_id_nm"),
        mbrPcpDF("pcp_id"),
        providerDF("pcp_frst_nm"),
        providerDF("pcp_mid_nm"),
        providerDF("pcp_last_nm"),
        hcaRankingDF("pcp_mdo_rank"),
        //TODO extra columns
        mbrPcpDF("mbrshp_sor_cd"),
        ntwkProvDF("ntwk_id"),
        prodDF("prod_id"),
        col("B.src_grp_nbr"),
        coalesce(col("C.prchsr_org_nm"), col("D.prchsr_org_nm")).alias("grpname"),
        col("B.src_subgrp_nbr"),
        col("A.prchsr_org_nbr"),
        prodOfrgDF("pkg_nbr"),
        mpeDF("prod_ofrg_key"),
        current_timestamp().alias("created_dtm"),
        lit(user_id).alias("created_by"),
        current_timestamp().alias("last_updtd_dtm"),
        lit(user_id).alias("last_updtd_by")).persist(StorageLevel.MEMORY_AND_DISK)

    info("Step9: Deriving the required columns from  rprtngDF done. Final datframe generated!")

    finalReportingDF.printSchema()
    var finalPcpDFMap: Map[String, DataFrame] = null
    //finalReportingDF.show(false)

    info("End of processing")

    if (sendNtfctnMail) {
      val minRowCnt = config.getInt("reportingDataMinCnt")
      val maxRowCnt = config.getInt("reportingDataMaxCnt")
      val finalDfCnt = finalReportingDF.count()
      info(s"[SPCP-ETL] Final Report Table Count is : " + finalDfCnt)
      if ((finalDfCnt < minRowCnt) || (finalDfCnt > maxRowCnt)) {
        finalPcpDFMap = Map("reportingTargetTbl" -> null)
        info("Member data not within threshold limits")
        val mailDF = spark.sparkContext.parallelize(List((reportingTargetTable, minRowCnt, maxRowCnt, finalDfCnt)))
          .toDF("Table_Nm", "Min_Cnt", "Max_Cnt", "Current_Record_Cnt")
        mailDF.show()
        SpcpMailUtils.sendMail(config, mailDF)
        throw new Exception("[SPCP-ETL] : RPT_MBR_PCP_DTLS Data Load Suspended. Row count beyond threshold.")
      } else {
        finalPcpDFMap = Map("reportingTargetTbl" -> finalReportingDF.distinct())
      }
    } else {
      finalPcpDFMap = Map("reportingTargetTbl" -> finalReportingDF.distinct())
    }

    info(s"[SPCP-ETL] processing() Data Completed at: " + DateTime.now())
    info(s"[SPCP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

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

    val targetTableReportingDF = outDFs.getOrElse("reportingTargetTbl", null)

    val modTargetDF = SPCPDataFrameUtils.columnsInUpper(targetTableReportingDF)

    info("capital schema is : ")
    modTargetDF.printSchema()

    splicemachineContext.execute(s"delete from $spcpSpliceDB.$reportingTargetTable")
    info(s"$spcpSpliceDB.$reportingTargetTable has been truncated")

    splicemachineContext.insert(modTargetDF, spcpSpliceDB + """.""" + reportingTargetTable)
    info("REPORTING RECORDS INSERTED")
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
    splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDB + "." + spcpAuditTable)

  }

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






