package com.deloitte.demo.excelOperation

import com.deloitte.demo.framework.{OperationSession, Operator, StringKey}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{input_file_name, lit}

import scala.collection.+:

/**
  * Created by yuntliu on 12/3/2017.
  */
class readExcelToHiveOperation (override val AppName:String, override val master:String
                      ) extends OperationSession(AppName,master,None)  with Operator {

  val dataSetString1 = "Filenames"
  val COMay = "Monthly IFT - CO - May_2016.xlsm"
  val CAMay="Monthly IFT - CA - May_2016.xlsm"
  val CAMar="Monthly IFT - CA - Mar_2017.xlsm"

  override def loadData():Map[String,org.apache.spark.sql.DataFrame]={

    var allDataMap: Map[String, org.apache.spark.sql.DataFrame] = null
    var tinAllFilesDF:DataFrame = null

    if (exDocReader.getString("env.name").equalsIgnoreCase(StringKey.SnetServer)) {
      tinAllFilesDF = hiveSqlContext.read.text(exDocReader.getString("excel.excelFilePath")).withColumn("filename", input_file_name)
    }
    else {
      //// Read Folder to get all the file names
      tinAllFilesDF = sqlContext.read.text(exDocReader.getString("excel.excelFilePath")).withColumn("filename", input_file_name)
    }


      val tinFilesNamesDF = tinAllFilesDF.select("filename").distinct()
      tinFilesNamesDF.show()

    allDataMap = Map (
      dataSetString1->tinFilesNamesDF
    )

      val fileNamePattern = "Monthly.*xlsm?".r
      tinFilesNamesDF.collect().distinct.foreach { filePath =>
        val tinFilePath = fileNamePattern.findAllMatchIn(filePath.toString())
        println("=========== Process Each xlsm file =================")
        tinFilePath.foreach(excelfilename=>{
          if (excelfilename.toString().equalsIgnoreCase(COMay))
            {

              extractingExcel(excelfilename.toString(), "CO", "Provider Dtl").show()
              allDataMap =allDataMap.+( COMay -> (extractingExcel(excelfilename.toString(), "CO", "Provider Dtl")))
            }
          else if (excelfilename.toString().equalsIgnoreCase(CAMay))
            {
              extractingExcel(excelfilename.toString(), "CA", "PC_2").show()
              allDataMap=allDataMap.+(CAMay->extractingExcel(excelfilename.toString(), "CA", "PC_2"))
            }
          else if (excelfilename.toString().equalsIgnoreCase(CAMar))
          {
            extractingExcel(excelfilename.toString(), "CA", "PC_2").show()
            allDataMap=allDataMap.+(CAMar->extractingExcel(excelfilename.toString(), "CA", "PC_2"))
          }
          else {
            println("File name does not match any predefined value")
          }
        })

    }


    return allDataMap
  }
  override def writeData(outDFs:Map[String,DataFrame]):Unit={
    outDFs.getOrElse(dataSetString1, null).show()

    outDFs.head._2.coalesce(1).write.mode("overwrite").format( "com.databricks.spark.csv" )
      .option( "header", "true" ).save(exDocReader.getString("env.SaveFilePath") + exDocReader.getString("excel.SaveFileName"))


    outDFs.getOrElse(COMay,null).coalesce(1).write.mode("overwrite").format( "com.databricks.spark.csv" )
      .option( "header", "true" ).save(exDocReader.getString("env.SaveFilePath") + "COMay.csv")

    outDFs.getOrElse(CAMay,null).coalesce(1).write.mode("overwrite").format( "com.databricks.spark.csv" )
      .option( "header", "true" ).save(exDocReader.getString("env.SaveFilePath") + "CAMay.csv")

    outDFs.getOrElse(CAMar,null).coalesce(1).write.mode("overwrite").format( "com.databricks.spark.csv" )
      .option( "header", "true" ).save(exDocReader.getString("env.SaveFilePath") + "CAMar.csv")

  }

  override def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={

    return inDFs;
  }


  def extractingExcel(path: String, states: String, sheetName: String):DataFrame= {
    println("extracting columns")
    //Extracting sheet data from excel file
    val sheetFile = readExcelSheet(path.toString(), sheetName)
    sheetFile.printSchema()
    val sheetDF = sheetFile.select("Tax ID", "Tax ID Name", "Program", "Date").withColumn("MARKET", lit(states.toString()))
    sheetDF.show()
    //Adding each sheetDF to BufferArray for creating master Excel sheet
    return sheetDF;
  }

  def readExcelSheet(path: String, sheetValue: String) = {




    if (exDocReader.getString("env.name").equalsIgnoreCase(StringKey.SnetServer)) {


      hiveSqlContext.read.
        format("com.crealytics.spark.excel").
        option("location", exDocReader.getString("excel.excelFileLocalPath") + path).
        option("sheetName", sheetValue).
        option("useHeader", "true").
        option("spark.read.simpleMode", "true").
        option("dateFormat", "dd/MM/yyyy").
        option("treatEmptyValuesAsNulls", "true").
        option("inferSchema", "false").
        option("addColorColumns", "false").
        load()
    }
    else
      {
        sqlContext.read.
          format("com.crealytics.spark.excel").
          option("location", exDocReader.getString("excel.excelFilePath") + path).
          option("sheetName", sheetValue).
          option("useHeader", "true").
          option("spark.read.simpleMode", "true").
          option("dateFormat", "dd/MM/yyyy").
          option("treatEmptyValuesAsNulls", "true").
          option("inferSchema", "false").
          option("addColorColumns", "false").
          load()
      }



  }

}
