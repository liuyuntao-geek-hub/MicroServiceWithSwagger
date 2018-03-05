package com.anthem.smartpcp.drools.splicedroolssplice

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
    val FilePath = config.getString("DroolsXLSRuleFile")
    val FileName = config.getString("DroolsXLSRuleFileName")
    println(FilePath);
    println(FileName);

    if (env.equalsIgnoreCase("local"))
      {
      sc.addFile(FilePath)
      }
      else
      {
      sc.addFile(FileName)
      }
  
    val localFile = SparkFiles.get(FileName)
    println(localFile)
    
    var drlFile:File = null;
      if (env.equalsIgnoreCase("local"))
        {
       drlFile = new File(localFile);
        }
      else
      {
      drlFile = new File(FileName);
      }
    println(drlFile)
    import spark.implicits._
   
    val outDFs = (inDFs.getOrElse("NewData1", null)).map(row=>{rulesProcessorFromFile.applyShortnameRuleFromFile(row,drlFile)}).toDF("longname", "shortname")
      outDFs.show()
      val row = inDFs.getOrElse("NewData1", null).first()
  
      val allDataMap = Map(
        "NewData1" -> outDFs
      )
  
      return allDataMap;
  }

  def writeData(outDFs: Map[String, DataFrame]): Unit = {
    println("************* After Apply Rules *************")
  val df1 = outDFs.getOrElse("NewData1", null)
  
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
