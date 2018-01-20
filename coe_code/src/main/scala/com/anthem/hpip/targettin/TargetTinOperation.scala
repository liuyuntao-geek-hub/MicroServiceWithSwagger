package com.anthem.hpip.targettin

import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.functions.countDistinct
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.expr
import org.apache.spark.sql.functions.input_file_name
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.udf

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.OperationSession
import com.anthem.hpip.helper.Operator

class TargetTinOperation(confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env, queryFileCategory) with Operator {

  import hiveContext.implicits._

  var datafrmeArray = ArrayBuffer[DataFrame]()
  val datafrmeVarianceArray = ArrayBuffer[DataFrame]()

  def loadData(): Map[String, DataFrame] = {

    info("loadData() start")
    val startTime = System.currentTimeMillis()
    info("loadData() start time " + startTime)

    //Extracting the excel file names from the specified path
    val tinAllFilesDF = hiveContext.read.text(config.getString("tin_files_path")).withColumn("filename", input_file_name)
    val tinFilesNamesDF = tinAllFilesDF.select("filename")

    tinFilesNamesDF.show()
    //creating the regex pattern to filter files
    tinFilesNamesDF.collect().distinct.foreach { filePath =>
      info(filePath)
      val extractedFilePath = filePath.toString().replaceAll("[\\[\\]]", "")
      //Patter for extracting state values from filename
      val stateExtractPattern = "-(.*)?-".r
      val stateValues = stateExtractPattern.findAllMatchIn(extractedFilePath)
      var states = ""
      //replacing '-' character enclosed by state example -> - CA -
      stateValues.foreach { state => states = state.toString().replaceAll("- ", "").replaceAll(" -", "") }
      val caSheetName = "PC_2"
      val otherSheetName = "Provider Dtl"
      //extracting sheet name from excel file if CA it will extract PC_2 if it is other excel it will extract Provider_Dtl
      if (states.toString().trim().equals("CA")) {
        extractingExcel(extractedFilePath, states.toString(), caSheetName)
      } else {
        extractingExcel(extractedFilePath, states, otherSheetName)
      }
    }

    info("entered into UNION zone")
    //Creating master Source File By combining all extracted Sheets
    val combinedDataframes = datafrmeArray.reduce((a, b) => a.unionAll(b))
    //udf for upperCase for Creating new column
    info("udf uppercase")
    val upperc = (x: String) => { if (x != null) x.toUpperCase().toString() else "NAN" }
    val upperCase = udf(upperc)
    info("changing Targeted columns")
    //Changing column names
    val columnRenamedDF = combinedDataframes.withColumnRenamed("Tax ID", "TIN").withColumnRenamed("Tax ID Name", "TARGETED_GROUP_OLD").withColumn("TARGET_DATE", expr("date_sub(date_add(\"1900-1-1\",cast(Date as int)),2)")).withColumn("TARGETED_GROUP", upperCase($"TARGETED_GROUP_OLD")).drop("Date")
    //Filtering data because of empty rows in excel sheet
    val columnRenamedFilterDF = columnRenamedDF.filter("TIN is not null")
    info("changed column names")

    val currentDate = com.anthem.hpip.util.DateUtils.getCurrentDate()
    val filteredDataAfterTodayDF = columnRenamedFilterDF.filter(columnRenamedFilterDF("TARGET_DATE").gt(lit(currentDate)))

    info("filtered date based on today date")
    val tinMap = Map("Targeted_Tin" -> filteredDataAfterTodayDF)

    // val tinMap = Map("Targeted_Tin" -> columnRenamedFilterDF)

    info("loadData() End time " + (System.currentTimeMillis() - startTime))
    tinMap

  }

  def extractingExcel(path: String, states: String, sheetName: String) {
    info("extracting columns")
    //Extracting sheet data from excel file
    val sheetFile = readExcelSheet(path.toString(), sheetName)
    sheetFile.printSchema()
    val sheetDF = sheetFile.select("Tax ID", "Tax ID Name", "Program", "Date").withColumn("MARKET", lit(states.toString()))
    sheetDF.show()
    //Adding each sheetDF to BufferArray for creating master Excel sheet
    datafrmeArray += sheetDF

  }

  def readExcelSheet(path: String, sheetValue: String) = {
    hiveContext.read.
      format("com.crealytics.spark.excel").
      option("location", path).
      option("sheetName", sheetValue).
      option("useHeader", "true").
      option("spark.read.simpleMode", "true").
      option("dateFormat", "dd/MM/yyyy").
      option("treatEmptyValuesAsNulls", "true").
      option("inferSchema", "false").
      option("addColorColumns", "false").
      load()
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {
    outDFs.foreach {
      finalDF =>
        //getting each Dataframe
        val tablename = finalDF._1
        val tinDf = finalDF._2
        tinDf.show()
        println("writing table:" + tablename)
        val hiveDB = config.getString("inbound-hive-db")
        val warehouseHiveDB = config.getString("warehouse-hive-db")
        val partitionColName = config.getString("tin_partition_col").toLowerCase()
        val partitionColValue = config.getString("tin_partition_val")
        val targetTinWithAuditDF = tinDf.withColumn(partitionColName, lit(" " + partitionColValue))
        targetTinWithAuditDF.write.option("delimiter", "|").mode("overwrite").partitionBy(partitionColName).insertInto(warehouseHiveDB + """.""" + tablename)
        info(tablename + " written in hdfs")
    }
    processVariance(outDFs)
  }

  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    println("entered into process")
    inDFs.head._2.printSchema()
    inDFs.head._2.show(20)
    //inDFs is the input dataframe with key and value where value is Dataframe

    val targetTinMarket_Date_DF = inDFs.head._2.groupBy($"TARGET_DATE", $"MARKET").agg(countDistinct($"TIN").alias("COUNT_TINS"), count($"*").alias("COUNT_RECS")).withColumn("last_updt_dtm", current_timestamp())
    val TinMarketDF = inDFs.head._2.groupBy($"MARKET").agg(countDistinct($"TIN").alias("COUNT_TINS"), count($"*").alias("COUNT_RECS")).withColumn("last_updt_dtm", current_timestamp())
    val TinTargetDateDF = inDFs.head._2.groupBy($"TARGET_DATE").agg(countDistinct($"TIN").alias("COUNT_TINS"), count($"*").alias("COUNT_RECS")).withColumn("last_updt_dtm", current_timestamp())

    val targeted_tins = config.getString("tg_table_targeted_tins").toLowerCase()
    val targeted_tins_target_date_market = config.getString("tg_table_targeted_tins_target_date_market").toLowerCase()
    val targeted_tins_market = config.getString("tg_table_targeted_tins_market").toLowerCase()
    val targeted_tins_target_date = config.getString("tg_table_targeted_tins_target_date").toLowerCase()
    val targetedTinDF = inDFs.head._2.agg(count("*").as("TIN"))
    val tinmapDF = Map("targeted_tins_target_date_market" -> targetTinMarket_Date_DF, "targeted_tins_market" -> TinMarketDF, "targeted_tins_target_date" -> TinTargetDateDF, "targeted_tins" -> inDFs.head._2)
    //processing varaince for above 3 tables
    // val tinMapsDF = tinmapDF + ("targeted_tins1" -> inDFs.head._2)

    tinmapDF
  }

  def processVariance(tinVar: Map[String, DataFrame]): Unit = {
    //var col_name=""
    val targeted_tins = config.getString("tg_table_targeted_tins").toLowerCase()
    val partitionColumn = config.getString("default_partition_col")
    val partitionValue = config.getString("tin_partition_val")
    val varianceTableName = config.getString("variance_table")

    tinVar.map(x => {
      if (x._1.equals("targeted_tins_target_date_market")) {
        val col_name = "COUNT_TINS_Market_Date"
        val df1 = x._2.selectExpr(s"sum(COUNT_TINS) as $col_name").withColumn("id", lit("1"))
        datafrmeVarianceArray += df1
      } else if (x._1.equals("targeted_tins_market")) {
        val col_name = "COUNT_TINS_Market"
        val df1 = x._2.selectExpr(s"sum(COUNT_TINS) as $col_name").withColumn("id", lit("1"))
        datafrmeVarianceArray += df1
      } else if (x._1.equals("targeted_tins_target_date")) {
        val col_name = "COUNT_TINS_Date"
        val df1 = x._2.selectExpr(s"sum(COUNT_TINS) as $col_name").withColumn("id", lit("1"))
        datafrmeVarianceArray += df1
      } else if (x._1.equals("targeted_tins")) {
        val col_name = "COUNT_TINS"
        val df1 = x._2.select("*").agg(count("*").as(col_name)).withColumn("id", lit("1"))
        datafrmeVarianceArray += df1
      }

    })

    val varianceDataframe = datafrmeVarianceArray.reduceLeft((a, b) => a.join(b)).first()
    val ab = datafrmeVarianceArray.reduceLeft((a, b) => a.join(b))
    ab.printSchema()
    ab.show()
    val check_threshold = (x: Double, y: Double) => { if (x > y.toDouble) "true" else "false" }
    val targetDB = config.getString("warehouse-hive-db")
    val variance_qry = config.getString("query_tin_variance").replaceAll(ConfigKey.warehouseDBPlaceHolder, targetDB).toLowerCase()
    val previousQuarterVaraince = hiveContext.sql(variance_qry)
    val c1 = varianceDataframe.get(0).toString().toDouble
    val c2 = varianceDataframe.get(2).toString().toDouble
    val c3 = varianceDataframe.get(4).toString().toDouble
    val c4 = varianceDataframe.get(6).toString().toDouble

    val VarianceDF = previousQuarterVaraince.map(f => {
      //f6 previous varaince value 
      val f6 = f.get(6).toString.toDouble
      println(f6)
      //get(4) previous operation value
      f.get(4).toString match {
        case "COUNT_TINS_Market_Date" =>

          VarianceSchemaa(targeted_tins, partitionColumn, partitionValue, "Market_Date", "COUNT_TINS_Market_Date", "count(Market_Date)", c1, ((c1 - f6) / f6) * 100, 5, check_threshold((((c1 - f6) / f6) * 100), 15.0))

        case "COUNT_TINS_Market" =>

          VarianceSchemaa(targeted_tins, partitionColumn, partitionValue, "TINS_Market", "COUNT_TINS_Market", "sum(TINS_Market)", c2, ((c2 - f6) / f6) * 100, 5, check_threshold((((c2 - f6) / f6) * 100), 15.0))

        case "COUNT_TINS_Date" =>
          VarianceSchemaa(targeted_tins, partitionColumn, partitionValue, "TINS_Date", "COUNT_TINS_Date", "sum(TINS_Date)", c3, ((c3 - f6) / f6) * 100, 5, check_threshold((((c3 - f6) / f6) * 100), 15.0))

        case "COUNT_TINS" =>
          VarianceSchemaa(targeted_tins, partitionColumn, partitionValue, "TINS", "COUNT_TINS", "sum(TINS_Date)", c4, ((c4 - f6) / f6) * 100, 5, check_threshold((((c4 - f6) / f6) * 100), 15.0))

      }
    }).toDF()

    VarianceDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(targetDB + """.""" + varianceTableName)

  }
}



