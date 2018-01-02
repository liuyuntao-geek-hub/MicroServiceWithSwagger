package com.deloitte.demo.programTemplate

import com.deloitte.demo.framework.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame

/**
  * Created by yuntliu on 11/8/2017.
  */
class templateOperation (override val AppName:String, override val master:String
                        ) extends OperationSession(AppName,master,None)  with Operator {

  override def loadData():Map[String,org.apache.spark.sql.DataFrame]={

    val newData1 = List(
      ("a", 1),
      ("b", 2),
      ("c", 3),
      ("d", 4),
      ("e", 5)
    )

    val allDataMap = Map (
      "NewData1"->sqlContext.createDataFrame(sc.parallelize(newData1, 5).map(x=>(x._1,x._2))).toDF("id","number")
    )
    return allDataMap
  }
  override def writeData(outDFs:Map[String,DataFrame]):Unit={
    outDFs.getOrElse("NewData1", null).show()
  }

  override def processData(inDFs:Map[String,DataFrame]):Map[String,DataFrame]={

    return inDFs;
  }

}
