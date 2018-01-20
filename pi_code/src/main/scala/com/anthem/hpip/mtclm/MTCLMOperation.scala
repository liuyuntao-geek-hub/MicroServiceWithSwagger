package com.anthem.hpip.mtclm

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.round
import org.apache.spark.sql.functions.substring
import org.apache.spark.storage.StorageLevel
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.OperationSession
import com.anthem.hpip.helper.Operator
import com.anthem.hpip.util.DateUtils

case class hpipAudit(program: String, user_id: String, app_id: String, start_time: String, app_duration: String, status: String)
class MTCLMOperation(configPath: String, env: String, queryFileCategory: String) extends OperationSession(configPath, env, queryFileCategory) with Operator {

  import spark.implicits._

  var datafrmeArray = ArrayBuffer[DataFrame]()
  var dataFrameArray = new ArrayBuffer[DataFrame]()
  var columnMapIndexValue = Map[String, Int]()

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var start = ""

  var listBuffer = ListBuffer[hpipAudit]()

  @Override def beforeLoadData() {

    import spark.implicits._

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
    import spark.implicits._

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

    val clmQuery = config.getString("query_clm").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    info(s"[HPIP-ETL] Query for reading data from CLM table is $clmQuery")

    val clmLineQuery = config.getString("query_clm_line").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    info(s"[HPIP-ETL] Query for reading data from CLM LINE table is $clmLineQuery")

    val clmPaidQuery = config.getString("query_clm_paid").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    info(s"[HPIP-ETL] Query for reading data from CLM PAID table is $clmPaidQuery")

    val fcltyClmQuery = config.getString("query_fclty_clm").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    info(s"[HPIP-ETL] Query for reading data from FCLTYCLM table is $fcltyClmQuery")

    val clmLineCoaQuery = config.getString("query_clm_line_coa").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    info(s"[HPIP-ETL] Query for reading data from CLM LINE COA table is $clmLineCoaQuery")

    //Reading the queries from config file
    println("Reading the queries from config file")
    val clmDF = spark.sql(clmQuery) //.repartition(3000)
    val clmLineDF = spark.sql(clmLineQuery) //.repartition(3000)
    val clmPaidDF = spark.sql(clmPaidQuery) //.repartition(3000)

    val fcltyClmDF = spark.sql(fcltyClmQuery) //.repartition(3000)
    val clmLineCoaDF = spark.sql(clmLineCoaQuery) //.repartition(3000)

    //    clmDF.persist(StorageLevel.MEMORY_AND_DISK)
    //    clmLineDF.persist(StorageLevel.MEMORY_AND_DISK)
    //    clmPaidDF.persist(StorageLevel.MEMORY_AND_DISK)

    //Creating Map of Data frames

    val mapDF = Map("clm" -> clmDF, "clm_line" -> clmLineDF, "clm_paid" -> clmPaidDF, "fclty_clm" -> fcltyClmDF, "clm_line_coa" -> clmLineCoaDF)
    info(s"[HPIP-ETL] Loading data completed at : " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for loading the data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    mapDF

  }

  def processData(inMapDF: Map[String, DataFrame]): Map[String, DataFrame] = {
    val startTime = DateTime.now
    //Processing the data
    info(s"[HPIP-ETL] Processing Data Started: $startTime")
    //Reading the data frames as elements from Map

    val clmDF = inMapDF.getOrElse("clm", null)
    val clmLineDF = inMapDF.getOrElse("clm_line", null)
    val clmPaidDF = inMapDF.getOrElse("clm_paid", null)
    val fcltyClmDF = inMapDF.getOrElse("fclty_clm", null)
    val clmLineCoaDF = inMapDF.getOrElse("clm_line_coa", null)

    val lastUpdatedDate = config.getString("audit-column-name").toLowerCase
    info(s"[HPIP-ETL] The Audit column name is  $lastUpdatedDate")
    //Processing the data using joins for MTCLM
    info(s"[HPIP-ETL] Processing the data using joins for MTCLM")

    val clmJoinClmLineDF = clmDF.join(clmLineDF, clmDF("clm_adjstmnt_key") === clmLineDF("clm_adjstmnt_key") && clmDF("adjdctn_dt") === clmLineDF("adjdctn_dt") && clmDF("clm_sor_cd") === clmLineDF("clm_sor_cd"), "inner").select(clmDF("clm_adjstmnt_key"), clmDF("mbrshp_sor_cd"), clmDF("clm_its_host_cd"), clmDF("srvc_rndrg_type_cd"), clmDF("src_prov_natl_prov_id"), clmDF("src_billg_tax_id"), clmDF("mbr_key"), clmDF("rx_filled_dt"), clmDF("clm_sor_cd"), clmDF("adjdctn_dt"))
    val clmJoinClmLineJoinclmPaidDF = clmJoinClmLineDF.join(clmPaidDF, clmJoinClmLineDF("clm_adjstmnt_key") === clmPaidDF("clm_adjstmnt_key"), "left_outer").select(clmJoinClmLineDF("clm_adjstmnt_key"), clmJoinClmLineDF("mbrshp_sor_cd"), clmJoinClmLineDF("clm_its_host_cd"), clmJoinClmLineDF("srvc_rndrg_type_cd"), clmJoinClmLineDF("src_prov_natl_prov_id"), clmJoinClmLineDF("src_billg_tax_id"), clmJoinClmLineDF("mbr_key"), clmJoinClmLineDF("rx_filled_dt"), clmPaidDF("gl_post_dt"), clmJoinClmLineDF("clm_sor_cd"), clmJoinClmLineDF("adjdctn_dt"))
    val mtclmDF = clmJoinClmLineJoinclmPaidDF.join(fcltyClmDF, clmJoinClmLineJoinclmPaidDF("clm_adjstmnt_key") === fcltyClmDF("clm_adjstmnt_key") && clmJoinClmLineJoinclmPaidDF("clm_sor_cd") === fcltyClmDF("clm_sor_cd"), "left_outer").select(clmJoinClmLineJoinclmPaidDF("clm_adjstmnt_key"), clmJoinClmLineJoinclmPaidDF("mbrshp_sor_cd"), clmJoinClmLineJoinclmPaidDF("clm_its_host_cd"), clmJoinClmLineJoinclmPaidDF("srvc_rndrg_type_cd"), clmJoinClmLineJoinclmPaidDF("src_prov_natl_prov_id"), clmJoinClmLineJoinclmPaidDF("src_billg_tax_id"), clmJoinClmLineJoinclmPaidDF("mbr_key"), clmJoinClmLineJoinclmPaidDF("rx_filled_dt"), clmJoinClmLineJoinclmPaidDF("gl_post_dt"), fcltyClmDF("admt_dt"), clmJoinClmLineJoinclmPaidDF("clm_sor_cd"), clmJoinClmLineJoinclmPaidDF("adjdctn_dt")).distinct
    val mtclmWithAuditColumn = mtclmDF.withColumn(lastUpdatedDate, lit(current_timestamp()))
    val mtclmColumnsOrderRearranged = mtclmWithAuditColumn.select(mtclmWithAuditColumn("clm_adjstmnt_key"), mtclmWithAuditColumn("mbrshp_sor_cd"), mtclmWithAuditColumn("clm_its_host_cd"), mtclmWithAuditColumn("srvc_rndrg_type_cd"), mtclmWithAuditColumn("src_prov_natl_prov_id"), mtclmWithAuditColumn("src_billg_tax_id"), mtclmWithAuditColumn("mbr_key"), mtclmWithAuditColumn("rx_filled_dt"), mtclmWithAuditColumn("gl_post_dt"), mtclmWithAuditColumn("admt_dt"), mtclmWithAuditColumn(lastUpdatedDate), mtclmWithAuditColumn("adjdctn_dt"), mtclmWithAuditColumn("clm_sor_cd"))

    //    mtclmColumnsOrderRearranged.persist(StorageLevel.MEMORY_AND_DISK)
    //    info("The record count of mtclm table is " + mtclmColumnsOrderRearranged.count())

    //					clmDF.unpersist()
    //					clmLineDF.unpersist()
    //					clmPaidDF.unpersist()
    //					fcltyClmDF.unpersist()
    //					clmLineCoaDF.unpersist()
    //					clmJoinClmLineDF.unpersist()
    //					clmJoinClmLineJoinclmPaidDF.unpersist()
    //					mtclmDF.unpersist()
    //					mtclmWithAuditColumn.unpersist()

    //Processing the data using joins for MTCLM_COA
    info(s"[HPIP-ETL] Processing the data using joins for MTCLM_COA")
    val clmLineFilterdDF = clmLineDF.filter(clmLineDF("clm_line_encntr_cd").isin("N", "NA") && clmLineDF("clm_line_stts_cd").isin("PD", "APRVD", "PDZB", "APRZB", "VOID") && (clmLineDF("hlth_srvc_type_cd").notEqual("ADA")))
    val clmLineCoaFilterdDF = clmLineCoaDF.filter(((substring(clmLineCoaDF("prod_cf_cd"), 1, 1).isin("M", "P", "B")) || (clmLineCoaDF("prod_cf_cd").isin("TPPMC", "TADVZ", "TAOPZ"))) && ((clmLineCoaDF("prod_cf_cd").isin("BSEAT", "BSVEA") === false)))
    val coaClmJoinclmLineDF = clmDF.join(clmLineFilterdDF, clmDF("clm_adjstmnt_key") === clmLineFilterdDF("clm_adjstmnt_key") && clmDF("adjdctn_dt") === clmLineFilterdDF("adjdctn_dt") && clmDF("clm_sor_cd") === clmLineFilterdDF("clm_sor_cd"), "inner").select(clmDF("clm_adjstmnt_key"), clmLineFilterdDF("clm_line_nbr"), clmLineFilterdDF("inpat_cd"), clmLineFilterdDF("clm_line_encntr_cd"), clmLineFilterdDF("clm_line_stts_cd"), clmLineFilterdDF("hlth_srvc_type_cd"), clmDF("clm_sor_cd"), clmDF("adjdctn_dt"))
    val mtclmCoaDF = coaClmJoinclmLineDF.join(clmLineCoaFilterdDF, coaClmJoinclmLineDF("clm_adjstmnt_key") === clmLineCoaFilterdDF("clm_adjstmnt_key") && coaClmJoinclmLineDF("clm_line_nbr") === clmLineCoaFilterdDF("clm_line_nbr") && coaClmJoinclmLineDF("clm_sor_cd") === clmLineCoaFilterdDF("clm_sor_cd"), "inner").select(coaClmJoinclmLineDF("clm_adjstmnt_key"), coaClmJoinclmLineDF("clm_line_nbr"), coaClmJoinclmLineDF("inpat_cd"), clmLineCoaFilterdDF("mbu_cf_cd"), clmLineCoaFilterdDF("cmpny_cf_cd"), coaClmJoinclmLineDF("clm_line_encntr_cd"), coaClmJoinclmLineDF("clm_line_stts_cd"), coaClmJoinclmLineDF("hlth_srvc_type_cd"), coaClmJoinclmLineDF("clm_sor_cd"), coaClmJoinclmLineDF("adjdctn_dt"), clmLineCoaFilterdDF("prod_cf_cd")).distinct
    val mtclmCoaWithAuditColumn = mtclmCoaDF.withColumn(lastUpdatedDate, lit(current_timestamp()))
    val mtclmCoaColumnsOrderRearranged = mtclmCoaWithAuditColumn.select(mtclmCoaWithAuditColumn("clm_adjstmnt_key"), mtclmCoaWithAuditColumn("clm_line_nbr"), mtclmCoaWithAuditColumn("mbu_cf_cd"), mtclmCoaWithAuditColumn("cmpny_cf_cd"), mtclmCoaWithAuditColumn("inpat_cd"), mtclmCoaWithAuditColumn(lastUpdatedDate), mtclmCoaWithAuditColumn("prod_cf_cd"))

    //    mtclmCoaColumnsOrderRearranged.persist(StorageLevel.MEMORY_AND_DISK)
    //    info("The record count value of mtclmcoa table is " + mtclmCoaColumnsOrderRearranged.count())

    //val colsToRemove = Seq("clm_line_encntr_cd", "clm_line_stts_cd", "hlth_srvc_type_cd", "adjdctn_dt")
    // val mtclmCoaColumnsDroped = mtclmCoaColumnsOrderRearranged.select(mtclmCoaColumnsOrderRearranged.columns.filter(colName => !colsToRemove.contains(colName)).map(colName => new Column(colName)): _*)

    //  val mtclmCoaColumnsDropedDF = mtclmCoaColumnsOrderRearranged.drop("clm_line_encntr_cd").drop("clm_line_stts_cd").drop("hlth_srvc_type_cd").drop("adjdctn_dt").drop("clm_sor_cd ")

    //					clmLineFilterdDF.unpersist()
    //					clmLineCoaFilterdDF.unpersist()
    //					coaClmJoinclmLineDF.unpersist()
    //					mtclmCoaDF.unpersist()
    //					mtclmCoaWithAuditColumn.unpersist()
    //    clmDF.unpersist()
    //    clmLineDF.unpersist()
    //    clmPaidDF.unpersist()

    //Creating a map of data frames

    val targetTableNameMtclm = config.getString("mtclm_target_table").toLowerCase()
    info(s"[HPIP-ETL] mclm_coa table name is : $targetTableNameMtclm")

    val targetTableNameMtclmCoa = config.getString("mtclm_coa_target_table").toLowerCase()
    info(s"[HPIP-ETL] mclm table name is : $targetTableNameMtclmCoa")

    val outMapDF = Map(targetTableNameMtclm -> mtclmColumnsOrderRearranged, targetTableNameMtclmCoa -> mtclmCoaColumnsOrderRearranged)

    info(s"[HPIP-ETL] processing() Data Completed at: " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    outMapDF

  }

  def writeData(map: Map[String, DataFrame]): Unit = {
    val startTime = DateTime.now()
    //Writing the data to a table in Hive
    info(s"[HPIP-ETL] Writing Dataframes to Hive started at: $startTime")

    map.foreach(x => {

      val tableName = x._1

      val startTime = DateTime.now()
      info(s"[HPIP-ETL] Writing Dataframes to Hive table $tableName started at: $startTime")

      val hiveDB = config.getString("inbound-hive-db")
      info(s"[HPIP-ETL] Hive In-bound Database schema is $hiveDB")

      val warehouseHiveDB = config.getString("warehouse-hive-db")
      info(s"[HPIP-ETL] Warehouse Database Schema is $warehouseHiveDB")

      info(s"[HPIP-ETL] Target tablename is $tableName")

      //Displaying the sample of data
      val df = x._2
      info(s"[HPIP-ETL] Showing the contents of df")

      //Truncating the previous table created
      info(s"[HPIP-ETL] Truncating the previous table created")
      spark.sql("truncate table " + warehouseHiveDB + """.""" + tableName)
      var partitionColumn = ""
      if (tableName.equalsIgnoreCase("mtclm")) {
        partitionColumn = config.getString("mtclm_partition_col").toLowerCase()
      } else {
        partitionColumn = config.getString("mtclm_coa_partition_col").toLowerCase()
      }
      info(s"[HPIP-ETL] Partition column name: $partitionColumn")
      //Creating the table in Hive
      spark.conf.set("hive.exec.dynamic.partition", "true")
      spark.conf.set("hive.exec.dynamic.partition.mode", "nonstrict")
      //df.write.mode("overwrite").partitionBy(partitionColumn).insertInto(warehouseHiveDB + """.""" + tablename)
      df.write.mode("overwrite").insertInto(warehouseHiveDB + """.""" + tableName)
      info(s"[HPIP-ETL] Table created as $warehouseHiveDB.$tableName")
      
      varianceCalculation(tableName)

      info(s"[HPIP-ETL] writing the data to target table $tableName is Completed at: " + DateTime.now())
      info(s"[HPIP-ETL] Time Taken for writing the data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    })
    info(s"[HPIP-ETL] writing the data to target tables is Completed at: " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for writing the data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    //    varianceCalculation(map)
  }

  //  def varianceCalculation(targetTableDFs: Map[String, DataFrame]): Unit = {
  def varianceCalculation(tableName: String): Unit = {
    //Calculating the variance percentage of row count with the previous count

    val startTime = DateTime.now()
    info(s"[HPIP-ETL] started varaince logic at : $startTime")
    //    targetTableDFs.foreach(x => {

    //Reading the required variables
    val warehouseHiveDB = config.getString("warehouse-hive-db")

    // val tableName = x._1
    info(s"[HPIP-ETL] Warehouse Database Schema is $warehouseHiveDB")
    info(s"[HPIP-ETL] Target tablename is $tableName")

    val varianceTableName = config.getString("variance_table").toLowerCase
    info(s"[HPIP-ETL] Variance tale name is : $varianceTableName")

    val subjectArea = config.getString("sas_program_name_MTCLM")
    info(s"[HPIP-ETL] subject Area is : $subjectArea")

    //      val vdfcurrent = targetTableDFs.getOrElse(tablename, null)
    val thresholdMtclm = config.getString("threshold_mtclm").toDouble
    info(s"[HPIP-ETL] The threshold variance for $tableName is $thresholdMtclm")
    val thresholdMtclmDefault = config.getString("threshold_mtclm_default").toDouble
    info(s"[HPIP-ETL] The default threshold variance for $tableName is $thresholdMtclm")
    var previous_variance_data_query = ""
    
//    val targetDB = config.getString("warehouse-hive-db")
//    info(s"[HPIP-ETL] target Database schema is : $targetDB")
//    
    //Reading the previous data query depending on the table name
    if (tableName.equalsIgnoreCase("mtclm")) {

      //Target table is MTCLM
      info(s"[HPIP-ETL] ---The target table is MTCLM----")
      previous_variance_data_query = config.getString("previous_variance_data_query_mtclm").replaceAll(ConfigKey.warehouseDBPlaceHolder, warehouseHiveDB).toLowerCase()
      info(s"[HPIP-ETL] Data load query is  previous_variance_data_query_mtclm $previous_variance_data_query")

    } else {

      //Target table is MTCLM_COA
      info(s"[HPIP-ETL] ---The target table is MTCLM_COA----")
      previous_variance_data_query = config.getString("previous_variance_data_query_mtclm_coa").replaceAll(ConfigKey.warehouseDBPlaceHolder, warehouseHiveDB).toLowerCase()
      info(s"[HPIP-ETL] Data load query is  previous_variance_data_query_mtclm_coa $previous_variance_data_query")

    }

    //Checking if the threshold is crossed for the variance
    val check_threshold = (x: Double, y: Double) => { if (x > y || x < -y) "true" else "false" }
    val prvVarDF = spark.sql(previous_variance_data_query)

    import spark.implicits._

    //Getting the record count fot the current data load
    //      val varianceCount = vdfcurrent.count().toDouble
    val varianceCount = spark.sql(s"select count(1) from $warehouseHiveDB.$tableName").first().getLong(0)
    info(s"[HPIP-ETL] he record count for the current load is $varianceCount")

    if (prvVarDF.head(1).isEmpty) {

      //Appending the values to the variance data frame for the first time insertion into variance table
      info(s"Appending the values to the variance data frame for the first time insertion into variance table")
      val toBeInserted = Seq((tableName, "NA", "NA", "NA", "row count", "count(*)", varianceCount.toDouble, thresholdMtclmDefault, thresholdMtclm, "true", subjectArea))
      val varianceDF = toBeInserted.map(a => { (MTCLMVarianceSchema(a._1, a._2, a._3, a._4, a._5, a._6, a._7, a._8, a._9, a._10, a._11)) }).toDF
      //Inserting into the variance table
      info(s"[HPIP-ETL] Inserting the values to the variance data frame for the first time")
      val vDF = varianceDF.select(($"tableName"), ($"partitionColumn"), ($"partitionValue"), ($"columnName"), ($"operationDescription"), ($"operation"), round(($"operationValue"), 2), round(($"percentageVariance"), 2), ($"threshold"), ($"isThresholdCrossed"), ($"subjectArea"))
      vDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)
      //varianceDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)
      //showing the contents of variance data frame
      info(s"[HPIP-ETL] Showing the contents of Data frame for variance")

    } else {
      //Getting the previous record count from the dataset
      
      val prvCount = prvVarDF.first().getDouble(6)
//    		  val prvCount = prvVarDF.select($"operationValue").first().toString().toDouble
      //        val prvCount = prvVarDF.map(f => f.get(6)).first().toString().toDouble
      //	val prvCount = (i:Int)=>prvVarDF.rdd.zipWithIndex.filter(_._2==i).map(_._1).first().toString().split(",")(6)
      info(s"[HPIP-ETL] The old record count is $prvCount")

      //Appending the values to the variance data frame
      val varianceDF = prvVarDF.map(f => MTCLMVarianceSchema(tableName, "NA", "NA", "NA", "row count", "count(*)", varianceCount, (((varianceCount - prvCount) / prvCount) * 100), thresholdMtclm, check_threshold((((varianceCount - prvCount) / varianceCount) * 100), thresholdMtclm), subjectArea)).toDF()
      //Inserting into the variance table

      //	varianceDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)
      val vDF = varianceDF.select(($"tableName"), ($"partitionColumn"), ($"partitionValue"), ($"columnName"), ($"operationDescription"), ($"operation"), round(($"operationValue"), 2), round(($"percentageVariance"), 2), ($"threshold"), ($"isThresholdCrossed"), ($"subjectArea"))
      vDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)
      info(s"[HPIP-ETL] variance() logic Completed at :" + DateTime.now())
      info(s"[HPIP-ETL] Time Taken varaince() logic :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

      //showing the contents of variance data frame
      info(s"[HPIP-ETL] Showing the contents of Data frame for variance")

    }
    //    })

    info(s"[HPIP-ETL] variance() logic Completed at :" + DateTime.now())
    info(s"[HPIP-ETL] Time Taken varaince() logic :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
  }

}