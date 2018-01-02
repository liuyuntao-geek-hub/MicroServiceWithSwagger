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

object excelScalaReader {
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

    val firstRow = mySheet.getRow(0)

    try {
      if (firstRow != null) {
        //extracting Last Cell number
        val lastCellNum = firstRow.getLastCellNum
        println("last cell number is: "+lastCellNum)

      }

    }
  catch {
    case _ => println("DIscard this message because of other cell checking")
  }

  }
}
