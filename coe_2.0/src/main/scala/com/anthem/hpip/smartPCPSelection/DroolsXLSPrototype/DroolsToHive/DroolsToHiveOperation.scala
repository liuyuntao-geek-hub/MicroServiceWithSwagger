package com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.DroolsToHive

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
//import org.apache.spark.sql.catalyst.expressions.MonotonicallyIncreasingID;
import org.apache.spark.sql.functions._
class DroolsToHiveOperation (confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("info")

  def loadData(): Map[String, DataFrame] = {


    var newData1 = List(
      ("diamond", 0),
      ("gold", 0),
      ("pearl", 0),
      ("silver", 0),
      ("ruby", 0)
    )
    var newData = newData1
    for (i <- 1 to 2000)
      {newData1=newData1:::newData}


    val allDataMap = Map(
      "NewData1" -> spark.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1, x._2))).toDF("Type", "Discount")
    )
    return allDataMap

  }


  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    println("************* Before Apply Rules *************")

    val FilePath = config.getString("DroolsXLSRuleFile")
    val FileName = config.getString("DroolsXLSRuleFileName")

    if (env.equalsIgnoreCase("local"))
      {
      sc.addFile(FilePath)
      }
      else
      {
      sc.addFile(FileName)
      }
     // --file /path/xxx/file => this the real file with path
    // sc.addFile() and SparkFiles.get() => only use the file name
    // when run deploymode client => need to be in the current folder with the rule file
    // when run --deploymode cluster => no need to be in current folder + need --file pointing to local
 
    val localFile = SparkFiles.get(FileName)
    println(localFile)

    //val drlFile = new File(FileName);
    var drlFile:File = null;
    if (env.equalsIgnoreCase("local"))
      {
     drlFile = new File(localFile);
      }
     else
      {
      drlFile = new File(FileName);
      }
        
    var inDf= inDFs.getOrElse("NewData1", null)
    
    for (i <- 1 to config.getInt("DroolsDFCount"))
    {
     inDf=inDf.union(inDFs.getOrElse("NewData1", null))
    }
    
    import spark.implicits._
  //  val outDFs = (inDFs.getOrElse("NewData1", null)).map(row=>{rulesProcessorstream.applyDiscountRule(row,FilePath)}).toDF("Type", "Discount")
    val outDFs = (inDf).map(row=>{rulesProcessorFromFile.applyDiscountRuleFromFile(row,drlFile)}).toDF("Type", "Discount")

    val row = inDf.first()

    
    val lastUpdatedDate = config.getString("audit-column-name").toLowerCase
    val ReadyDFs=outDFs.withColumn(lastUpdatedDate, lit(current_timestamp())).withColumn("id_key", lit(monotonically_increasing_id()) ).map(row=>(row.get(row.fieldIndex("id_key")).toString(), 
        row.getInt(row.fieldIndex("Discount")), row.getTimestamp(row.fieldIndex("last_updt_dtm")), row.getString(row.fieldIndex("Type")) 
        )
        ).toDF("id_key","discount","last_updt_dtm","type")
    

    
    val allDataMap = Map(
      "zz_product_discount" -> ReadyDFs
    )

    return allDataMap;
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {
    println("************* After Apply Rules *************")
   
  val df1 = outDFs.getOrElse("zz_product_discount", null)


    //Writing the data to a table in Hive
    println("Writing the data to a table in Hive")
    //Looping for map of data frames
    println("Looping for map of data frames")

    outDFs.foreach(x => {
      println("-------Inside For loop Only one DF --------")

      val hiveDB = config.getString("inbound-hive-db")
      val warehouseHiveDB = config.getString("warehouse-hive-db")
      println("hiveDB is " + hiveDB)
      println("warehouseHiveDB is " + warehouseHiveDB)

      val tablename = x._1
      println("tablename is" + tablename)

      //Displaying the sample of data
      var df = x._2
      printf("Showing the contents of df")
      df.printSchema()
   //   df.show(false)

      println("Truncating the previous table created")
    //  spark.sql("ALTER TABLE " + warehouseHiveDB + """.""" + tablename+" SET TBLPROPERTIES('EXTERNAL'='FALSE')")
     // spark.sql("truncate table " + warehouseHiveDB + """.""" + tablename)




        df.write.mode("overwrite").option("truncate", "true").insertInto(warehouseHiveDB + """.""" + tablename)

      println("Table created as " + tablename)
      info(s"[HPIP-ETL] Table created as $warehouseHiveDB.$tablename")
    })


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
  
  object counter extends Serializable {
    
    var myCounter=0;
    def getCounter():Int={
      myCounter=myCounter+1;
      return myCounter;
    }
  }
