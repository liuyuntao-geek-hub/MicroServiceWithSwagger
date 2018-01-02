package com.deloitte.demo.DroolsPrototype

import com.deloitte.demo.DroolsPrototype.drools.DroolsRulesApplier
import com.deloitte.demo.framework.{OperationSession, Operator}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import com.deloitte.demo.DroolsPrototype.drools.Product
/**
  * Created by yuntliu on 12/7/2017.
  */
class DroolsProductOperation(override val AppName:String, override val master:String
                            ) extends OperationSession(AppName,master,None)  with Operator  {

  override def loadData():Map[String,org.apache.spark.sql.DataFrame]={

    val newData1 = List(
      ("diamond", 0),
      ("gold", 0),
      ("pearl", 0),
      ("silver", 0),
      ("ruby", 0)
    )

    val allDataMap = Map (
      "NewData1"->sqlContext.createDataFrame(sc.parallelize(newData1, 5).map(x=>(x._1,x._2))).toDF("Type","Discount")
    )
    return allDataMap
  }
  override def writeData(outDFs:Map[String,DataFrame]):Unit={
    println("************* After Apply Rules *************")
    outDFs.getOrElse("NewData1", null).show()
  }

  override def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={
    println("************* Before Apply Rules *************")
    inDFs.getOrElse("NewData1", null).show()
   // val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>(row.getString(row.fieldIndex("Type")),row.getInt(row.fieldIndex("Discount"))))).toDF("Type","Discount")
    val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(rulesProcessor.applyDiscountRule)).toDF("Type","Discount")

    val row = inDFs.getOrElse("NewData1", null).first()
    val allDataMap = Map (
      "NewData1"->outDFs
    )

    return allDataMap;
  }



}
object rulesProcessor extends Serializable {
  def applyDiscountRule(row: org.apache.spark.sql.Row): (String, Int) = {
    var product = new Product();
    product.setType(row.getString(row.fieldIndex("Type")));
    product.setDiscount(row.getInt(row.fieldIndex("Discount")));
    (new DroolsRulesApplier("/rules/DiscountRule.drl")).applyDiscountRule(product)

    return (product.getType, product.getDiscount)

  }
}