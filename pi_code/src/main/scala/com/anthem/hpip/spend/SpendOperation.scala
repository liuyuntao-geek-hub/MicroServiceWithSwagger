package com.anthem.hpip.spend

import java.text.SimpleDateFormat
import java.util.Calendar

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.from_unixtime
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.round
import org.apache.spark.sql.functions.sum
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions.unix_timestamp
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.OperationSession
import com.anthem.hpip.helper.Operator
import com.anthem.hpip.util.DateUtils
import org.apache.spark.storage.StorageLevel

case class hpipAudit(program: String, user_id: String, app_id: String, start_time: String, app_duration: String, status: String)

class SpendOperation(confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env, queryFileCategory) with Operator {

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
    return Map(("df", loadHiveTableData()))
  }

  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val startTime = DateTime.now
    info(s"[HPIP-ETL] Processing Data Started: $startTime")

    val correctSpendMap = processCorrectSpend(inDFs.getOrElse("df", null))
    val finalResultDF = processMBRAllow(correctSpendMap.getOrElse("spend_spend_spend_date", null))

    info(s"[HPIP-ETL] processing() Data Completed at: " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    return Map(("df", finalResultDF)) ++ correctSpendMap

  }

  def writeData(rdd: RDD[(String, Int)]): Unit = {
    rdd.collect().foreach(println)
  }

  def varianceCalculation(targetTableDFs: Map[String, DataFrame]): Unit = {
    info(s"[HPIP-ETL] Variance Logic started ")
    val startTime = DateTime.now()
    info(s"[HPIP-ETL] started varaince logic at : $startTime")

    //  val hiveDB = config.getString("hiveDB")
    val warehouseHiveDB = config.getString("warehouse-hive-db")
    info(s"[HPIP-ETL] The warehouse database schema is $warehouseHiveDB")
    val varianceTableName = config.getString("variance_table")
    info(s"[HPIP-ETL] The Variance table name is $varianceTableName")
    val thresholdSpend = config.getString("threshold_Spend").toDouble
    info(s"[HPIP-ETL] The threshold for spend is $thresholdSpend")
    val previous_variance_data_query = config.getString("query_spend_variance").replaceAll(ConfigKey.warehouseDBPlaceHolder, config.getString("warehouse-hive-db")).toLowerCase()
    info(s"[HPIP-ETL] The previous variance data query is $previous_variance_data_query")
    val lastUpdatedDate = config.getString("audit-column-name")
    info(s"[HPIP-ETL] The Audit column date is $lastUpdatedDate")

    val subjectArea = config.getString("sas_program_name_Spend")
    info(s"[HPIP-ETL] subject Area is : $subjectArea")

    val prvVarDF = spark.sql(previous_variance_data_query)

    val targetTableName = config.getString("spendTargetTableName")
    info(s"[HPIP-ETL] The target table name is $targetTableName")
    val partitionColumn = config.getString("default_partition_col")
    info(s"[HPIP-ETL] The partition column name is $partitionColumn")
    val partitionValue = config.getString("spend_partition_value")
    info(s"[HPIP-ETL] The partition column value is $partitionValue")

    val vdfcurrent = targetTableDFs.getOrElse(targetTableName, null)
    val mtclmDefaultThreshold = config.getString("threshold_mtclm_default").toDouble
    val vdfcurrentAgg = vdfcurrent.agg(count(lit(1)).as("Total_Members"), sum($"ALLOWED_TOTAL").as("TOTAL_ALLOWED_AMOUNT"), sum($"PAID_TOTAL").as("TOTAL_PAID_AMOUNT")).first()

    info(vdfcurrentAgg)
    
    val c1 = vdfcurrentAgg.getLong(0)

    //hadling the null pointer exception for wrong filter where no results processed
    var c2, c3: Double = 0
    var c4: Long = 0
    
    if (c1 != 0 && !vdfcurrentAgg.isNullAt(1) && !vdfcurrentAgg.isNullAt(2) ) {
    c2 = vdfcurrentAgg.get(1).toString().toDouble
    c3 = vdfcurrentAgg.get(2).toString().toDouble
    c4 = vdfcurrent.count()
    }
    
    val check_threshold = (x: Double, y: Double) => { if (x > y || x < -y) "true" else "false" }

    if (prvVarDF.count() == 0) {

        val VarianceDF = List(VarianceSchema(targetTableName, partitionColumn, partitionValue, "MEMBER_MCID", "Total_Members", "count(member_mcid)", c1, mtclmDefaultThreshold, thresholdSpend, "true", subjectArea),
        VarianceSchema(targetTableName, partitionColumn, partitionValue, "ALLOWED_TOTAL", "TOTAL_ALLOWED_AMOUNT", "sum(ALLOWED_AMOUNT)", c2, mtclmDefaultThreshold, thresholdSpend, "true", subjectArea),
        VarianceSchema(targetTableName, partitionColumn, partitionValue, "PAID_TOTAL", "TOTAL_PAID_AMOUNT", "sum(PAID_AMOUNT)", c3, mtclmDefaultThreshold, thresholdSpend, "true", subjectArea),
        VarianceSchema(targetTableName, partitionColumn, partitionValue, "ROW_COUNT", "TOTAL_ROWS", "count(ROWS)", c4, mtclmDefaultThreshold, thresholdSpend, "true", subjectArea)).toDF().toDF()

      val vDF = VarianceDF.select(($"tableName"), ($"partitionColumn"), ($"partitionValue"), ($"columnName"), ($"operationDescription"), ($"operation"), round(($"operationValue"), 2), round(($"percentageVariance"), 2), ($"threshold"), ($"isThresholdCrossed"), ($"subjectArea"))
      vDF.withColumn(lastUpdatedDate, lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)

      println(s"Variance logic ended")

      info(s"[HPIP-ETL] variance() logic Completed at :" + DateTime.now())
      info(s"[HPIP-ETL] Time Taken varaince() logic :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    } else {

      val VarianceDF = prvVarDF.map(f => {
        val f6 = f.get(6).toString.toDouble
        f.get(4).toString match {
          case "Total_Members" =>
            VarianceSchema(targetTableName, partitionColumn, partitionValue, "MEMBER_MCID", "Total_Members", "count(member_mcid)", c1, ((c1 - f6) / f6) * 100, thresholdSpend, check_threshold((((c1 - f6) / f6) * 100), thresholdSpend), subjectArea)

          case "TOTAL_ALLOWED_AMOUNT" =>
            VarianceSchema(targetTableName, partitionColumn, partitionValue, "ALLOWED_TOTAL", "TOTAL_ALLOWED_AMOUNT", "sum(ALLOWED_AMOUNT)", c2, ((c2 - f6) / f6) * 100, thresholdSpend, check_threshold((((c2 - f6) / f6) * 100), thresholdSpend), subjectArea)

          case "TOTAL_PAID_AMOUNT" =>
            VarianceSchema(targetTableName, partitionColumn, partitionValue, "PAID_TOTAL", "TOTAL_PAID_AMOUNT", "sum(PAID_AMOUNT)", c3, ((c3 - f6) / f6) * 100, thresholdSpend, check_threshold((((c3 - f6) / f6) * 100), thresholdSpend), subjectArea)

          case "TOTAL_ROWS" =>
            VarianceSchema(targetTableName, partitionColumn, partitionValue, "ROW_COUNT", "TOTAL_ROWS", "count(ROWS)", c4, ((c4 - f6) / f6) * 100, thresholdSpend, check_threshold((((c4 - f6) / f6) * 100), thresholdSpend), subjectArea)

        }
      }).toDF()

      val vDF = VarianceDF.select(($"tableName"), ($"partitionColumn"), ($"partitionValue"), ($"columnName"), ($"operationDescription"), ($"operation"), round(($"operationValue"), 2), round(($"percentageVariance"), 2), ($"threshold"), ($"isThresholdCrossed"), ($"subjectArea"))

      vDF.withColumn(lastUpdatedDate, lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)

      println(s"Variance logic ended")

      info(s"[HPIP-ETL] variance() logic Completed at :" + DateTime.now())
      info(s"[HPIP-ETL] Time Taken varaince() logic :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
    }
  }
  
  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val startTime = DateTime.now()
    //Writing the data to a table in Hive
    info(s"[HPIP-ETL] Writing Dataframes to Hive started at: $startTime")

    val df = outDFs.getOrElse("df", null)
    val spend_s_spend_date_d = outDFs.getOrElse("spend_s_spend_date_d", null)
    val spend_spend_date_sum = outDFs.getOrElse("spend_spend_date_sum", null)
    val spend_s_spend_date = outDFs.getOrElse("spend_s_spend_date", null)
    val spend_spend_spend_date = outDFs.getOrElse("spend_spend_spend_date", null)
    spark.conf.set("hive.exec.dynamic.partition", "true")
    spark.conf.set("hive.exec.dynamic.partition.mode", "nonstrict")
    val warehouseHiveDB = config.getString("warehouse-hive-db")
    info(s"[HPIP-ETL] The warehouse database schema is $warehouseHiveDB")
    val stagingHiveDB = config.getString("stage-hive-db")
    info(s"[HPIP-ETL] The Staging Hive schema is $stagingHiveDB")
    val targetTableName = config.getString("spendTargetTableName")
    info(s"[HPIP-ETL] The target table name is $targetTableName")
    val staging0TableName = config.getString("spendStaging0TableName")
    info(s"[HPIP-ETL] The Staging -0 Table name is $staging0TableName")
    val staging1TableName = config.getString("spendStaging1TableName")
    info(s"[HPIP-ETL] The Staging -1 table name is $staging1TableName")
    val staging2TableName = config.getString("spendStaging2TableName")
    info(s"[HPIP-ETL] The Staging -2 Table name is $staging2TableName")
    val staging3TableName = config.getString("spendStaging3TableName")
    info(s"[HPIP-ETL] the Staging -3 table name is $staging3TableName")
    val partitionColumn = config.getString("default_partition_col")
    info(s"[HPIP-ETL] The Partition Column is $partitionColumn")
    val partitionValue = config.getString("spend_partition_value")
    info(s"[HPIP-ETL] The partition column value is $partitionValue")
    val lastUpdatedDate = config.getString("audit-column-name")
    info(s"[HPIP-ETL] The Audit column name is $lastUpdatedDate")

  
    val dfwrite = df.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
     
    dfwrite.persist(StorageLevel.MEMORY_AND_DISK)
    info("the record count value of spendmrlevel table is "+ dfwrite.count())
    
//    dfwrite.write.mode("overwrite").partitionBy(partitionColumn).insertInto(warehouseHiveDB + """.""" + targetTableName)
    dfwrite.write.mode("overwrite").insertInto(warehouseHiveDB + """.""" + targetTableName)

    if (config.getString("spendStagingFlag").equals("true")) {
      val spend_s_spend_date_d_write = spend_s_spend_date_d.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
      spend_s_spend_date_d_write.write.mode("overwrite").insertInto(stagingHiveDB + """.""" + staging0TableName)
//      spend_s_spend_date_d_write.write.mode("overwrite").partitionBy(partitionColumn).insertInto(stagingHiveDB + """.""" + staging0TableName)

      val spend_spend_date_sum_write = spend_spend_date_sum.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
      spend_spend_date_sum_write.write.mode("overwrite").insertInto(stagingHiveDB + """.""" + staging1TableName)
//      spend_spend_date_sum_write.write.mode("overwrite").partitionBy(partitionColumn).insertInto(stagingHiveDB + """.""" + staging1TableName)

      val spend_s_spend_date_write = spend_s_spend_date.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
      spend_s_spend_date_write.write.mode("overwrite").insertInto(stagingHiveDB + """.""" + staging2TableName)
//      spend_s_spend_date_write.write.mode("overwrite").partitionBy(partitionColumn).insertInto(stagingHiveDB + """.""" + staging2TableName)

      val spend_spend_spend_date_write = spend_spend_spend_date.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
      spend_spend_spend_date_write.write.mode("overwrite").insertInto(stagingHiveDB + """.""" + staging3TableName)
//      spend_spend_spend_date_write.write.mode("overwrite").partitionBy(partitionColumn).insertInto(stagingHiveDB + """.""" + staging3TableName)
    }

    info(s"[HPIP-ETL] writing the data to target tables is Completed at: " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for writing the data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
    varianceCalculation(Map((targetTableName, dfwrite)))
  }

  def loadHiveTableData(): DataFrame = {
    //3. Bring Spend and Store it
    val startTime = DateTime.now
    info(s"[HPIP-ETL] The loading of Data started with start time at :  + $startTime")

    val inboundHiveDB = config.getString("inbound-hive-db")
    info(s"[HPIP-ETL] The in-bound hive database schema is $inboundHiveDB")
    val tableName = config.getString("spendSourceTableName")
    info(s"[HPIP-ETL] The source table name is $tableName")

    val pi_spnd_query = config.getString("spendSourceDataQuery").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    info(s"[HPIP-ETL] The query for reading the data from source table $pi_spnd_query")

    //Direct Hive table load
    // val spendRawDF = spark.sql(s"select * from $hiveDB.$tableName limit 100")
    val spendRawDF = spark.sql(pi_spnd_query)
    
    info(s"[HPIP-ETL] Loading data completed at : " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for loading the data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    spendRawDF
  }

  def processCorrectSpend(spendRawDF: DataFrame): Map[String, DataFrame] = {
    spendRawDF.printSchema()
    info(s"[HPIP-ETL] Processing Correct Spend logic started ")

    val srcFilter: String = config.getString("spendSourceDataFilter")
    info(s"[HPIP-ETL] Spend - source data filter is $srcFilter")
    val startDate = srcFilter.split(",")(0)
    val endDate = srcFilter.split(",")(1)

    println(s"StartDate  and EndDate values are  $srcFilter")

      val spnd_d = spendRawDF.select($"MCID", $"YEAR_MNTH_NBR" as "MONTH", $"PMNT_TYPE",
      $"QTR_NM", $"MBU", $"FUNDCF", $"PRODCF", $"ALLOWED", $"PAID", $"STRT_DT" as "START", $"END_DT" as "END")

    val spndPmtTypeAggDF = spendRawDF.groupBy($"PMNT_TYPE").agg(
      sum($"ALLOWED").alias("Allowed_Total"),
      sum($"PAID").alias("Paid_Total"))
    
      spndPmtTypeAggDF.printSchema()

    val spnd = spendRawDF.withColumn("DRVDDOS", from_unixtime(unix_timestamp(getLastDayOfTheMonth($"YEAR_MNTH_NBR"),"yyyyMMdd"),"yyyy-MM-dd").cast("timestamp")).drop("YEAR_MNTH_NBR").withColumnRenamed("STRT_DT", "START").withColumnRenamed("END_DT", "END")

    val spndGrpd = spnd.groupBy("MCID", "DRVDDOS", "PMNT_TYPE","START","END","MBU","FUNDCF","PRODCF").agg(
      sum($"ALLOWED").alias("Sum_Allowed"),
      sum($"PAID").alias("Sum_Paid"))
      spndGrpd.printSchema()
    val payTypeSet = Seq("INPT", "OUTPT", "CAP", "PHRCY", "PROF", "TRNSPLNT")

    val carrectSpndDFs = for {
      payType <- payTypeSet
    } yield (payType + "DF", correctSpend(spndGrpd, payType))

    val unionDF = carrectSpndDFs.reduceLeft((a, b) => ((a._1), (a._2.union(b._2))))._2
     unionDF.printSchema()
    val df3 = unionDF.groupBy("MCID", "DRVDDOS","START","END","MBU","FUNDCF","PRODCF").agg(sum("INPAT_Allowed").alias("INPAT_Allowed"), sum("INPAT_Paid").alias("INPAT_Paid"),
      sum("OUTPAT_Allowed").alias("OUTPAT_Allowed"), sum("OUTPAT_Paid").alias("OUTPAT_Paid"), sum("CPTTN_ALLOWED").alias("CPTTN_ALLOWED"),
      sum("CPTTN_PAID").alias("CPTTN_PAID"), sum("RX_ALLOWED").alias("RX_ALLOWED"), sum("RX_PAID").alias("RX_PAID"),
      sum("PROF_ALLOWED").alias("PROF_ALLOWED"), sum("PROF_PAID").alias("PROF_PAID"), sum("TRNSPLNT_ALLOWED").alias("TRNSPLNT_ALLOWED"),
      sum("TRNSPLNT_PAID").alias("TRNSPLNT_PAID"), sum("Allowed_Total").alias("Allowed_Total"), sum("Paid_Total").alias("Paid_Total"))
    df3.printSchema()
    val dfcombinedResult = df3.select($"MCID", $"DRVDDOS", $"INPAT_Allowed", $"INPAT_Paid", $"OUTPAT_Allowed", $"OUTPAT_Paid",
      $"CPTTN_ALLOWED", $"CPTTN_PAID", $"RX_ALLOWED", $"RX_PAID", $"PROF_ALLOWED", $"PROF_PAID", $"TRNSPLNT_ALLOWED",
      $"TRNSPLNT_PAID", ($"INPAT_Allowed" + $"OUTPAT_Allowed" + $"CPTTN_ALLOWED" + $"RX_ALLOWED" + $"PROF_ALLOWED" + $"TRNSPLNT_ALLOWED").alias("Allowed_Total"),
      ($"INPAT_Paid" + $"OUTPAT_Paid" + $"CPTTN_PAID" + $"RX_PAID" + $"PROF_PAID" + $"TRNSPLNT_PAID").alias("Paid_Total"), $"START", $"END", $"MBU", $"FUNDCF", $"PRODCF")
     dfcombinedResult.printSchema()
    info(s"[HPIP-ETL] Processing Correct Spend logic completed ")
    Map(("spend_s_spend_date_d", spnd_d), ("spend_spend_date_sum", spndPmtTypeAggDF), ("spend_s_spend_date", spnd), ("spend_spend_spend_date", dfcombinedResult))

  }
  def processMBRAllow(df: DataFrame): DataFrame = {
    //Create Data Set of rolling 12 months of allowed dollars by MCID;
    info(s"[HPIP-ETL] Processing  MBRAllow logic started ")
    val spndMBRAllowDF = df
      .groupBy($"MCID")
      .agg(
        sum($"INPAT_Allowed").as("ALLOWED_INPATIENT_NO_TRANSPLANT"),
        sum($"TRNSPLNT_ALLOWED").as("ALLOWED_TRANSPLANT"),
        sum($"OUTPAT_Allowed").as("ALLOWED_OUTPATIENT"),
        sum($"PROF_ALLOWED").as("ALLOWED_PROFESSIONAL"),
        sum($"RX_ALLOWED").as("ALLOWED_RX"),
        sum($"CPTTN_ALLOWED").as("ALLOWED_CAPITATED"),
        sum($"CPTTN_ALLOWED").as("ALLOWED_OTHER"),
        sum($"INPAT_Allowed".+($"TRNSPLNT_ALLOWED")).as("ALLOWED_INPATIENT"),
        sum($"INPAT_Allowed".+($"TRNSPLNT_ALLOWED").+($"OUTPAT_Allowed").+($"PROF_ALLOWED").+($"RX_ALLOWED").+($"CPTTN_ALLOWED")).as("ALLOWED_TOTAL"),
        sum($"INPAT_Paid").as("PAID_INPATIENT_NO_TRANSPLANT"),
        sum($"TRNSPLNT_PAID").as("PAID_TRANSPLANT"),
        sum($"OUTPAT_Paid").as("PAID_OUTPATIENT"),
        sum($"PROF_PAID").as("PAID_PROFESSIONAL"),
        sum($"RX_PAID").as("PAID_RX"),
        sum($"CPTTN_PAID").as("PAID_CAPITATED"),
        sum($"CPTTN_PAID").as("PAID_OTHER"),
        sum($"INPAT_Paid".+($"TRNSPLNT_PAID")).as("PAID_INPATIENT"),
        sum($"INPAT_Paid".+($"TRNSPLNT_PAID").+($"OUTPAT_Paid").+($"PROF_PAID").+($"RX_PAID").+($"CPTTN_PAID")).as("PAID_TOTAL"))

    val startDate = config.getString("spendSourceDataFilter").split(",")(0)
    info(s"[HPIP-ETL] The start date is $startDate")
    val endDate = config.getString("spendSourceDataFilter").split(",")(1)
    info(s"[HPIP-ETL] The end date is $endDate")

    val spndMBR = spndMBRAllowDF.withColumn("sEFFDT12", lit("" + startDate)).withColumn(
      "sENDDT",
      lit("" + endDate)).orderBy("MCID")

    info(s"[HPIP-ETL] Processing  MBRAllow logic completed ")
    (spndMBR)

  }

  def correctSpend(df: DataFrame, pmntType: String): DataFrame = {

    pmntType.trim() match {
      case "INPT" =>
        df.filter("PMNT_TYPE rlike 'INPT'").select($"MCID", $"DRVDDOS", $"START", $"END", $"MBU", $"FUNDCF", $"PRODCF",$"Sum_Allowed" as "INPAT_Allowed", $"Sum_Paid" as "INPAT_Paid", lit(0) as "OUTPAT_Allowed", lit(0) as "OUTPAT_Paid", lit(0) as "CPTTN_ALLOWED", lit(0) as "CPTTN_PAID", lit(0) as "RX_ALLOWED", lit(0) as "RX_PAID", lit(0) as "PROF_ALLOWED", lit(0) as "PROF_PAID", lit(0) as "TRNSPLNT_ALLOWED", lit(0) as "TRNSPLNT_PAID", lit(0) as "Allowed_Total", lit(0) as "Paid_Total")
      case "OUTPT" =>
        df.filter("PMNT_TYPE rlike 'OUTPT'").select($"MCID", $"DRVDDOS",  $"START", $"END", $"MBU", $"FUNDCF", $"PRODCF",lit(0) as "INPAT_Allowed", lit(0) as "INPAT_Paid", $"Sum_Allowed" as "OUTPAT_Allowed", $"Sum_Paid" as "OUTPAT_Paid", lit(0) as "CPTTN_ALLOWED", lit(0) as "CPTTN_PAID", lit(0) as "RX_ALLOWED", lit(0) as "RX_PAID", lit(0) as "PROF_ALLOWED", lit(0) as "PROF_PAID", lit(0) as "TRNSPLNT_ALLOWED", lit(0) as "TRNSPLNT_PAID", lit(0) as "Allowed_Total", lit(0) as "Paid_Total")

      case "CAP" =>
        df.filter("PMNT_TYPE rlike 'CAP'").select($"MCID", $"DRVDDOS",  $"START", $"END", $"MBU", $"FUNDCF", $"PRODCF",lit(0) as "INPAT_Allowed", lit(0) as "INPAT_Paid", lit(0) as "OUTPAT_Allowed", lit(0) as "OUTPAT_Paid", $"Sum_Allowed" as "CPTTN_ALLOWED", $"Sum_Paid" as "CPTTN_PAID", lit(0) as "RX_ALLOWED", lit(0) as "RX_PAID", lit(0) as "PROF_ALLOWED", lit(0) as "PROF_PAID", lit(0) as "TRNSPLNT_ALLOWED", lit(0) as "TRNSPLNT_PAID", lit(0) as "Allowed_Total", lit(0) as "Paid_Total")

      case "PHRCY" =>
        df.filter("PMNT_TYPE rlike 'PHRCY'").select($"MCID", $"DRVDDOS",  $"START", $"END", $"MBU", $"FUNDCF", $"PRODCF",lit(0) as "INPAT_Allowed", lit(0) as "INPAT_Paid", lit(0) as "OUTPAT_Allowed", lit(0) as "OUTPAT_Paid", lit(0) as "CPTTN_ALLOWED", lit(0) as "CPTTN_PAID", $"Sum_Allowed" as "RX_ALLOWED", $"Sum_Paid" as "RX_PAID", lit(0) as "PROF_ALLOWED", lit(0) as "PROF_PAID", lit(0) as "TRNSPLNT_ALLOWED", lit(0) as "TRNSPLNT_PAID", lit(0) as "Allowed_Total", lit(0) as "Paid_Total")

      case "PROF" =>
        df.filter("PMNT_TYPE rlike 'PROF'").select($"MCID", $"DRVDDOS", $"START", $"END", $"MBU", $"FUNDCF", $"PRODCF", lit(0) as "INPAT_Allowed", lit(0) as "INPAT_Paid", lit(0) as "OUTPAT_Allowed", lit(0) as "OUTPAT_Paid", lit(0) as "CPTTN_ALLOWED", lit(0) as "CPTTN_PAID", lit(0) as "RX_ALLOWED", lit(0) as "RX_PAID", $"Sum_Allowed" as "PROF_ALLOWED", $"Sum_Paid" as "PROF_PAID", lit(0) as "TRNSPLNT_ALLOWED", lit(0) as "TRNSPLNT_PAID", lit(0) as "Allowed_Total", lit(0) as "Paid_Total")

      case "TRNSPLNT" =>
        df.filter("PMNT_TYPE rlike 'TRNSPLNT'").select($"MCID", $"DRVDDOS", $"START", $"END", $"MBU", $"FUNDCF", $"PRODCF", lit(0) as "INPAT_Allowed", lit(0) as "INPAT_Paid", lit(0) as "OUTPAT_Allowed", lit(0) as "OUTPAT_Paid", lit(0) as "CPTTN_ALLOWED", lit(0) as "CPTTN_PAID", lit(0) as "RX_ALLOWED", lit(0) as "RX_PAID", lit(0) as "PROF_ALLOWED", lit(0) as "PROF_PAID", $"Sum_Allowed" as "TRNSPLNT_ALLOWED", $"Sum_Paid" as "TRNSPLNT_PAID", lit(0) as "Allowed_Total", lit(0) as "Paid_Total")

    }

  }

  //getLastDayOfTheMonth UDF
  val getLastDayOfTheMonth = udf((date: Long) => {
    val formatter = new SimpleDateFormat("yyyyMMdd")

    val dt = formatter.parse(date.toString() + "01")
    val calendar = Calendar.getInstance()
    calendar.setTime(dt)

    calendar.add(Calendar.MONTH, 1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.add(Calendar.DATE, -1)

    val lastDay = calendar.getTime()
    formatter.format(lastDay)
  })

}
