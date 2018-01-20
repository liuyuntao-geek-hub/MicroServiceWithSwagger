package com.anthem.hpip.spend

import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import com.anthem.hpip.helper.OperationSession
import com.anthem.hpip.helper.Operator
import com.anthem.hpip.util
import scala.reflect.internal.util.TableDef.Column
import com.anthem.hpip.config._

class SpendOperation(confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env, queryFileCategory) with Operator {

  def loadData(): Map[String, DataFrame] = {
    return Map(("df", loadHiveTableData()))
  }

  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val correctSpendMap = processCorrectSpend(inDFs.getOrElse("df", null))
    val finalResultDF = processMBRAllow(correctSpendMap.getOrElse("df3", null))
    return Map(("df", finalResultDF)) ++ correctSpendMap

  }

  def writeData(rdd: RDD[(String, Int)]): Unit = {
    rdd.collect().foreach(println)
  }

  def varianceCalculation(targetTableDFs: Map[String, DataFrame]): Unit = {

    //  val hiveDB = config.getString("hiveDB")
    val warehouseHiveDB = config.getString("warehouse-hive-db")
    val varianceTableName = config.getString("variance_table")
   
    val previous_variance_data_query = config.getString("query_spend_variance").replaceAll(ConfigKey.warehouseDBPlaceHolder, config.getString("warehouse-hive-db")).toLowerCase()
    val lastUpdatedDate = config.getString("audit-column-name")

    println(s"Variance logic started")
    println(s"Data load query is  $previous_variance_data_query")

    val prvVarDF = hiveContext.sql(previous_variance_data_query)
    prvVarDF.show()

    import hiveContext.implicits._

    val targetTableName = config.getString("spendTargetTableName")

    val partitionColumn = config.getString("default_partition_col")
    val partitionValue = config.getString("spend_partition_value")

    val vdfcurrent = targetTableDFs.getOrElse(targetTableName, null)

    val vdfcurrentAgg = vdfcurrent.agg(count(lit(1)).as("Total_Members"), sum($"ALLOWED_TOTAL").as("TOTAL_ALLOWED_AMOUNT"), sum($"PAID_TOTAL").as("TOTAL_PAID_AMOUNT")).first()

    val c1 = vdfcurrentAgg.get(0).toString().toDouble
    val c2 = vdfcurrentAgg.get(1).toString().toDouble
    val c3 = vdfcurrentAgg.get(2).toString().toDouble

    val check_threshold = (x: Double, y: Double) => { if (x > y) "true" else "false" }

    val VarianceDF = prvVarDF.map(f => {

      val f6 = f.get(6).toString.toDouble
      f.get(4).toString match {
        case "Total_Members" =>

          VarianceSchema(targetTableName, partitionColumn, partitionValue, "mcid", "Total_Members", "count(mcid)", c1, ((c1 - f6) / f6) * 100, 5, check_threshold((((c1 - f6) / f6) * 100), 5.0))

        case "TOTAL_ALLOWED_AMOUNT" =>

          VarianceSchema(targetTableName, partitionColumn, partitionValue, "ALLOWED_AMOUNT", "TOTAL_ALLOWED_AMOUNT", "sum(ALLOWED_AMOUNT)", c2, ((c2 - f6) / f6) * 100, 5, check_threshold((((c2 - f6) / f6) * 100), 5.0))

        case "TOTAL_PAID_AMOUNT" =>
          VarianceSchema(targetTableName, partitionColumn, partitionValue, "PAID_AMOUNT", "TOTAL_PAID_AMOUNT", "sum(PAID_AMOUNT)", c3, ((c3 - f6) / f6) * 100, 5, check_threshold((((c3 - f6) / f6) * 100), 5.0))

      }
    }).toDF()

    VarianceDF.withColumn(lastUpdatedDate, lit(current_timestamp())).show()

    VarianceDF.withColumn(lastUpdatedDate, lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)
    VarianceDF.show()

    println(s"Variance logic ended")

  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {
    val df = outDFs.getOrElse("df", null)
    val df0 = outDFs.getOrElse("df0", null)
    val df1 = outDFs.getOrElse("df1", null)
    val df2 = outDFs.getOrElse("df2", null)
    val df3 = outDFs.getOrElse("df3", null)
    hiveContext.setConf("hive.exec.dynamic.partition", "true")
    hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")
    val warehouseHiveDB = config.getString("warehouse-hive-db")
     val stagingHiveDB = config.getString("stage-hive-db")
    val targetTableName = config.getString("spendTargetTableName")
    val staging0TableName = config.getString("spendStaging0TableName")
    val staging1TableName = config.getString("spendStaging1TableName")
    val staging2TableName = config.getString("spendStaging2TableName")
    val staging3TableName = config.getString("spendStaging3TableName")
    val partitionColumn = config.getString("default_partition_col")
    val partitionValue = config.getString("spend_partition_value")
    val lastUpdatedDate = config.getString("audit-column-name")

    df.show(false)

    val dfwrite = df.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
    dfwrite.write.mode("overwrite").partitionBy(partitionColumn).insertInto(warehouseHiveDB + """.""" + targetTableName)

     if (config.getString("spendStagingFlag").equals("true")) {
      val df0write = df0.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
      df0write.write.mode("overwrite").partitionBy(partitionColumn).insertInto(stagingHiveDB + """.""" + staging0TableName)
    }
    
    if (config.getString("spendStagingFlag").equals("true")) {
      val df1write = df1.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
      df1write.write.mode("overwrite").partitionBy(partitionColumn).insertInto(stagingHiveDB + """.""" + staging1TableName)
    }

    if (config.getString("spendStagingFlag").equals("true")) {
      val df2write = df2.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
      df2write.write.mode("overwrite").partitionBy(partitionColumn).insertInto(stagingHiveDB + """.""" + staging2TableName)
    }

    if (config.getString("spendStagingFlag").equals("true")) {
      val df3write = df3.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn(partitionColumn, lit("" + partitionValue))
      df3write.write.mode("overwrite").partitionBy(partitionColumn).insertInto(stagingHiveDB + """.""" + staging3TableName)
    }

    varianceCalculation(Map((targetTableName, dfwrite)))
  }

  def loadHiveTableData(): DataFrame = {
    //3. Bring Spend and Store it
    val inboundHiveDB = config.getString("inbound-hive-db")
    val tableName = config.getString("spendSourceTableName")
    
    val pi_spnd_query = config.getString("spendSourceDataQuery").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()

    println(s"Hive database is $inboundHiveDB")
    println(s"Hive table name is $tableName")
    
    println(s"Data load query is  $pi_spnd_query")

    //Direct Hive table load
    // val spendRawDF = hiveContext.sql(s"select * from $hiveDB.$tableName limit 100")
    val spendRawDF = hiveContext.sql(pi_spnd_query)
    spendRawDF.printSchema()
    spendRawDF.show(false)

    //Avro file laod
    //    val rawDataDF = sqlContext.read.format("com.databricks.spark.avro").load(hdfsPathOfTable)
    //    rawDataDF.printSchema()
    //    println("Data Directory:" + hdfsPathOfTable)
    //    rawDataDF.show()

    spendRawDF
  }

  def processCorrectSpend(spendRawDF: DataFrame): Map[String, DataFrame] = {
    import hiveContext.implicits._

    val srcFilter: String = config.getString("spendSourceDataFilter")
    val startDate = srcFilter.split(",")(0)
    val endDate = srcFilter.split(",")(1)

    println(s"StartDate  and EndDate values are  $srcFilter")
    
    val spnd_d = spendRawDF.map(s => {
      Spend_D(s(0).toString, s(1).toString, s(2).toString, s(3).toString, s(4).toString, s(5).toString,
        s(6).toString, s(7).toString, s(8).toString, s(9).toString, s(10).toString
        )
    }).toDF()

    spnd_d.show(false)
    

    val spndPmtTypeAggDF = spendRawDF.groupBy($"PMNT_TYPE").agg(
      sum($"ALLOWED").alias("Sum_Allowed"),
      sum($"PAID").alias("Sum_Paid"))

    spndPmtTypeAggDF.show(false)

    val spnd = spendRawDF.map(s => {
      Spend(s(0).toString, s(1).toString, s(2).toString, s(3).toString, s(4).toString, s(5).toString,
        s(6).toString, s(7).toString, s(8).toString, s(9).toString, s(10).toString,
        util.DateUtils.getLastDayOfTheMonth(s(1).toString() + "01"))
    }).toDF()

    spnd.show(false)

    val spndGrpd = spnd.groupBy("MCID", "DRVD_DT", "PMNT_TYPE").agg(
      sum($"ALLOWED").alias("Sum_Allowed"),
      sum($"PAID").alias("Sum_Paid"))

    val payTypeSet = Seq("INPT", "OUTPT", "CAP", "PHRCY", "PROF", "TRNSPLNT")

    val carrectSpndDFs = for {
      payType <- payTypeSet
    } yield (payType + "DF", correctSpend(spndGrpd, payType))

    val unionDF = carrectSpndDFs.reduceLeft((a, b) => ((a._1), (a._2.unionAll(b._2))))._2

    unionDF.show(false)

    val df3 = unionDF.groupBy("MCID", "DRVD_DT").agg(sum("INPAT_Allowed").alias("INPAT_Allowed"), sum("INPAT_Paid").alias("INPAT_Paid"),
      sum("OUTPUT_Allowed").alias("OUTPUT_Allowed"), sum("OUTPUT_Paid").alias("OUTPUT_Paid"), sum("CPTTN_ALLOWED").alias("CPTTN_ALLOWED"),
      sum("CPTTN_PAID").alias("CPTTN_PAID"), sum("RX_ALLOWED").alias("RX_ALLOWED"), sum("RX_PAID").alias("RX_PAID"),
      sum("PROF_ALLOWED").alias("PROF_ALLOWED"), sum("PROF_PAID").alias("PROF_PAID"), sum("TRNSPLNT_ALLOWED").alias("TRNSPLNT_ALLOWED"),
      sum("TRNSPLNT_PAID").alias("TRNSPLNT_PAID"), sum("Allowed_Total").alias("Allowed_Total"), sum("Paid_Total").alias("Paid_Total"))

    val dfcombinedResult = df3.select($"MCID", $"DRVD_DT", $"INPAT_Allowed", $"INPAT_Paid", $"OUTPUT_Allowed", $"OUTPUT_Paid",
      $"CPTTN_ALLOWED", $"CPTTN_PAID", $"RX_ALLOWED", $"RX_PAID", $"PROF_ALLOWED", $"PROF_PAID", $"TRNSPLNT_ALLOWED",
      $"TRNSPLNT_PAID", ($"INPAT_Allowed" + $"OUTPUT_Allowed" + $"CPTTN_ALLOWED" + $"RX_ALLOWED" + $"PROF_ALLOWED" + $"TRNSPLNT_ALLOWED").alias("Allowed_Total"),
      ($"INPAT_Paid" + $"OUTPUT_Paid" + $"CPTTN_PAID" + $"RX_PAID" + $"PROF_PAID" + $"TRNSPLNT_PAID").alias("Paid_Total"))

    dfcombinedResult.show(false)
    Map(("df0",spnd_d),("df1", spndPmtTypeAggDF), ("df2", spnd), ("df3", dfcombinedResult))

  }
  def processMBRAllow(df: DataFrame): DataFrame = {
    import hiveContext.implicits._
    //Create Data Set of rolling 12 months of allowed dollars by MCID;

    val spndMBRAllowDF = df
      .groupBy($"MCID")
      .agg(
        sum($"INPAT_Allowed").as("ALLOWED_INPATIENT_NO_TRANSPLANT"),
        sum($"TRNSPLNT_ALLOWED").as("ALLOWED_TRANSPLANT"), sum($"OUTPUT_Allowed").as("ALLOWED_OUTPATIENT"),
        sum($"PROF_ALLOWED").as("ALLOWED_PROFESSIONAL"), sum($"RX_ALLOWED").as("ALLOWED_RX"),
        sum($"CPTTN_ALLOWED").as("ALLOWED_CAPITATED"),
        sum($"CPTTN_ALLOWED").as("ALLOWED_OTHER"), sum($"INPAT_Allowed".
          +($"TRNSPLNT_ALLOWED")).as("ALLOWED_INPATIENT"),
        sum($"INPAT_Allowed".+($"TRNSPLNT_ALLOWED").
          +($"OUTPUT_Allowed").+($"PROF_ALLOWED").
          +($"RX_ALLOWED").
          +($"CPTTN_ALLOWED")).as("ALLOWED_TOTAL"), sum($"INPAT_Paid").as("PAID_INPATIENT_NO_TRANSPLANT"),
        sum($"TRNSPLNT_PAID").as("PAID_TRANSPLANT"), sum($"OUTPUT_Paid").as("PAID_OUTPATIENT"),
        sum($"PROF_PAID").as("PAID_PROFESSIONAL"), sum($"RX_PAID").as("PAID_RX"),
        sum($"CPTTN_PAID").as("PAID_CAPITATED"),
        sum($"CPTTN_PAID").as("PAID_OTHER"), sum($"INPAT_Paid".
          +($"TRNSPLNT_PAID")).as("PAID_INPATIENT"),
        sum($"INPAT_Paid".+($"TRNSPLNT_PAID").
          +($"OUTPUT_Paid").+($"PROF_PAID").
          +($"RX_PAID").
          +($"CPTTN_PAID")).as("PAID_TOTAL"))

    val startDate = config.getString("spendSourceDataFilter").split(",")(0)
    val endDate = config.getString("spendSourceDataFilter").split(",")(1)
    print("Start date is" + startDate)
    print("end date is" + endDate)

    val spndMBR = spndMBRAllowDF.withColumn("sEFFDT12", lit("" + startDate)).withColumn(
      "sENDDT",
      lit("" + endDate)).orderBy("MCID")

    spndMBR.show(false)

    (spndMBR)

  }

  def correctSpend(df: DataFrame, pmntType: String): DataFrame = {

    import hiveContext.implicits._

    pmntType.trim() match {
      case "INPT" =>
        df.filter("PMNT_TYPE rlike 'INPT'").map(a => {
          CorrectedSpend(a.get(0).toString, a.get(1).toString, a.get(3).toString, a.get(4).toString, "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0")
        }).toDF()
      case "OUTPT" =>
        df.filter("PMNT_TYPE rlike 'OUTPT'").map(a => {
          CorrectedSpend(a.get(0).toString, a.get(1).toString, "0", "0", "0", "0", a.get(3).toString, a.get(4).toString, "0", "0", "0", "0", "0", "0", "0", "0")
        }).toDF()
      case "CAP" =>
        df.filter("PMNT_TYPE rlike 'CAP'").map(a => {
          CorrectedSpend(a.get(0).toString, a.get(1).toString, "0", "0", "0", "0", "0", "0", a.get(3).toString, a.get(4).toString, "0", "0", "0", "0", "0", "0")
        }).toDF()
      case "PHRCY" =>
        df.filter("PMNT_TYPE rlike 'PHRCY'").map(a => {
          CorrectedSpend(a.get(0).toString, a.get(1).toString, "0", "0", "0", "0", "0", "0", "0", "0", a.get(3).toString, a.get(4).toString, "0", "0", "0", "0")
        }).toDF()
      case "PROF" =>
        df.filter("PMNT_TYPE rlike 'PROF'").map(a => {
          CorrectedSpend(a.get(0).toString, a.get(1).toString, "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", a.get(3).toString, a.get(4).toString, "0", "0")
        }).toDF()
      case "TRNSPLNT" =>
        df.filter("PMNT_TYPE rlike 'TRNSPLNT'").map(a => {
          CorrectedSpend(a.get(0).toString, a.get(1).toString, "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", a.get(3).toString, a.get(4).toString)
        }).toDF()
    }

  }

}
