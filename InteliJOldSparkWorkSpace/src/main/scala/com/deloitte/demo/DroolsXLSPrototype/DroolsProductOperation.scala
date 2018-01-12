package com.deloitte.demo.DroolsXLSPrototype

import java.io.File

import com.deloitte.demo.framework.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import com.deloitte.demo.DroolsXLSPrototype.drools._
import com.typesafe.config.{Config, ConfigFactory}
/**
  * Created by yuntliu on 1/6/2018.
  */
class DroolsProductOperation(override val AppName:String, override val master:String
                            ) extends OperationSession(AppName,master,None)  with Operator {

  override def loadData(): Map[String, org.apache.spark.sql.DataFrame] = {

    val newData1 = List(
      ("diamond", 0),
      ("gold", 0),
      ("pearl", 0),
      ("silver", 0),
      ("ruby", 0)
    )

    val allDataMap = Map(
      "NewData1" -> sqlContext.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1, x._2))).toDF("Type", "Discount")
    )
    return allDataMap
  }

  override def writeData(outDFs: Map[String, DataFrame]): Unit = {
    println("************* After Apply Rules *************")
    outDFs.getOrElse("NewData1", null).show()
  }

  override def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    println("************* Before Apply Rules *************")
    inDFs.getOrElse("NewData1", null).show()
    val FilePath = exDocReader.getString("Drools.DroolsXLSRuleFile")
    // val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>(row.getString(row.fieldIndex("Type")),row.getInt(row.fieldIndex("Discount"))))).toDF("Type","Discount")
    val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{rulesProcessor.applyDiscountRule(row,FilePath)})).toDF("Type", "Discount")



    val row = inDFs.getOrElse("NewData1", null).first()
    val allDataMap = Map(
      "NewData1" -> outDFs
    )

    return allDataMap;
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

