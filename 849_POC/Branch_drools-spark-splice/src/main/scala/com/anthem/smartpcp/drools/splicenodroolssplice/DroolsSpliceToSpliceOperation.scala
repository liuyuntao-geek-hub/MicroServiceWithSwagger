package com.anthem.smartpcp.drools.splicenodroolssplice

import java.io.File
import java.util.Properties
import java.sql.DriverManager
import java.sql.Connection
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.DataFrame
import org.kie.api.io.Resource
import org.apache.spark.sql.functions.{current_timestamp, lit}
import org.apache.spark.SparkFiles
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SaveMode

import com.anthem.smartpcp.drools.util.{OperationSession}
import com.anthem.smartpcp.drools.util.Operator
import com.anthem.smartpcp.drools.util.KieSessionApplier
import com.anthem.smartpcp.drools.util._
import com.anthem.smartpcp.drools.model._  

class DroolsSpliceToSpliceOperation (confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

 sc.setLogLevel("info")

  def loadData(): Map[String, DataFrame] = {
      
    import spark.implicits._
    val allDataMap = Map(
      "NewData1" -> loadSpliceDroolsTableData() )
      return allDataMap
  }

  def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    println("************* Before Apply Rules *************")
    inDFs.getOrElse("NewData1", null).show()
    import spark.implicits._
 
    var inDf= inDFs.getOrElse("NewData1", null)
    
    for (i <- 1 to config.getInt("DroolsDFCount"))
    {
     inDf=inDf.union(inDFs.getOrElse("NewData1", null))
    }
    
  val outDFs = (inDf).map(row=>(row.getString(0),row.getString(1))).toDF("Longname", "Shortname")
  val ReadyDFs=outDFs.withColumn("lastUpdatedDate", lit(current_timestamp())).withColumn("id_key", lit(monotonically_increasing_id()) ).map(row=>(row.get(row.fieldIndex("id_key")).toString(), 
        row.getString(row.fieldIndex("Longname")), row.getTimestamp(row.fieldIndex("lastUpdatedDate")), row.getString(row.fieldIndex("Shortname")) 
        )
        ).toDF("id_key","Longname","lastUpdatedDate","Shortname")
  
  val allDataMap = Map(
      "state_process_details" -> ReadyDFs
    )

  return allDataMap;
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {
    println("************* After Apply Rules *************")
  val df1 = outDFs.getOrElse("state_process_details", null)
  
  val jdbcUsername = ""
  val jdbcPassword = ""
  val jdbcHostname = "dwbdtest1r5w1.wellpoint.com"
  val jdbcPort = 1527
  val jdbcDatabase ="splicedb"
  val jdbcUrl = s"jdbc:splice://${jdbcHostname}:${jdbcPort}/${jdbcDatabase}"
  val jdbcDriver = "com.splicemachine.db.jdbc.ClientDriver"
  
  val connectionProperties = new Properties()
  connectionProperties.put("user", jdbcUsername)
  connectionProperties.put("password", jdbcPassword)
  
  Class.forName(jdbcDriver)
  val connection = DriverManager.getConnection(jdbcUrl,connectionProperties)
  val a = spark.sql("SET ROLE DV_SMARTPCP_DVLPR_XM")
  val employees_table = df1.write.mode(SaveMode.Overwrite).jdbc(jdbcUrl, "SYS.SYSTABLES", connectionProperties)
   }

def loadSpliceDroolsTableData(): DataFrame = {
  val jdbcUsername = ""
  val jdbcPassword = ""
  val jdbcHostname = "dwbdtest1r5w1.wellpoint.com"
  val jdbcPort = 1527
  val jdbcDatabase ="splicedb"
  val jdbcUrl = s"jdbc:splice://${jdbcHostname}:${jdbcPort}/${jdbcDatabase}"
  val jdbcDriver = "com.splicemachine.db.jdbc.ClientDriver"
  
  val connectionProperties = new Properties()
  connectionProperties.put("user", jdbcUsername)
  connectionProperties.put("password", jdbcPassword)
  
  Class.forName(jdbcDriver)
  val connection = DriverManager.getConnection(jdbcUrl,connectionProperties)
  val a = spark.sql("SET ROLE DV_SMARTPCP_DVLPR_XM")
  val employees_table = spark.read.jdbc(jdbcUrl, "SYS.SYSTABLES", connectionProperties)
  employees_table.printSchema() 
  return employees_table
  }
}

object rulesProcessorFromFile extends  Serializable {
    def applyShortnameRuleFromFile(row: org.apache.spark.sql.Row, file:File): (String, String) = {
      var state = new State();
      state.setLongname(row.getString(row.fieldIndex("longname")));
      state.setShortname(row.getString(row.fieldIndex("shortname")));
      (new KieSessionApplier(file)).applyShortnameRuleFromFile(state)
      println(state.getLongname, state.getShortname)
      return (state.getLongname, state.getShortname)

    }
}  
