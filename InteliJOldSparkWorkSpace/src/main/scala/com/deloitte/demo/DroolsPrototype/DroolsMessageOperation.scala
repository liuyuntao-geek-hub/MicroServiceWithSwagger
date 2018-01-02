package com.deloitte.demo.DroolsPrototype

import com.deloitte.demo.DroolsPrototype.drools.DroolsRulesApplier
import com.deloitte.demo.framework.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame

/**
  * Created by yuntliu on 12/7/2017.
  */
class DroolsMessageOperation(override val AppName:String, override val master:String
                               ) extends OperationSession(AppName,master,None)  with Operator with Serializable {

  override def loadData():Map[String,org.apache.spark.sql.DataFrame]={

    val newData1 = List(
      ("a", "aaaa"),
      ("b", "aaaa"),
      ("c", "aaaa"),
      ("d", "eeeeeeee"),
      ("e", "")
    )

    val allDataMap = Map (
      "NewData1"->sqlContext.createDataFrame(sc.parallelize(newData1, 5).map(x=>(x._1,x._2))).toDF("id","number")
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
    val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>(row.getString(row.fieldIndex("id")),(new DroolsRulesApplier("/rules/Rules.drl")).applyRule(row.getString(row.fieldIndex("number")))))).toDF("id","number")

    val allDataMap = Map (
      "NewData1"->outDFs
    )

    return allDataMap;
  }

}
