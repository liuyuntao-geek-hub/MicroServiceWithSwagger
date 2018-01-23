package com.anthem.hpip.targettin

import java.text.SimpleDateFormat

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

import org.apache.hadoop.fs.Path
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellValue
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.functions.countDistinct
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.input_file_name
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.round
import org.apache.spark.sql.functions.row_number
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions.unix_timestamp
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.config.Spark2Config
import com.anthem.hpip.helper.OperationSession
import com.anthem.hpip.helper.Operator
import java.sql.Timestamp
import com.anthem.hpip.util.DateUtils
import org.apache.spark.storage.StorageLevel

case class hpipAudit(program: String, user_id: String, app_id: String, start_time: String, app_duration: String, status: String)

class TargetTinOperation(confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env, queryFileCategory) with Operator {


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

    val startTime = DateTime.now
    info(s"[HPIP-ETL] loading Data Started: $startTime")

    //Extracting the excel file names from the specified path
    info("Extracting Excel file names from path")
    val tinAllFilesDF = spark.read.text(config.getString("tin_files_path")).withColumn("filename", input_file_name)
    val tinFilesNamesDF = tinAllFilesDF.select("filename")
    //  tinFilesNamesDF.show()
    info("file names extraction completed")

    //creating the regex pattern to filter files
    tinFilesNamesDF.cache().collect().distinct.foreach { filePath =>
      info(filePath)
      val extractedFilePath = filePath.get(0).toString()
      println("extracted path" + filePath + " " + extractedFilePath)

      //val tinFilesPath = filePath.toString().replaceAll("[\\[\\]]", "")

      //Patter for extracting state values from filename
      val stateExtractPattern = "-(.*)?-".r
      val stateValues = stateExtractPattern.findAllMatchIn(extractedFilePath)
      var states = ""
      //replacing '-' character enclosed by state example -> - CA -
      stateValues.foreach { state => states = state.toString().replaceAll("- ", "").replaceAll(" -", "").replaceAll("-", "") }
      val pc2Sheet = "PC_2"
      val providerDTLSheet = "Provider Dtl"
      //extracting sheet name from excel file if CA it will extract PC_2 if it is other excel it will extract Provider_Dtl

      if (states.toString().trim().equals("CA")) {
        readRequiredExcelSheet(extractedFilePath, pc2Sheet, states)
      } else {
        readRequiredExcelSheet(extractedFilePath, providerDTLSheet, states)
      }

    }

    info("Merging all the sheet contents")
    val allStatesDF = datafrmeArray.reduceLeft((a, b) => a.union(b))
    allStatesDF.show
    val allStatesFilterDF = allStatesDF.filter("length(TIN)>1")
    allStatesFilterDF.show
    val currentDate = com.anthem.hpip.util.DateUtils.getCurrentDate()
    val dataAfterTodayDF = allStatesFilterDF.filter(allStatesFilterDF("TARGET_DATE").gt(lit(currentDate)))

    info("filtered date based on today date")
    val tinMap = Map("Targeted_Tin" -> dataAfterTodayDF)

    info(s"[HPIP-ETL] load() Data Completed at: " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for load() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    tinMap

  }

  def readRequiredExcelSheet(filePath: String, sheetname: String, state: String): ArrayBuffer[DataFrame] = {

    val InputStream = hdfs.open(new Path(filePath))
    val myWorkbook = new XSSFWorkbook(InputStream)
    var rowValues = ListBuffer[TinRecord]()
    //creating evaluator incase of any formula error cell
    val evaluator = myWorkbook.getCreationHelper.createFormulaEvaluator()
    //getting required sheet
    val formatter = new DataFormatter()
    val mySheet = myWorkbook.getSheet(sheetname)

    //extracting first row from sheet
    try {
      //extracting Last Cell number
      val lastRowNum = mySheet.getLastRowNum
      var a, b, c, d = ""
      for (j <- 0 to lastRowNum) {
        if (j == 0) {
          //getting first row from excel sheet
          val cells = mySheet.getRow(j).cellIterator()
          //getting each cell from row
          while (cells.hasNext) {
            val cell = cells.next()
            if (cell != null || cell.getCellType != Cell.CELL_TYPE_BLANK) {
              //formatting cells of first row for checking any error value
              val cellVal = formatter.formatCellValue(evaluator.evaluateInCell(cell))
              if (cellVal.equals("Tax ID")) {
                val index = cell.getColumnIndex
                a = formatter.formatCellValue(evaluator.evaluateInCell(mySheet.getRow(0).getCell(index)))
                println(a)
                columnMapIndexValue += (a -> index)
              } else if (cellVal.equals("Tax ID Name")) {
                val index = cell.getColumnIndex
                b = formatter.formatCellValue(evaluator.evaluateInCell(mySheet.getRow(0).getCell(index)))
                columnMapIndexValue += (b -> index)
              } else if (cellVal.equals("Program Effective Date")) {
                val index = cell.getColumnIndex
                c = formatter.formatCellValue(evaluator.evaluateInCell(mySheet.getRow(0).getCell(index)))
                columnMapIndexValue += (c -> index)
              } else if (cellVal.equals("Program")) {
                val index = cell.getColumnIndex
                d = formatter.formatCellValue(evaluator.evaluateInCell(mySheet.getRow(0).getCell(index)))
                columnMapIndexValue += (d -> index)
              }

            }
          }
        } else if (j > 0) {
          var tax_id, tax_id_name, programme_effective_date, program = ""
          val c1 = evaluator.evaluate(mySheet.getRow(j).getCell(columnMapIndexValue.get("Tax ID").get))
          if (c1 == null) {
            tax_id = ""
          } else {
            tax_id = checkCell(c1, formatter, evaluator, mySheet, j, "Tax ID")
          }
          val c2 = evaluator.evaluate(mySheet.getRow(j).getCell(columnMapIndexValue.get("Tax ID Name").get))
          if (c2 == null) {
            tax_id_name = ""
          } else {
            tax_id_name = checkCell(c2, formatter, evaluator, mySheet, j, "Tax ID Name")
          }
          val c3 = evaluator.evaluate(mySheet.getRow(j).getCell(columnMapIndexValue.get("Program Effective Date").get))
          if (c3 == null) {
            programme_effective_date = ""
          } else {
            programme_effective_date = c3.getCellType match {
              case Cell.CELL_TYPE_NUMERIC =>
                dateFormatter(mySheet.getRow(j).getCell(columnMapIndexValue.get("Program Effective Date").get).getNumericCellValue.toString())

              case Cell.CELL_TYPE_STRING =>
                dateFormatter(mySheet.getRow(j).getCell(columnMapIndexValue.get("Program Effective Date").get).getStringCellValue)
              case _ => ""
            }
          }
          val c4 = evaluator.evaluate(mySheet.getRow(j).getCell(columnMapIndexValue.get("Program").get))
          if (c4 == null) {
            program = ""
          } else {
            program = checkCell(c4, formatter, evaluator, mySheet, j, "Program")
          }
          val tinRec = TinRecord(tax_id, tax_id_name, programme_effective_date, program)
          rowValues += tinRec
          tax_id = ""
          tax_id_name = ""
          programme_effective_date = ""
          program = ""
        }
      }
    } catch {
      case t: Throwable => " "
    }
    val upperc = (x: String) => { if (x != null) x.toUpperCase().toString() else "NAN" }
    val upperCase = udf(upperc)
    val excelTableDF = rowValues.toDF()
    val excelAddedColumnsDF = excelTableDF.withColumn("MARKET", lit(state.toString())).withColumn("TARGETED_GROUP", upperCase($"TARGETED_GROUP_OLD"))
    val excelDateFormatDF = excelAddedColumnsDF.select($"TIN", $"TARGETED_GROUP_OLD", unix_timestamp($"TARGET_DATE", "yyyy-MM-dd").cast("timestamp").alias("TARGET_DATE"), $"Program", $"MARKET", $"TARGETED_GROUP")
    excelDateFormatDF.printSchema()
    excelDateFormatDF.show

    datafrmeArray += excelDateFormatDF
  }
  //checking cell types
  info("checking cell types")
  def checkCell(cellVal: CellValue, formatter: DataFormatter, evaluator: XSSFFormulaEvaluator, mySheet: XSSFSheet, j: Int, columnName: String): String = {
    val value = cellVal.getCellType match {
      case Cell.CELL_TYPE_NUMERIC =>
        formatter.formatCellValue(evaluator.evaluateInCell(mySheet.getRow(j).getCell(columnMapIndexValue.get(columnName).get)))
      case Cell.CELL_TYPE_STRING =>
        formatter.formatCellValue(evaluator.evaluateInCell(mySheet.getRow(j).getCell(columnMapIndexValue.get(columnName).get)))
      case _ => ""
    }
    value
  }

  //converting double to Date Value
  info("converting Double values to Date Format")
  def dateFormatter(date: String): String = {
    try {
      val dateDouble = Some(date.toDouble).get
      val dateFormat = DateUtil.getJavaDate(dateDouble)
      val dateValue = new SimpleDateFormat("yyyy-MM-dd").format(dateFormat)
      dateValue

    } catch { case t: Throwable => " " }
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val startTime = DateTime.now
    info(s"[HPIP-ETL] writing Data Started: $startTime")

    outDFs.foreach {
      finalDF =>
        //getting each Dataframe
        val tablename = finalDF._1
        val tinDf = finalDF._2
        info("writing table:" + tablename)
        val hiveDB = config.getString("inbound-hive-db")
        info("Hive DB: " + hiveDB)

        val warehouseHiveDB = config.getString("warehouse-hive-db")
        info("WareHouse DB: " + warehouseHiveDB)
        val partitionColName = config.getString("tin_partition_col").toLowerCase()
        info("partition column name: " + partitionColName)
        val partitionColValue = config.getString("tin_partition_val")
        info("partition column Value: " + partitionColValue)

        //        if (tablename == "targeted_tins") {
        //          processDataDuplicates(tinDf, partitionColName, warehouseHiveDB, partitionColValue, tablename)
        //        } else {
        val targetTinWithAuditColumnDF = tinDf.withColumn(partitionColName, lit(partitionColValue))
        targetTinWithAuditColumnDF.write.option("delimiter", "|").mode("overwrite").insertInto(warehouseHiveDB + """.""" + tablename)
//        targetTinWithAuditColumnDF.write.option("delimiter", "|").mode("overwrite").partitionBy(partitionColName).insertInto(warehouseHiveDB + """.""" + tablename)
        info(tablename + " written in hdfs")
     //   }
        
    }
    info(s"[HPIP-ETL] writing() Data Completed at: " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for writing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    processVariance(outDFs.getOrElse("targeted_tins", null))
  }

  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val startTime = DateTime.now
    info("started processing data: " + startTime)

    //inDFs is the input dataframe with key and value where value is Dataframe

    val targetTinMarket_Date_DF = inDFs.head._2.groupBy($"TARGET_DATE", $"MARKET").agg(countDistinct($"TIN").alias("COUNT_TINS"), count($"*").alias("COUNT_RECS")).withColumn("last_updt_dtm", current_timestamp())
    targetTinMarket_Date_DF.show
    val TinMarketDF = inDFs.head._2.groupBy($"MARKET").agg(countDistinct($"TIN").alias("COUNT_TINS"), count($"*").alias("COUNT_RECS")).withColumn("last_updt_dtm", current_timestamp())
    TinMarketDF.show
    val TinTargetDateDF = inDFs.head._2.groupBy($"TARGET_DATE").agg(countDistinct($"TIN").alias("COUNT_TINS"), count($"*").alias("COUNT_RECS")).withColumn("last_updt_dtm", current_timestamp())
    TinTargetDateDF.show
  
    val targeted_tins = config.getString("tg_table_targeted_tins").toLowerCase()
    val targeted_tins_target_date_market = config.getString("tg_table_targeted_tins_target_date_market").toLowerCase()
    val targeted_tins_market = config.getString("tg_table_targeted_tins_market").toLowerCase()
    val targeted_tins_target_date = config.getString("tg_table_targeted_tins_target_date").toLowerCase()
    
    //Duplicate TIN removal
    val windows = Window.partitionBy("tin").orderBy("target_date", "targeted_group", "market")
    val uniqueTins = inDFs.head._2.select("*").withColumn("row_number", row_number().over(windows)).filter($"row_number" === 1)
    val targetTinWithOutDuplicatesDF = uniqueTins.drop("row_number").withColumn("last_updt_dtm", lit(current_timestamp()))

    targetTinWithOutDuplicatesDF.persist(StorageLevel.MEMORY_ONLY)
    info(targetTinWithOutDuplicatesDF.count())
    
    val tinmapDF = Map("targeted_tins_target_date_market" -> targetTinMarket_Date_DF, "targeted_tins_market" -> TinMarketDF, "targeted_tins_target_date" -> TinTargetDateDF, "targeted_tins" -> targetTinWithOutDuplicatesDF)

    info(s"[HPIP-ETL] processing() Data Completed at: " + DateTime.now())
    info(s"[HPIP-ETL] Time Taken for processing() Data :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    tinmapDF
  }

  def processVariance(tinDF: DataFrame): Unit = {

    info(s"[HPIP-ETL] Variance Logic started ")
    val startTime = DateTime.now()
    info(s"[HPIP-ETL] started varaince logic at : $startTime")

    val targeted_tins = config.getString("tg_table_targeted_tins").toLowerCase()
    val partitionColumn = config.getString("tin_partition_col")
    val partitionValue = config.getString("tin_partition_val")
    val varianceTableName = config.getString("variance_table")
    val targetTinThrsHld = config.getString("threshold_target_tin").toDouble
    val targetTinThrshldDefault = config.getString("threshold_target_tin_default").toDouble

    val presentQtrDF = tinDF.select("*").agg(count("*").as("COUNT_TINS")).first()
    val presentQtrCnt = presentQtrDF.get(0).toString().toDouble

    val subjectArea = config.getString("sas_program_name_TargettedTINs")
    info(s"[HPIP-ETL] subject Area is : $subjectArea")
    val check_threshold = (x: Double, y: Double) => { if (x > y || x < -y) "true" else "false" }

    info("loading previous varaince data table")
    val targetDB = config.getString("warehouse-hive-db")
    val variance_qry = config.getString("query_tin_variance").replaceAll(ConfigKey.warehouseDBPlaceHolder, targetDB).toLowerCase()
    info("previous varaince query: " + variance_qry)

    val previousQuarterDF = spark.sql(variance_qry)

    if (previousQuarterDF.head(1).isEmpty) {

      //Appending the values to the variance data frame for the first time insertion into variance table
      info(s"Appending the values to the variance data frame for the first time insertion into variance table")
      val toBeInserted = Seq((targeted_tins, partitionColumn, partitionValue, "TINS", "COUNT_TINS", "count(TINS)", presentQtrCnt, targetTinThrshldDefault, targetTinThrsHld, "true", subjectArea))
      val varianceDF = toBeInserted.map(a => { (VarianceSchemaa(a._1, a._2, a._3, a._4, a._5, a._6, a._7, a._8, a._9, a._10, a._11)) }).toDF
      //Inserting into the variance table
      info(s"[HPIP-ETL] Inserting the values to the variance data frame for the first time")
      val vDF = varianceDF.select(($"tableName"), ($"partitionColumn"), ($"partitionValue"), ($"columnName"), ($"operationDescription"), ($"operation"), round(($"operationValue"), 2), round(($"percentageVariance"), 2), ($"threshold"), ($"isThresholdCrossed"), ($"subjectArea"))
      vDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(targetDB + """.""" + varianceTableName)
      //showing the contents of variance data frame
      info(s"[HPIP-ETL] Showing the contents of Data frame for variance")

    } else {

      val previousQtrCnt = previousQuarterDF.first().get(6).toString().toDouble
      info("previous qtr count: " + previousQtrCnt)
      println("previousQtrCnt" + previousQtrCnt)

      val varienceList = List(VarianceSchemaa(targeted_tins, partitionColumn, partitionValue, "TINS", "COUNT_TINS", "count(TINS)",
        presentQtrCnt, (((presentQtrCnt - previousQtrCnt) / previousQtrCnt) * 100),
        targetTinThrsHld, check_threshold((((presentQtrCnt - previousQtrCnt) / previousQtrCnt) * 100), targetTinThrsHld), subjectArea))

      val varianceDF = varienceList.toDF()

      println("writing varaince data into hive table" + targetDB + ":" + varianceTableName)
      info("writing updated varaince data into" + targetDB + """.""" + varianceTableName)
      val vDF = varianceDF.select(($"tableName"), ($"partitionColumn"), ($"partitionValue"), ($"columnName"), ($"operationDescription"), ($"operation"), round(($"operationValue"), 2), round(($"percentageVariance"), 2), ($"threshold"), ($"isThresholdCrossed"), ($"subjectArea"))
      vDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(targetDB + """.""" + varianceTableName)
      println(s"Variance logic ended")
      info(s"[HPIP-ETL] variance() logic Comshpleted at :" + DateTime.now())
      info(s"[HPIP-ETL] Time Taken varaince() logic :" + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")

    }
  }

 /* def processDataDuplicates(tinDF: DataFrame, partitionColName: String, warehouseHiveDB: String, partitionColValue: String, tablename: String) {

    val windows = Window.partitionBy("tin").orderBy("target_date", "targeted_group", "market")
    val uniqueTins = tinDF.select("*").withColumn("row_number", row_number().over(windows)).filter($"row_number" === 1)

    val targetTinWithAuditDF = uniqueTins.drop("row_number").withColumn("last_updt_dtm", lit(current_timestamp())).withColumn(partitionColName, lit(partitionColValue))
    targetTinWithAuditDF.write.option("delimiter", "|").mode("overwrite").partitionBy(partitionColName).insertInto(warehouseHiveDB + """.""" + tablename)
    info(tablename + " Distinct tins written in hdfs")
  }*/
  
}

