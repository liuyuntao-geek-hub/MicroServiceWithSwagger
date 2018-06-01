package com.anthem.hpip.SysPerformance.createTestingTable

import java.io.File
import java.util.UUID
import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{current_timestamp, lit}
import java.lang.Math._
import org.apache.spark.storage.StorageLevel



class createTestingTableOperation (confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("info")
  val randomTB=config.getString("randomTB")
  val srcTableA=config.getString("srcTableA")
  val srcTableB=config.getString("srcTableB")
  val srcTableACount = config.getLong("srcTableACount")
  val srcTableBCount = config.getLong("srcTableBCount")

  def loadData(): Map[String, DataFrame] = {


/// Create randomTB Dataframe
    var uuid: String = null;
    var newData1 = List((0L,""));
     val uuidCount = config.getLong("uuidCount")
 /////////////////////////
 //// Run on local & Remote on server = both works
   for (i<-1L to uuidCount)
    {
      uuid = UUID.randomUUID().toString()
      newData1 = newData1:::List((i,uuid))
      
    }
   newData1=newData1.drop(1)
   val newDataDF =  spark.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1,x._2)) ).toDF("id_key", "random_string")
   newDataDF.persist(StorageLevel.MEMORY_AND_DISK)
 //  newDataDF.show()
   
 ////////////////////
 ///// Enable the following if want to use the random util table created before 
 //  val newDataDF = loadHiveTableData().toDF("id_key", "random_string")
 /////////////////////////
    
     
     
    import spark.implicits._
     
     var Random = 0L;
     var RandomIndex = 0L;
     var uuidString =""
     var newDataA = List((0L,0L,""));
     
    for (i<-1L to srcTableACount)
    {

      Random = (Math.random()*(uuidCount-2)+1).toLong
      RandomIndex = (Math.random()*(uuidCount-2)+1).toLong
     // println ("Random is: "+ Random + " Index is: " + RandomIndex )     
     // ************ when using DF function, it will be slow 
      uuidString = newDataDF.filter($"id_key"===RandomIndex).first().getString(1)   
      // **************** When using list directly 
     // uuidString = (newData1.find(x=>{x._1==RandomIndex}).getOrElse((0L,"No_Value"))._2)
     // println ("Random is: "+ Random + " Index is: " + RandomIndex +    " uuid string is: "+ uuidString)
      newDataA = newDataA:::List((i,Random,uuidString))
    }
    newDataA=newDataA.drop(1)
   val newDataADF =  spark.createDataFrame(sc.parallelize(newDataA, 5).map(x => (x._1,x._2, x._3)) ).toDF("id_key", "random_number", "random_string")
//   newDataADF.show()    
    
    

     Random = 0L;
     RandomIndex = 0L;
     uuidString =""
     var newDataB = List((0L,0L,""));
     import spark.implicits._
    for (i<-1L to srcTableBCount)
    {

      Random = (Math.random()*(uuidCount-2)+1).toLong
      RandomIndex = (Math.random()*(uuidCount-2)+1).toLong
     // println ("Random is: "+ Random + " Index is: " + RandomIndex )     
      // **************** when using DF function, it will be actually faster 
      uuidString = newDataDF.filter($"id_key"===RandomIndex).first().getString(1)
      // **************** When using list directly 
      // uuidString = (newData1.find(x=>{x._1==RandomIndex}).getOrElse((0L,"No_Value"))._2)
     // println ("Random is: "+ Random + " Index is: " + RandomIndex +    " uuid string is: "+ uuidString)
      newDataB = newDataB:::List((i,Random,uuidString))
    }
     newDataB=newDataB.drop(1)
   val newDataBDF =  spark.createDataFrame(sc.parallelize(newDataB, 5).map(x => (x._1,x._2,x._3)) ).toDF("id_key", "random_number", "random_string")
// newDataBDF.show()     

    
     val allDataMap = Map(
      randomTB -> newDataDF,
      srcTableA -> newDataADF,
      srcTableB -> newDataBDF
    )


    return allDataMap
  }


  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    return inDFs
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {

    val dfRandomTB = outDFs.getOrElse(randomTB, null)
    val dfsrcTableA = outDFs.getOrElse(srcTableA, null)
    val dfsrcTableB = outDFs.getOrElse(srcTableB, null)
    dfsrcTableA.persist(StorageLevel.MEMORY_AND_DISK)
    dfsrcTableB.persist(StorageLevel.MEMORY_AND_DISK)
    dfRandomTB.show(500)
    dfsrcTableA.show(500)
    dfsrcTableB.show(500)
//***** enable when write to Hive + disable if test on local *******//
    
   
   dfRandomTB.write.mode("overwrite").option("truncate", "true").insertInto(randomTB)
   dfsrcTableA.write.mode("overwrite").option("truncate", "true").insertInto(srcTableA)
   dfsrcTableB.write.mode("overwrite").option("truncate", "true").insertInto(srcTableB)    
  
  val writeAppendCount = config.getLong("writeAppendCount")
   for (i<-1L to writeAppendCount)
  {
         dfsrcTableA.write.mode("append").option("truncate", "true").insertInto(srcTableA)
         dfsrcTableB.write.mode("append").option("truncate", "true").insertInto(srcTableB)  
  }
  }

  
  
  def loadHiveTableData(): DataFrame = {
    //Reading the data into Data frames
    println("Reading the data into Data frames")
    val clmQuery = config.getString("randomTB")
    println(clmQuery)
    println("Reading the queries from config file")
    val clmDF = spark.sql(clmQuery).repartition(200)
    clmDF.printSchema()

    clmDF
  }

}
