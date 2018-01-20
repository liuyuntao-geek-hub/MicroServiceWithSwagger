package com.anthem.hpip.mtclm_hive

import org.apache.spark.sql.DataFrame

import com.anthem.hpip.helper.OperationSession
import com.anthem.hpip.helper.Operator

class MTCLMOperation(confFilePath: String, env: String,queryFileCategory: String) extends OperationSession(confFilePath, env,queryFileCategory) with Operator {

  def loadData(): Map[String, DataFrame] = {

    println("-------Inside Load method--------")

    val df_clm = loadFromHiveCLM()
    val df_clm_coa = loadFromHhiveCLM_COA()

    val targetTableName_mtclm = config.getString("targetTableName_mtclm")
    val targetTableName_mtclm_coa = config.getString("targetTableName_mtclm_coa")

    val map = Map(targetTableName_mtclm -> df_clm, targetTableName_mtclm_coa -> df_clm_coa)
    map
    //val map = Map(targetTableName_mtclm -> df_clm_coa)

  }

  def processData(map: Map[String, DataFrame]): Map[String, DataFrame] = {
    println("-------Inside Process method--------")
    return map;
  }

  def writeData(map: Map[String, DataFrame]): Unit = {
    println("-------Inside Write method--------")
    map.foreach(x => {
      println("-------Inside For loop--------")
      val hiveDB = config.getString("hiveDB")
      println("hiveDB" + hiveDB)
      val tablename = x._1
      println("tablename" + tablename)
      val df = x._2
      //						df.show(false)
      df.write.mode("overwrite").saveAsTable(hiveDB + """.""" + tablename)
      println("Table created " + tablename)

    })

  }

  def writeData(df: DataFrame, tableName: String): Unit = {
    val hiveDB = config.getString("hiveDB")
    val targetTableName = tableName
    df.show(false)
    df.write.mode("overwrite").saveAsTable(hiveDB + """.""" + targetTableName)
  }

  def loadFromHiveCLM(): DataFrame = {
    println("-------Inside Load method for MTCLM --------")
    val mtclm_query = config.getString("SOURCE_DATA_QUERY_MTCLM")
    println("The select query for MTCLM is " + mtclm_query)
    val mtclm_df = hiveContext.sql(mtclm_query)
    //					mtclm_df.show(false)

    return mtclm_df
  }

  def loadFromHhiveCLM_COA(): DataFrame = {
    println("-------Inside Load method for MTCLM_COA--------")
    val mtclm_coa_query = config.getString("SOURCE_DATA_QUERY_MTCLM_COA")
    println("The select query for mtclm_coa is " + mtclm_coa_query)
    val mtclm_coa_df = hiveContext.sql(mtclm_coa_query)
    //					mtclm_coa_df.show(false)
    //writeData(df_mtclm_coa, targetTableName)
    return mtclm_coa_df

  }
}