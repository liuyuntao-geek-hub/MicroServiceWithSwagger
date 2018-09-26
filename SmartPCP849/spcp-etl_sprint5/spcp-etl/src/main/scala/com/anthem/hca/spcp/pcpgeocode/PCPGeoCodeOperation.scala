package com.anthem.hca.spcp.pcpgeocode

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.expr
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.types.DoubleType
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spcp.helper.Audit
import com.anthem.hca.spcp.helper.OperationSession
import com.anthem.hca.spcp.helper.Operator
import com.anthem.hca.spcp.util.DateUtils
import com.anthem.hca.spcp.util.SPCPDataFrameUtils
import com.anthem.hca.spcp.util.SPCPUDFs

class PCPGeoCodeOperation(configPath: String, env: String, queryFileCategory: String)
    extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var listBuffer = ListBuffer[Audit]()

  override def extractData(): Map[String, DataFrame] = {
    val startTime = DateTime.now
    println(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")
    println("Reading the queries from config file")

    val refAdrsQuery = config.getString("query_ref_adrs").toLowerCase() //.replace(ConfigKey.sourceDBPlaceHolder, inboundHiveDB).replace(ConfigKey.effdt12PlaceHolder, config.getString(ConfigKey.effdt12)).replace(ConfigKey.adjdtPlaceHolder, config.getString(ConfigKey.adjdt)).toLowerCase()
    val wgspPcpAdrsQuery = config.getString("query_wgsp_pcp_adrs").toLowerCase() //.replace(ConfigKey.sourceDBPlaceHolder, inboundHiveDB).replace(ConfigKey.effdt12PlaceHolder, config.getString(ConfigKey.effdt12)).replace(ConfigKey.enddtPlaceHolder, config.getString(ConfigKey.enddt)).toLowerCase()

    println(s"[SPCP-ETL] Query for reading data from REF_ADRS table is: $refAdrsQuery")
    println(s"[SPCP-ETL] Query for reading data from WGSP_PCP_ADRS table is: $wgspPcpAdrsQuery")

    val refAdrsDF = splicemachineContext.df(refAdrsQuery)
    val wgspPcpAdrsDF = splicemachineContext.df(wgspPcpAdrsQuery)

    val wgspPcpAdrsReqDF = wgspPcpAdrsDF.select($"prov_pcp_id", $"adrs_line_1_txt",
      $"adrs_line_2_txt", $"adrs_city_nm", $"adrs_st_cd", $"adrs_zip_cd", $"adrs_zip_plus_4_cd",
      $"pa_cmnctn_typ_value", $"hmo_type_cd", $"src_sor_cd", $"pcp_efctv_dt", $"adrs_typ_cd",
      $"adrs_cnty_cd", $"adrs_id", $"adrs_efctv_dt")

    val mapDF = Map("refAadrs" -> refAdrsDF, "wgsppcpadrs" -> wgspPcpAdrsReqDF)

    println(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())

    mapDF
  }

  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val startTime = DateTime.now
    println(s"[SPCP-ETL] Processing Data Started: $startTime")

    val refAdrsDF = inDFs.getOrElse("refAadrs", null)
    val wgspPcpAdrsDF = inDFs.getOrElse("wgsppcpadrs", null)

    val pcpAdrsRefAdrsJoinDF = wgspPcpAdrsDF.join(refAdrsDF, Seq("adrs_line_1_txt", "adrs_line_2_txt", "adrs_city_nm", "adrs_st_cd",
      "adrs_zip_cd"), "left").select(wgspPcpAdrsDF("*"), $"latd_cordnt_nbr", $"lngtd_cordnt_nbr")

    var outMapDF = Map("pcpGeoCodeDF" -> pcpAdrsRefAdrsJoinDF, "pcpRefAdrsDF" -> null)

    val pcpAdrs_latlong_mis_DF = pcpAdrsRefAdrsJoinDF.filter($"latd_cordnt_nbr".isNull || $"lngtd_cordnt_nbr".isNull).cache

    val latLongMissingRecordCnt = pcpAdrs_latlong_mis_DF.count()
    println("=========================" + latLongMissingRecordCnt)

    if (latLongMissingRecordCnt > 0) {

      val pcpAdrs_latlong_from_refAdrs = pcpAdrsRefAdrsJoinDF.except(pcpAdrs_latlong_mis_DF)
      println("=========================" + pcpAdrs_latlong_from_refAdrs.count())
      val geoCodeMissingAdrsDF = pcpAdrs_latlong_mis_DF.select($"adrs_st_cd", $"adrs_city_nm",
        $"adrs_zip_cd", $"adrs_line_1_txt").distinct() //avoid duplicate adrs lookup
      val geoCodeAdrsDF = bingServicelookup(geoCodeMissingAdrsDF)
      val pcpAdrs_lat_long_df = pcpAdrs_latlong_mis_DF.join(geoCodeAdrsDF, Seq("adrs_line_1_txt", "adrs_city_nm",
        "adrs_st_cd", "adrs_zip_cd"), "left")

      val pcpAdrs_geocodes_frombing = pcpAdrs_lat_long_df.select($"prov_pcp_id", $"adrs_line_1_txt",
        $"adrs_line_2_txt", $"adrs_city_nm", $"adrs_st_cd", $"adrs_zip_cd", $"adrs_zip_plus_4_cd",
        $"pa_cmnctn_typ_value", $"hmo_type_cd", $"src_sor_cd", $"pcp_efctv_dt", $"adrs_typ_cd",
        $"adrs_cnty_cd", $"adrs_id", $"adrs_efctv_dt", geoCodeAdrsDF("latd_cordnt_nbr") as "latd_cordnt_nbr",
        geoCodeAdrsDF("lngtd_cordnt_nbr") as "lngtd_cordnt_nbr")

      val pcpAdrs_lat_long_refadrs = pcpAdrs_geocodes_frombing.select($"adrs_line_1_txt", $"adrs_line_2_txt",
        $"adrs_city_nm", $"adrs_st_cd", $"adrs_zip_cd", $"latd_cordnt_nbr", $"lngtd_cordnt_nbr").distinct

      outMapDF += ("pcpRefAdrsDF" -> pcpAdrs_lat_long_refadrs)

      val pcp_adrs_final = pcpAdrs_latlong_from_refAdrs.union(pcpAdrs_geocodes_frombing).distinct()

      outMapDF += ("pcpGeoCodeDF" -> pcp_adrs_final)

    }


    println(s"[SPCP-ETL] processing() Data Completed at: " + DateTime.now())
    println(s"[SPCP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    outMapDF

  }

  override def loadData(outDFs: Map[String, DataFrame]): Unit = {

    val pcpGeocodeTargetTable = config.getString("pcpGeocodeTargetTable")
    val pcpGeoCodeDF = outDFs.getOrElse("pcpGeoCodeDF", null)
    val pcpGeoCodeUpperCaseColDF = SPCPDataFrameUtils.columnsInUpper(pcpGeoCodeDF).withColumn("LAST_UPDTD_DTM", current_timestamp())

    splicemachineContext.execute(s"delete from $spcpSpliceDb.$pcpGeocodeTargetTable")
    println(s"$spcpSpliceDb.$pcpGeocodeTargetTable has been truncated")

    println(s"$spcpSpliceDb.$pcpGeocodeTargetTable insert started")
    splicemachineContext.insert(pcpGeoCodeUpperCaseColDF, spcpSpliceDb + """.""" + pcpGeocodeTargetTable)
    println(s"$spcpSpliceDb.$pcpGeocodeTargetTable insert completed")

    val pcpRefAdrsDF = outDFs.getOrElse("pcpRefAdrsDF", null)
    if (pcpRefAdrsDF != null) {
      val pcpRefAdrsUppercaseDF = SPCPDataFrameUtils.columnsInUpper(pcpRefAdrsDF).withColumn("LAST_UPDTD_DTM", current_timestamp())
      val pcpGeocodeRefAdrsTable = config.getString("pcpGeocodeRefAdrsTable")
      splicemachineContext.insert(pcpRefAdrsUppercaseDF, spcpSpliceDb + "." + pcpGeocodeRefAdrsTable)
      println("PCP GEO CODE COMMITTTED")
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

  def bingServicelookup(df: DataFrame): DataFrame = {

    val bingUrl = config.getString("bing.url")
    val bingKey = config.getString("bing.key")

    println(s"[SPCP-ETL: Bing URL is $bingUrl]")
    println(s"[SPCP-ETL: Bing key is $bingKey]")

    val pcpAdrs_latlong_df = df.withColumn(
      "latitude_longitude",
      SPCPUDFs.geoCodeUDF($"adrs_st_cd", $"adrs_city_nm", $"adrs_zip_cd", $"adrs_line_1_txt", lit(bingUrl), lit(bingKey))).cache

    // pcpAdrs_latlong_df is cached because the below split logic is enforcing to call deocode udf multiple times
    val geoCodeAdrsDF = pcpAdrs_latlong_df.select(
      pcpAdrs_latlong_df("*"),
      expr("split(latitude_longitude,',')[0]").as("latd_cordnt_nbr").cast(DoubleType),
      expr("split(latitude_longitude,',')[1]").as("lngtd_cordnt_nbr").cast(DoubleType)).drop($"latitude_longitude")

    (geoCodeAdrsDF)

  }

}