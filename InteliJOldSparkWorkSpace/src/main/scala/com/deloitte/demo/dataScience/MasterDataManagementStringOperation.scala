package com.deloitte.demo.dataScience
import com.deloitte.demo.framework.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.desc

import scala.collection.mutable.ArrayBuffer
/**
  * Created by yuntliu on 11/8/2017.
  */
class MasterDataManagementStringOperation (override val AppName:String, override val master:String

                                          ) extends OperationSession(AppName,master, None ) with Operator {



  override def loadData():Map[String,org.apache.spark.sql.DataFrame]={


    val path = exDocReader.getString("env.inputFilePath")+docReader.getString("firstFileName")
    println("Path:" + path)

    val testDF = sqlContext
      .read.format( "com.databricks.spark.csv" )
      .option( "header", "true" )
      .option( "inferSchema", "true" )
      .load( exDocReader.getString("env.inputFilePath")+docReader.getString("firstFileName") )
    testDF.show(50)

    val testDF2 = sqlContext
      .read.format( "com.databricks.spark.csv" )
      .option( "header", "true" )
      .option( "inferSchema", "true" )
      .load( exDocReader.getString("env.inputFilePath")+docReader.getString("secondFileName") )
    testDF2.show(50)

    var dataMap = Map("FirstDataFrame"->testDF, "SecondDataFrame"->testDF2)
    return dataMap

  }
  override def writeData(outDFs:Map[String,DataFrame]):Unit={
    outDFs.getOrElse("Record Similarity Matching Scores", null).show(100)
  }

  override def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={
    //  val allDF = inDFs.getOrElse("FirstDataFrame",null).union(inDFs.getOrElse("SecondDataFrame",null))


    val col1Name: String =  "email"
    val col1WeightValue: Double =  0.4
    val col2Name:String =  "phone"
    val col2WeightValue: Double =  0.4
    val col3Name:String = "street"
    val col3WeightValue:Double = 0.2
    val rowIDcol:String = "rowID"
    var inputFieldsList:String = "source_rowID,first_name,last_name,street,city,state,zip,phone,email,source,rowID"
    var inputFieldsListArray:Array[String]=inputFieldsList.split(",")

    val combinedDF = inDFs.getOrElse("FirstDataFrame",null)
    import combinedDF.sqlContext.implicits._

    var custDataWeightedColDistances = Array[(Long, Long, Double)]()
    val custDataRowsForClusters = inDFs.getOrElse("FirstDataFrame",null).
      unionAll(inDFs.getOrElse("SecondDataFrame",null)).toDF(inputFieldsListArray: _*).map(row => ( row.getLong(row.fieldIndex(rowIDcol)).toLong, row.getString(row.fieldIndex(col1Name)), row.getString(row.fieldIndex(col2Name)), row.getString(row.fieldIndex(col3Name))))


    //Iterate through each row; compare distance for col1 and col2 for a row with all other rows; Using JaroWinklerDistance
    var i = 0
    var j = i + 1
    var custDataColDistancesBuffer = ArrayBuffer[(Long, Long, Double, Double, Double)]() //Row1ID - Row2ID - StringDistance_Col1 - StringDistance_Col2 - StringDistance_Col3
    for( i <- 0 to (custDataRowsForClusters.count().toInt - 2); j <- i+1 to (custDataRowsForClusters.count().toInt - 1)){
      var rowi = custDataRowsForClusters.collect()(i)
      var rowj = custDataRowsForClusters.collect()(j)
      custDataColDistancesBuffer += ((rowi._1, rowj._1,
        org.apache.commons.lang3.StringUtils.getJaroWinklerDistance(rowi._2.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._2.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", "")),
        org.apache.commons.lang3.StringUtils.getJaroWinklerDistance(rowi._3.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._3.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", "")),
        org.apache.commons.lang3.StringUtils.getJaroWinklerDistance(rowi._4.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._4.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""))))
    }
    val custDataColDistances = custDataColDistancesBuffer.toArray

    //Calculate weighted average - each for col1 and col2
    var custDataColDistancesBuffer2 = ArrayBuffer[(Long, Long, Double)]() //Row1ID - Row2ID - WeightedAverageDistance
    for (eachElement <- custDataColDistances) {
      custDataColDistancesBuffer2 += ((eachElement._1, eachElement._2, ((eachElement._3 *col1WeightValue) + (eachElement._4 *col2WeightValue)  + (eachElement._5 *col3WeightValue))))
      //  println(((eachElement._1, eachElement._2, ((eachElement._3 *col1WeightValue) + (eachElement._4 *col2WeightValue)  + (eachElement._5 *col3WeightValue)))))
    }

    val custDataColDistances2 = custDataColDistancesBuffer2.toArray
    val combinedOutputDF =  combinedDF.sqlContext.sparkContext.parallelize(custDataColDistances2).toDF(rowIDcol,rowIDcol,"weightedSimilarity")

    val allDataMap = Map("Record Similarity Matching Scores"->combinedOutputDF.sort(desc("weightedSimilarity")))
    return allDataMap


  }


}
