package com.anthem.smartpcp.drools.dfdroolssplice  

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

import com.anthem.smartpcp.drools.model._
import com.anthem.smartpcp.drools.util._

class DroolsDFOperation (confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

sc.setLogLevel("info")

def loadData(): Map[String, DataFrame] = { 
    var newData1 = List(
      ("Alabama",null),
      ("Alaska",null),  
      ("Arizona",null),
      ("Arkansas",null),
      ("California",null),
      ("Colorado",null),
      ("Connecticut",null),
      ("Delaware",null),
      ("Florida",null),
      ("Georgia",null),
      ("Hawaii",null),
      ("Idaho",null),
      ("Illinois",null),
      ("Indiana",null),
      ("Iowa",null),
      ("Kansas",null),
      ("Kentucky",null),
      ("Louisiana",null),
      ("Maine",null),
      ("Maryland",null),
      ("Massachusetts",null),
      ("Michigan",null),
      ("Minnesota",null),
      ("Mississippi",null),
      ("Missouri",null),
      ("Montana",null),
      ("Nebraska",null),
      ("Nevada",null),
      ("New Hampshire",null),
      ("New Jersey",null),
      ("New Mexico",null),
      ("New York",null),
      ("North Carolina",null),
      ("North Dakota",null),
      ("Ohio",null),
      ("Oklahoma",null),
      ("Oregon",null),
      ("Pennsylvania",null),
      ("Rhode Island",null),
      ("South Carolina",null),
      ("South Dakota",null),
      ("Tennessee",null),
      ("Texas",null),
      ("Utah",null),
      ("Vermont",null),
      ("Virginia",null),
      ("Washington",null),
      ("West Virginia",null),
      ("Wisconsin",null),
      ("Wyoming",null)
    )
    
    var newData = newData1
    for (i <- 1 to 2000)
      {newData1=newData1:::newData}

    val allDataMap = Map(
      "NewData1" -> spark.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1, x._2))).toDF("longname", "shortname")
    )
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