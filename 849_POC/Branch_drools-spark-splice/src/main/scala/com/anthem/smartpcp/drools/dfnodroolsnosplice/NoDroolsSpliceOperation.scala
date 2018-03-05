  package com.anthem.smartpcp.drools.dfnodroolsnosplice  

import java.io.File
import org.apache.spark.broadcast.Broadcast
import com.anthem.smartpcp.drools.util.{OperationSession, Operator}
import org.apache.spark.sql.DataFrame
import org.kie.api.io.Resource
import org.apache.spark.sql.functions.{current_timestamp, lit}
import org.apache.spark.SparkFiles
import org.apache.spark.sql.functions._


class NoDroolsSpliceOperation (confFilePath: String, env: String, queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  sc.setLogLevel("info")

def loadData(): Map[String, DataFrame] = { 
    import spark.implicits._
    var newData1 = List(
      ("Alabama","ALA"),
      ("Alaska","ALS"),  
      ("Arizona","ARI"),
      ("Arkansas","ARK"),
      ("California","CAL"),
      ("Colorado","COL"),
      ("Connecticut","CON"),
      ("Delaware","DEL"),
      ("Florida","FLO"),
      ("Georgia","GEO"),
      ("Hawaii","HAW"),
      ("Idaho","IDA"),
      ("Illinois","ILL"),
      ("Indiana","IND"),
      ("Iowa","IOW"),
      ("Kansas","KAN"),
      ("Kentucky","KEN"),
      ("Louisiana","LOU"),
      ("Maine","MAI"),
      ("Maryland","MAR"),
      ("Massachusetts","MAS"),
      ("Michigan","MIC"),
      ("Minnesota","MIN"),
      ("Mississippi","MIS"),
      ("Missouri","MISO"),
      ("Montana","MON"),
      ("Nebraska","NEB"),
      ("Nevada","NEV"),
      ("New Hampshire","NEW_H"),
      ("New Jersey","NEW_J"),
      ("New Mexico","NEW_M"),
      ("New York","NEW_Y"),
      ("North Carolina","NOR_C"),
      ("North Dakota","NOR_D"),
      ("Ohio","OHI"),
      ("Oklahoma","OKL"),
      ("Oregon","ORE"),
      ("Pennsylvania","PEN"),
      ("Rhode Island","RHO"),
      ("South Carolina","SOU_C"),
      ("South Dakota","SOU_D"),
      ("Tennessee","TEN"),
      ("Texas","TEX"),
      ("Utah","UTA"),
      ("Vermont","VER"),
      ("Virginia","VIR"),
      ("Washington","WAS"),
      ("West Virginia","WES"),
      ("Wisconsin","WIS"),
      ("Wyoming","WYO")
    )
    
    var newData = newData1
    for (i <- 1 to 2000)
      {newData1=newData1:::newData}

    val allDataMap = Map(
      "NewData1" -> spark.createDataFrame(sc.parallelize(newData1, 5).map(x => (x._1, x._2))).toDF("Longname", "Shortname")
    )
    return allDataMap
  }
 
def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
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
    import spark.implicits._
    println("************* After Apply Rules *************")
    outDFs.getOrElse("state_process_details", null).show()

  }
}
