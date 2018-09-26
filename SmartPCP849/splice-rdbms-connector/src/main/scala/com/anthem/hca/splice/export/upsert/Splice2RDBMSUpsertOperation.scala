package com.anthem.hca.splice.export.upsert

import java.util.Properties

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.{ col, hash }
import org.joda.time.DateTime
import org.joda.time.Minutes

import com.anthem.hca.spliceexport.helper.Audit
import com.anthem.hca.spliceexport.util.DataFrameUtils
import com.anthem.hca.spliceexport.helper.OperationSession
import com.anthem.hca.spliceexport.helper.Operator
import com.anthem.hca.spliceexport.util.DateUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.Column
import org.apache.spark.sql.catalyst.expressions.Murmur3Hash

class Splice2RDBMSUpsertOperation(configPath: String,env: String,tableName: String)
    extends OperationSession(configPath,env,tableName) with Operator {

  import spark.implicits._

  //Audit
  var program = ""
  var user_id = ""
  var app_id = ""
  var start_time: DateTime = DateTime.now()
  var listBuffer = ListBuffer[Audit]()

  override def extractData(): Map[String, DataFrame] = {
    val startTime = DateTime.now
    println(s"[SPCP-ETL] The loading of Data started with start time at :  + $startTime")
    println("Reading the queries from config file")

    //val exportTblQuery = config.getString(s"export_splice_member_info_query").toLowerCase().replace(ConfigKey.spcpSpliceDBPlaceHolder, spcpSpliceDB)
    //val spliceTblQuery = "select * from DV_PDPSPCP_XM.PROVIDER_INFO_TEST"
    val spliceTblQuery = "select * from dv_pdpspcp_xm.provInf1"
    // println(s"[SPCP-ETL] Query for reading data from $tblName table is: $exportTblQuery")

    val spliceTblDF = splicemachineContext.df(spliceTblQuery)

    //    exportTblDF.show(1)
    spliceTblDF.printSchema()

    val jdbcUrl = config.getString("spcp.sqlserver.url")
    val jdbcDriver = config.getString("spcp.sqlserver.driver")
    val jdbcUsername = config.getString("spcp.sqlserver.username")
    val jdbcPassword = config.getString("spcp.sqlserver.password")
    val sqlServerSchema = config.getString("spcp.sqlserver.schema")

    // Create the JDBC URL without passing in the user and password parameters.

    val connectionProperties = new Properties()
    connectionProperties.put("driver", jdbcDriver)
    connectionProperties.put("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    //TODO:unit testing
    //    connectionProperties.put("user", "af30986")
    //    connectionProperties.put("password", "XXXXXX")
    val sqlCntxt = new SQLContext(sc)
    //val sqlTableDF = sqlCntxt.read.jdbc(jdbcUrl, "DV_PDPSPCP_XM.PROVIDER_INFO", connectionProperties)
    val sqlTableDF = splicemachineContext.df("select * from dv_pdpspcp_xm.provInf2");

    println(s"[SPCP-ETL] Loading data completed at : " + DateTime.now())
    spliceTblDF.show()
    sqlTableDF.show()

    val mapDF = Map("spliceDF" -> spliceTblDF, "sqlDF" -> sqlTableDF)

    mapDF
  }

  override def transformData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {

    val spliceTblDF = inDFs.getOrElse("spliceDF", null)
    val sqlTblDF = inDFs.getOrElse("sqlDF", null)

  //Appending hash column to both dataframes.
    val spliceTblMD5 = spliceTblDF.withColumn("hashSplice", hash(spliceTblDF.columns.map(col): _*))
    val sqlTblMD5 = sqlTblDF.withColumn("hashSql", hash(sqlTblDF.columns.map(col): _*))

    //Joining both dataframes on primary columns.
    val spliceTbl_sqlTbl_joinDF = spliceTblMD5.join(sqlTblMD5,
      spliceTblMD5.col("PROV_PCP_ID") === sqlTblMD5.col("PROV_PCP_ID") &&
        spliceTblMD5.col("RGNL_NTWK_ID") === sqlTblMD5.col("RGNL_NTWK_ID") &&
        spliceTblMD5.col("NPI") === sqlTblMD5.col("NPI"),
      "FULL_OUTER")
    println("Done joining SQL and Splice tables")
    spliceTbl_sqlTbl_joinDF.cache()
    println("Count is " + spliceTbl_sqlTbl_joinDF.count())
    spliceTbl_sqlTbl_joinDF.cache()
    spliceTbl_sqlTbl_joinDF.printSchema()
    spliceTbl_sqlTbl_joinDF.show()

    //Identifying deleted rows.
    val deleteDF = spliceTbl_sqlTbl_joinDF.filter(spliceTblMD5.col("PROV_PCP_ID").isNull
      && spliceTblMD5.col("RGNL_NTWK_ID").isNull
      && spliceTblMD5.col("NPI").isNull)
    println("Formed deleteDF")
    deleteDF.cache()
    println("Count is " + deleteDF.count())
    deleteDF.printSchema()
    deleteDF.show()

    //Identifying inserts.
    val insertDF = spliceTbl_sqlTbl_joinDF.filter(sqlTblMD5.col("PROV_PCP_ID").isNull
      && sqlTblMD5.col("RGNL_NTWK_ID").isNull
      && sqlTblMD5.col("NPI").isNull)
    println("Formed insertDF")
    insertDF.cache()
    println("Count is " + insertDF.count())
    insertDF.printSchema()
    insertDF.show()

    //Identifying rows matching on primary keys.
    val sameRowsDF = spliceTbl_sqlTbl_joinDF.filter(sqlTblMD5.col("PROV_PCP_ID").isNotNull
      && sqlTblMD5.col("RGNL_NTWK_ID").isNotNull
      && sqlTblMD5.col("NPI").isNotNull
      && spliceTblMD5.col("PROV_PCP_ID").isNotNull
      && spliceTblMD5.col("RGNL_NTWK_ID").isNotNull
      && spliceTblMD5.col("NPI").isNotNull)
    println("Formed sameRowsDF")
    sameRowsDF.cache()
    println("Count is " + sameRowsDF.count())
    sameRowsDF.printSchema()
    sameRowsDF.show()

    //val updatedDF = sqlTablRenamedDF.withColumn("hash", hash(sqlTablRenamedDF.columns.map(col): _*))

    //Identifying updates based on MD5 column.
    val updtdDf = sameRowsDF.filter(sameRowsDF.col("hashSplice").notEqual(sameRowsDF.col("hashSql")))
    println("Formed updtdDf")
    updtdDf.cache()
    println("Count is " + updtdDf.count())
    updtdDf.printSchema()
    updtdDf.show()

    //Identifying updates by equating all the non primary columns.
    val updtdDf2 = sameRowsDF.filter(spliceTblMD5("GRPG_RLTD_PADRS_EFCTV_DT").notEqual(sqlTblMD5("GRPG_RLTD_PADRS_EFCTV_DT"))
      .or(spliceTblMD5("GRPG_RLTD_PADRS_TRMNTN_DT").notEqual(sqlTblMD5("GRPG_RLTD_PADRS_TRMNTN_DT")))
      .or(spliceTblMD5("ADRS_ZIP_CD").notEqual(sqlTblMD5("ADRS_ZIP_CD")))
      .or(spliceTblMD5("ADRS_ZIP_PLUS_4_CD").notEqual(sqlTblMD5("ADRS_ZIP_PLUS_4_CD")))
      .or(spliceTblMD5("LATD_CORDNT_NBR").notEqual(sqlTblMD5("LATD_CORDNT_NBR")))
      .or(spliceTblMD5("LNGTD_CORDNT_NBR").notEqual(sqlTblMD5("LNGTD_CORDNT_NBR")))
      .or(spliceTblMD5("ADRS_CNTY_CD").notEqual(sqlTblMD5("ADRS_CNTY_CD")))
      .or(spliceTblMD5("PCP_RANKG_ID").notEqual(sqlTblMD5("PCP_RANKG_ID")))
      //.or(spliceTblMD5("RGNL_NTWK_ID").notEqual(sqlTblMD5("RGNL_NTWK_ID")))
      .or(spliceTblMD5("SPCLTY_CD").notEqual(sqlTblMD5("SPCLTY_CD")))
      .or(spliceTblMD5("WGS_SPCLTY_CD").notEqual(sqlTblMD5("WGS_SPCLTY_CD")))
      .or(spliceTblMD5("SPCLTY_DESC").notEqual(sqlTblMD5("SPCLTY_DESC")))
      .or(spliceTblMD5("SPCLTY_MNEMONICS").notEqual(sqlTblMD5("SPCLTY_MNEMONICS")))
      .or(spliceTblMD5("PRMRY_SPCLTY_IND").notEqual(sqlTblMD5("PRMRY_SPCLTY_IND")))
      .or(spliceTblMD5("MAX_MBR_CNT").notEqual(sqlTblMD5("MAX_MBR_CNT")))
      .or(spliceTblMD5("CURNT_MBR_CNT").notEqual(sqlTblMD5("CURNT_MBR_CNT")))
      .or(spliceTblMD5("CP_TYPE_CD").notEqual(sqlTblMD5("CP_TYPE_CD")))
      .or(spliceTblMD5("ACC_NEW_PATIENT_FLAG").notEqual(sqlTblMD5("ACC_NEW_PATIENT_FLAG")))
      .or(spliceTblMD5("ISO_3_CODE").notEqual(sqlTblMD5("ISO_3_CODE")))
      .or(spliceTblMD5("PCP_LANG").notEqual(sqlTblMD5("PCP_LANG")))
      .or(spliceTblMD5("VBP_FLAG").notEqual(sqlTblMD5("VBP_FLAG")))
      .or(spliceTblMD5("HMO_TYPE_CD").notEqual(sqlTblMD5("HMO_TYPE_CD")))
      .or(spliceTblMD5("PCP_FRST_NM").notEqual(sqlTblMD5("PCP_FRST_NM")))
      .or(spliceTblMD5("PCP_MID_NM").notEqual(sqlTblMD5("PCP_MID_NM")))
      .or(spliceTblMD5("PCP_LAST_NM").notEqual(sqlTblMD5("PCP_LAST_NM")))
      .or(spliceTblMD5("ADRS_LINE_1_TXT").notEqual(sqlTblMD5("ADRS_LINE_1_TXT")))
      .or(spliceTblMD5("ADRS_LINE_2_TXT").notEqual(sqlTblMD5("ADRS_LINE_2_TXT")))
      .or(spliceTblMD5("ADRS_CITY_NM").notEqual(sqlTblMD5("ADRS_CITY_NM")))
      .or(spliceTblMD5("ADRS_ST_CD").notEqual(sqlTblMD5("ADRS_ST_CD")))
      .or(spliceTblMD5("PA_CMNCTN_TYP_VALUE").notEqual(sqlTblMD5("PA_CMNCTN_TYP_VALUE")))
      .or(spliceTblMD5("PROV_PRTY_CD").notEqual(sqlTblMD5("PROV_PRTY_CD")))
      //.or(spliceTblMD5("NPI").notEqual(sqlTblMD5("NPI")))
      .or(spliceTblMD5("TAX_ID").notEqual(sqlTblMD5("TAX_ID")))
      .or(spliceTblMD5("TIER_LEVEL").notEqual(sqlTblMD5("TIER_LEVEL"))))
    println("Formed updtdDf2")
    updtdDf2.cache()
    println("Count is " + updtdDf2.count())
    updtdDf2.printSchema()
    updtdDf2.show()

    return null

  }

  override def loadData(outDFs: Map[String, DataFrame]): Unit = {
  }

  @Override
  def beforeExtractData() {

    program = sc.appName
    user_id = sc.sparkUser
    app_id = sc.applicationId

    start_time = DateTime.now()

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), "0min", "Started")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, lit(current_timestamp()))

    val spcpAuditDFWithUppercaseCol = DataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    //using upsert to avoid insert failure while job retry , also trying to limit job retries to 1
    //  splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDb + "." + spcpAuditTable)
  }

  @Override
  def afterLoadData() {

    var listBuffer = ListBuffer[Audit]()
    val duration = Minutes.minutesBetween(start_time, DateTime.now()).getMinutes + "mins"

    listBuffer += Audit(program, user_id, app_id, DateUtils.currentTimestamp(), duration, "completed")
    val spcpAuditDF = listBuffer.toDS().withColumn(lastUpdatedDate, current_timestamp())
    val spcpAuditDFWithUppercaseCol = DataFrameUtils.columnsInUpper(spcpAuditDF)
    spcpAuditDFWithUppercaseCol.show
    //splicemachineContext.upsert(spcpAuditDFWithUppercaseCol, spcpSpliceDb + "." + spcpAuditTable)

  }

}