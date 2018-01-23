package com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.DroolsOnSpark

import java.io.File
import org.apache.spark.broadcast.Broadcast
import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.drools.KieSessionApplier
import org.apache.spark.sql.DataFrame
import org.kie.api.io.Resource
import org.apache.spark.sql.functions.{current_timestamp, lit}
import com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.drools._
import org.apache.spark.SparkFiles
/**
  * Created by yuntliu on 1/22/2018.
  */
class DroolsProductOperation(confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("info")

  def loadData(): Map[String, DataFrame] = {


    var newData1 = List(
      ("diamond", 0),
      ("gold", 0),
      ("pearl", 0),
      ("silver", 0),
      ("ruby", 0)
    )
    for (i<-1 to 5)
      {newData1=newData1:::newData1}



    val allDataMap = Map(
      "NewData1" -> spark.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1, x._2))).toDF("Type", "Discount")
    )
    return allDataMap

  }


  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    println("************* Before Apply Rules *************")
    inDFs.getOrElse("NewData1", null).show()
    val FilePath = config.getString("DroolsXLSRuleFile")
    val FileName = config.getString("DroolsXLSRuleFileName")
    // val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>(row.getString(row.fieldIndex("Type")),row.getInt(row.fieldIndex("Discount"))))).toDF("Type","Discount")
    // val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{rulesProcessor.applyDiscountRule(row,FilePath)})).toDF("Type", "Discount")
    /*
        val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{
         var product = new Product();
         product.setType(row.getString(row.fieldIndex("Type")));
         product.setDiscount(row.getInt(row.fieldIndex("Discount")));
         //  (new SessionApplier("\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
         //  (new KieSessionApplier("C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\InteliJOldSparkWorkSpace\\src\\main\\resources\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
         (new KieSessionApplier(FilePath)).applyDiscountRule(product)
         (product.getType, product.getDiscount)

       })).toDF("Type", "Discount")
    */

   // val outDFs = spark.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{rulesProcessorstream.applyDiscountRule(row,FilePath)})).toDF("Type", "Discount")


    /*        File drlFile = new File(drlFileName);
        Resource resource = kieResources.newFileSystemResource(drlFile);*/

    if (env.equalsIgnoreCase("local"))
      {
        sc.addFile(FilePath)
      }


    val localFile = SparkFiles.get(FileName)
    println(localFile)

    val drlFile = new File(localFile);


    import spark.implicits._
  //  val outDFs = (inDFs.getOrElse("NewData1", null)).map(row=>{rulesProcessorstream.applyDiscountRule(row,FilePath)}).toDF("Type", "Discount")
    val outDFs = (inDFs.getOrElse("NewData1", null)).map(row=>{rulesProcessorFromFile.applyDiscountRuleFromFile(row,drlFile)}).toDF("Type", "Discount")

    val row = inDFs.getOrElse("NewData1", null).first()

    val allDataMap = Map(
      "NewData1" -> outDFs
    )

    return allDataMap;
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {
    println("************* After Apply Rules *************")
    outDFs.getOrElse("NewData1", null).show()

  }

  def loadHiveTableData(): DataFrame = {
    //Reading the data into Data frames
    println("Reading the data into Data frames")
    val clmQuery = config.getString("query_clm").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
    println(clmQuery)
    println("Reading the queries from config file")
    val clmDF = spark.sql(clmQuery).repartition(200)
    clmDF.printSchema()

    clmDF
  }

  def applyDiscountNewRule(row: org.apache.spark.sql.Row, DroolsXLSRuleFile:String): (String, Int) = {
    var product = new Product();
    product.setType(row.getString(row.fieldIndex("Type")));
    product.setDiscount(row.getInt(row.fieldIndex("Discount")));
    //  (new SessionApplier("\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
    //  (new KieSessionApplier("C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\InteliJOldSparkWorkSpace\\src\\main\\resources\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
    (new KieSessionApplier(DroolsXLSRuleFile)).applyDiscountRule(product)
    return (product.getType, product.getDiscount)
  }


}
object rulesProcessor extends  Serializable {
  def applyDiscountRule(row: org.apache.spark.sql.Row, DroolsXLSRuleFile:String): (String, Int) = {
    var product = new Product();
    product.setType(row.getString(row.fieldIndex("Type")));
    product.setDiscount(row.getInt(row.fieldIndex("Discount")));
    //  (new SessionApplier("\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
    //  (new KieSessionApplier("C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\InteliJOldSparkWorkSpace\\src\\main\\resources\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
    (new KieSessionApplier(DroolsXLSRuleFile)).applyDiscountRule(product)
    return (product.getType, product.getDiscount)

  }
}
object rulesProcessorstream extends  Serializable {
  def applyDiscountRule(row: org.apache.spark.sql.Row, DroolsXLSRuleFile: String): (String, Int) = {
    var product = new Product();
    product.setType(row.getString(row.fieldIndex("Type")));
    product.setDiscount(row.getInt(row.fieldIndex("Discount")));
    //  (new SessionApplier("\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
    //  (new KieSessionApplier("C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\InteliJOldSparkWorkSpace\\src\\main\\resources\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
    (new KieSessionApplier(DroolsXLSRuleFile)).applyDiscountRule(product)
    return (product.getType, product.getDiscount)

  }
}

  object rulesProcessorFromFile extends  Serializable {
    def applyDiscountRuleFromFile(row: org.apache.spark.sql.Row, file:File): (String, Int) = {
      var product = new Product();
      product.setType(row.getString(row.fieldIndex("Type")));
      product.setDiscount(row.getInt(row.fieldIndex("Discount")));
      //  (new SessionApplier("\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
      //  (new KieSessionApplier("C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\InteliJOldSparkWorkSpace\\src\\main\\resources\\rules\\DroolsRuleDecisionTableDiscount.xls")).applyDiscountRule(product)
      (new KieSessionApplier(file)).applyDiscountRuleFromFile(product)
      return (product.getType, product.getDiscount)

    }
}
