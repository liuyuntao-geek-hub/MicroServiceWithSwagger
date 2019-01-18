package com.anthem.hca.spcp.provider.pcpgeocode

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.concat_ws
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.expr
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.when
import org.apache.spark.sql.types.DoubleType
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.config.ConfigKey
import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import com.anthem.hca.spcp.util.SPCPUDFs

/**
 * This class is meant to get the geocodes for various Providers.
 * It is meant for populating the PCP geocodes and ref_adrs table with the 
 * latitude and longitude values for various providers using the Bing API.
 * 
 * The source consists of WGSP_PCP_ADRS and REF_ADRS table. 
 *
 *
 * @author  Avula Narsireddy 
 * 
 */

class PCPGeoCodeOperation(configPath: String, env: String, queryFileCategory: String)
    extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var listBuffer = ListBuffer[Audit]()
  
  /*
   * This method loads the required tables into dataframes.
   * 
   * @param  None
   * @return  Map[String, DataFrame]
   */

  override def extractData(): Map[String, DataFrame] = {
    val startTime = DateTime.now
    info(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")
    info("Reading the queries from config file")

    val refAdrsQuery = config.getString("query_ref_adrs").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    val wgspPcpAdrsQuery = config.getString("query_wgsp_pcp_adrs").toLowerCase().replace(ConfigKey.cdlWgspDBPlaceHolder, cdlWgspDB)

    info(s"[SPCP-ETL] Query for reading data from REF_ADRS table is: $refAdrsQuery")
    info(s"[SPCP-ETL] Query for reading data from WGSP_PCP_ADRS table is: $wgspPcpAdrsQuery")
   // info("bing Key is " + bingKey)

    val refAdrsDF = splicemachineContext.internalDf(refAdrsQuery)
    val wgspPcpAdrsDF = splicemachineContext.internalDf(wgspPcpAdrsQuery)

    val wgspPcpAdrsReqDF = wgspPcpAdrsDF.select($"prov_pcp_id", $"adrs_line_1_txt",
      $"adrs_line_2_txt", $"adrs_city_nm", $"adrs_st_cd", $"adrs_zip_cd", $"adrs_zip_plus_4_cd",
      $"pa_cmnctn_typ_value", $"hmo_type_cd", $"src_sor_cd", $"pcp_efctv_dt", $"adrs_typ_cd",
      $"adrs_cnty_cd", $"adrs_id", $"adrs_efctv_dt")

    val mapDF = Map("refAadrs" -> refAdrsDF, "wgsppcpadrs" -> wgspPcpAdrsReqDF)

    info(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }
  
  /*
   * This method handles all transformation and Bing API calls.
   * 
   * @param   Map[String, DataFrame]
   * @return  Map[String, DataFrame]
   */

  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val startTime = DateTime.now
    info(s"[SPCP-ETL] Processing Data Started: $startTime")

    val refAdrsDF = inDFs.getOrElse("refAadrs", null)
    val wgspPcpAdrsDF = inDFs.getOrElse("wgsppcpadrs", null)

    val pcpAdrsRefAdrsJoinDF = wgspPcpAdrsDF.join(refAdrsDF, Seq("adrs_line_1_txt",  "adrs_city_nm", "adrs_st_cd",
      "adrs_zip_cd"), "left").select(wgspPcpAdrsDF("*"), $"latd_cordnt_nbr", $"lngtd_cordnt_nbr")

    var outMapDF = Map("pcpGeoCodeDF" -> pcpAdrsRefAdrsJoinDF, "pcpRefAdrsDF" -> null)

    val pcpAdrs_latlong_mis_DF = pcpAdrsRefAdrsJoinDF.filter($"latd_cordnt_nbr".isNull || $"lngtd_cordnt_nbr".isNull)

    val latLongMissingRecordCnt = pcpAdrs_latlong_mis_DF.count()
    info("Number of records with latitude and longitude not found in ref_adrs" + latLongMissingRecordCnt)

    if (latLongMissingRecordCnt > 0) {

      val pcpAdrs_latlong_from_refAdrs = pcpAdrsRefAdrsJoinDF.except(pcpAdrs_latlong_mis_DF)
      info("Number of records with latitude and longitude found in ref_adrs " + pcpAdrs_latlong_from_refAdrs.count())
      val geoCodeMissingAdrsDF = pcpAdrs_latlong_mis_DF.select($"adrs_st_cd", $"adrs_city_nm",
        $"adrs_zip_cd", $"adrs_line_1_txt", $"adrs_line_2_txt").distinct().cache //distinct is used avoid duplicate adrs lookup with bing
        info("Number of bing hits to happen " + geoCodeMissingAdrsDF.count())
      val geoCodeAdrsDF = bingServicelookup(geoCodeMissingAdrsDF)
      val pcpAdrs_lat_long_df = pcpAdrs_latlong_mis_DF.join(geoCodeAdrsDF, Seq("adrs_line_1_txt","adrs_city_nm",
        "adrs_st_cd", "adrs_zip_cd"), "left")

      val pcpAdrs_geocodes_frombing = pcpAdrs_lat_long_df.select($"prov_pcp_id", $"adrs_line_1_txt",
        pcpAdrs_latlong_mis_DF("adrs_line_2_txt"), $"adrs_city_nm", $"adrs_st_cd", $"adrs_zip_cd", $"adrs_zip_plus_4_cd",
        $"pa_cmnctn_typ_value", $"hmo_type_cd", $"src_sor_cd", $"pcp_efctv_dt", $"adrs_typ_cd",
        $"adrs_cnty_cd", $"adrs_id", $"adrs_efctv_dt", geoCodeAdrsDF("latd_cordnt_nbr") as "latd_cordnt_nbr",
        geoCodeAdrsDF("lngtd_cordnt_nbr") as "lngtd_cordnt_nbr")

      val pcpAdrs_lat_long_refadrs = pcpAdrs_geocodes_frombing.select($"adrs_line_1_txt", 
        $"adrs_city_nm", $"adrs_st_cd", $"adrs_zip_cd", $"latd_cordnt_nbr", $"lngtd_cordnt_nbr").distinct
        .filter(pcpAdrs_geocodes_frombing("latd_cordnt_nbr").notEqual(lit("0.0")) && 
            pcpAdrs_geocodes_frombing("lngtd_cordnt_nbr").notEqual(lit("0.0")))

      outMapDF += ("pcpRefAdrsDF" -> pcpAdrs_lat_long_refadrs)

      val pcp_adrs_final = pcpAdrs_latlong_from_refAdrs.union(pcpAdrs_geocodes_frombing).distinct()

      outMapDF += ("pcpGeoCodeDF" -> pcp_adrs_final)

    }


    info(s"[SPCP-ETL] processing() Data Completed at: " + DateTime.now())
    info(s"[SPCP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    outMapDF

  }

  
  /*
   * This method loads data into the pcp geocodes target table 
   * and ref_adrs table.
   * 
   * @param  Map[String, DataFrame]
   * @return None
   */
  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val pcpGeocodeTargetTable = config.getString("pcpGeocodeTargetTable")
    val pcpGeoCodeDF = outDFs.getOrElse("pcpGeoCodeDF", null)
    val pcpGeoCodeUpperCaseColDF = SPCPDataFrameUtils.columnsInUpper(pcpGeoCodeDF).withColumn("LAST_UPDTD_DTM", current_timestamp())

    splicemachineContext.execute(s"delete from $spcpSpliceDB.$pcpGeocodeTargetTable")
    info(s"$spcpSpliceDB.$pcpGeocodeTargetTable has been deleted")

    info(s"$spcpSpliceDB.$pcpGeocodeTargetTable insert started")
    splicemachineContext.insert(pcpGeoCodeUpperCaseColDF, spcpSpliceDB + """.""" + pcpGeocodeTargetTable)
    info(s"$spcpSpliceDB.$pcpGeocodeTargetTable insert completed")

    val pcpRefAdrsDF = outDFs.getOrElse("pcpRefAdrsDF", null)
    if (pcpRefAdrsDF != null) {
      val pcpRefAdrsUppercaseDF = SPCPDataFrameUtils.columnsInUpper(pcpRefAdrsDF).withColumn("LAST_UPDTD_DTM", current_timestamp())
      val pcpGeocodeRefAdrsTable = config.getString("pcpGeocodeRefAdrsTable")
      splicemachineContext.insert(pcpRefAdrsUppercaseDF, spcpSpliceDB + "." + pcpGeocodeRefAdrsTable)
      info("PCP GEO CODE COMMITTTED")
    }
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
  
  /*
   * This is a UDF for Bing service lookup. 
   * 
   */

  def bingServicelookup(df: DataFrame): DataFrame = {
    
    val bingKeyJceksLoc = config.getString("bingKey.jceks.location")
    val bingKeyAlias = config.getString("bingKey.jceks.alias")
    val bingKey = getCredentialSecret(bingKeyJceksLoc, bingKeyAlias)


    val bingUrl = config.getString("bing.url")
    //val bingKey = config.getString("bing.key")

    info(s"[SPCP-ETL: Bing URL is $bingUrl]")
    info(s"[SPCP-ETL: Bing key is $bingKey]")

    val dfModified = df.withColumn("adrs_txt", when(df.col("adrs_line_2_txt").isNull, $"adrs_line_1_txt")
      .otherwise(concat_ws(" ", $"adrs_line_1_txt", $"adrs_line_2_txt")))

    val pcpAdrs_latlong_df = dfModified.withColumn(
      "latitude_longitude",
      SPCPUDFs.geoCodeUDF($"adrs_st_cd", $"adrs_city_nm", $"adrs_zip_cd", $"adrs_txt", lit(bingUrl), lit(bingKey))).cache

    // pcpAdrs_latlong_df is cached because the below split logic is enforcing to call deocode udf multiple times
    val geoCodeAdrsDF = pcpAdrs_latlong_df.select(
      pcpAdrs_latlong_df("*"),
      expr("split(latitude_longitude,',')[0]").as("latd_cordnt_nbr").cast(DoubleType),
      expr("split(latitude_longitude,',')[1]").as("lngtd_cordnt_nbr").cast(DoubleType)).drop($"latitude_longitude")
      .drop($"adrs_txt").drop($"adrs_line_2_txt")

    (geoCodeAdrsDF)

  }

}