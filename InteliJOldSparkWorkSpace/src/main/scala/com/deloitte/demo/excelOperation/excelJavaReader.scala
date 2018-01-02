package com.deloitte.demo.excelOperation

/**
  * Created by yuntliu on 12/4/2017.
  */

import java.io.{File, FileInputStream}
import org.apache.poi.ss.usermodel.{Cell, DataFormatter}
import org.apache.poi.xssf.usermodel.{XSSFFormulaEvaluator, XSSFSheet, XSSFWorkbook}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object excelJavaReader {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("testing")
    val sc = new SparkContext(conf)
    val spark = new SQLContext(sc)
    import spark.implicits._
    val dataframeArray = ArrayBuffer[DataFrame]()
    //Sending Excel file for Extracing Columns
    val myFile = new File("C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\InteliJOldSparkWorkSpace\\data\\excel\\PADP_Project_UserStory_20170929.xlsx")
    val fis = new FileInputStream(myFile)
    val myWorkbook = new XSSFWorkbook(fis)
    //creating evaluator incase of any formula error cell
    val evaluator = myWorkbook.getCreationHelper.createFormulaEvaluator()
    //getting required sheet
    val mySheet = myWorkbook.getSheet("Master Tracking")
    //extracting first row from sheet
    val firstRow = mySheet.getRow(0)
    try {
      if (firstRow != null) {
        //extracting Last Cell number
        val lastCellNum = firstRow.getLastCellNum
        for (i <- 0 until lastCellNum) {
          val cell = firstRow.getCell(i)
          if (cell != null || cell.getCellType != Cell.CELL_TYPE_BLANK) {
            val cellVal = cell.getRichStringCellValue.getString.trim
            //passing required columns to extract from excel sheet
            if (cellVal.equals("Work Item")) {
              val index = cell.getColumnIndex
              val row = cellIterator(cell, mySheet, index, evaluator)
              convertToDF(row, cellVal)
            } else if (cellVal.equals("Due Date")) {
              val index = cell.getColumnIndex
              val row = cellIterator(cell, mySheet, index, evaluator)
              convertToDF(row, cellVal)
            }
            else if (cellVal.equals("Owner")) {
              val index = cell.getColumnIndex
              val row = cellIterator(cell, mySheet, index, evaluator)
              convertToDF(row, cellVal)
            }
            else if (cellVal.equals("UserStory")) {
              val index = cell.getColumnIndex
              val row = cellIterator(cell, mySheet, index, evaluator)
              convertToDF(row, cellVal)
            }
          }
        }
      }

    } catch {
      case _ => println("DIscard this message because of other cell checking")
    }

    def cellIterator(cel: Cell, activeSheet: XSSFSheet, idx: Int, evaluator: XSSFFormulaEvaluator): ArrayBuffer[Row] = {

      val rowVal = ArrayBuffer[Row]()
      val formatter = new DataFormatter()
      val rowLastNum = activeSheet.getLastRowNum
      for (i <- 1 to rowLastNum) {
        val activeRow = activeSheet.getRow(i)
        val cell = activeRow.getCell(idx)
        val celValue = cell.getCellType match {
          case Cell.CELL_TYPE_NUMERIC => val sd = evaluator.evaluate(cell)
            sd.getNumberValue.toString
          case Cell.CELL_TYPE_STRING => val sd = evaluator.evaluate(cell)
            sd.getStringValue
          case _ => None
        }
        rowVal.append(Row(celValue))
      }
      rowVal
    }

    def convertToDF(data: ArrayBuffer[Row], cellValue: String): Unit = {
      val rdd = sc.parallelize(data)
      val windowFunc = Window.orderBy(cellValue)
      val df = rdd.map({ case Row(val1: String) => (val1) }).toDF(cellValue).withColumn("id", row_number().over(windowFunc))
      dataframeArray += df
    }

    val finalResult = dataframeArray.reduce((a, b) => a.join(b, Seq("id"), joinType = "left_outer")).drop("id")
    val finalResultColumnRenamed = finalResult.withColumnRenamed("Work Item", "TIN").withColumnRenamed("Due Date", "TARGET_GROUP").
      withColumnRenamed("Owner", "TARGET_DATE").withColumnRenamed("UserStory", "TARGET_GROUP_OLD").withColumn("MARKET", lit("CA"))
    println(finalResult.count())
    finalResultColumnRenamed.show()
  }
}