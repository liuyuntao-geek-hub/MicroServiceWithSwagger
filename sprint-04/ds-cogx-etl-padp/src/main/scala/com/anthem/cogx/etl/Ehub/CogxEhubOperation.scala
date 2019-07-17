package com.anthem.cogx.etl.Ehub

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import scala.reflect.runtime.universe

import org.apache.commons.codec.digest.DigestUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Delete
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.rdd.PairRDDFunctions
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.concat
import org.apache.spark.sql.functions.current_date
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.substring
import org.apache.spark.sql.functions.to_date
import org.apache.spark.sql.functions.unix_timestamp
import org.apache.spark.storage.StorageLevel

import com.anthem.cogx.etl.config.CogxConfigKey
import com.anthem.cogx.etl.helper.CogxOperationSession
import com.anthem.cogx.etl.helper.CogxOperator
import com.anthem.cogx.etl.helper.ehubWpidDelete
import com.anthem.cogx.etl.helper.ehubWpidExtract
import com.anthem.cogx.etl.helper.ehubWpidHistory
import org.apache.hadoop.hbase.HConstants
import org.apache.spark.sql.AnalysisException
import org.apache.hadoop.hbase.KeyValue
import com.anthem.cogx.etl.util.CogxCommonUtils.asJSONString

/**
 * Created by yuntliu on 1/20/2018.
 */

class CogxEhubOperation(confFilePath: String, env: String, queryFileCategory: String) extends CogxOperationSession(confFilePath, env, queryFileCategory) with CogxOperator {

	//	sc.setLogLevel("info")

	import spark.implicits._
	import spark.sql

	var rowCount: Long = 0l
	var HardDelConList: List[String] = null

	def loadData(): Map[String, DataFrame] = {
	
			//Extracting data from Text files
			val files = FileSystem.get(sc.hadoopConfiguration).listStatus(new Path(config.getString("ehub_extracts_path")))
					val filesToDelete = FileSystem.get(sc.hadoopConfiguration).listStatus(new Path(config.getString("ehub_delete_path")))

					var extractDf = spark.emptyDataset[ehubWpidExtract].rdd.toDF
					var deleteExtractDf = spark.emptyDataset[ehubWpidDelete].rdd.toDF

					//Extracting date for load_dt column from Extract file 
//					val dateOfExtract = config.getString("load_dt_temp")

					//Loading all the files from Extract path to dataframe
					files.foreach(filename => {
						val a = filename.getPath.toString()
								var df = fileExtract(a).repartition(1000)
								extractDf = extractDf.union(df)
					})

					// Loading all the files for Deleted contracts to dataframe
					filesToDelete.foreach(filename => {
						val a = filename.getPath.toString()
								var df1 = fileDelete(a).repartition(1000)
								deleteExtractDf = deleteExtractDf.union(df1)
					})

					//Formatting the date to yyyy-MM-dd
					//Adding two columns to dataframe for auditing CONTRACT_START_DT,load_dt
					 val incrmntlDf = extractDf.toDF().withColumn("START_DT", to_date(unix_timestamp(extractDf("START_DT"), "MMddyyyy").cast("timestamp")))
					.withColumn("END_DT", to_date(unix_timestamp(extractDf("END_DT"), "MMddyyyy").cast("timestamp")))
					.withColumn("CONTRACT_START_DT", lit(concat($"CONTRACT", $"START_DT")))
					.withColumn("load_dt", lit(current_date())).persist(StorageLevel.MEMORY_AND_DISK)

					val deleteDf = deleteExtractDf.toDF().withColumn("START_DT", to_date(substring($"START_DT", 0, 10)))
					.withColumn("END_DT", to_date(substring($"END_DT", 0, 10)))
					.withColumn("CONTRACT_START_DT", lit(concat($"CONTRACT", $"START_DT")))
					.withColumn("load_dt", lit(current_date())).persist(StorageLevel.MEMORY_AND_DISK)
	  
					//Load data from history table
					val ehubHistoryQuery = config.getString("ehub_history_query").replaceAll(CogxConfigKey.stagingDBPlaceHolder, stagingHiveDB).toLowerCase()
					val historyDf = spark.sql(ehubHistoryQuery)

					val dataMap = Map("ehub_extract" -> incrmntlDf,
							"ehub_delete" -> deleteDf,
							"ehub_hisotry" -> historyDf)

					return dataMap

	}

	def processData(inDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
			val incrmntlDf = inDFs.getOrElse("ehub_extract", null)
					val deleteDf = inDFs.getOrElse("ehub_delete", null)
					val historyDf = inDFs.getOrElse("ehub_hisotry", null)
					val incrementalTable = config.getString("ehub_incremental_table")
					val deleteTable = config.getString("ehub_delete_table")
					val ehubHistoryTempTable = config.getString("ehub_history_temp_table")
					val ehubHistoryTable = config.getString("ehub_history_table")

					println(s"Loading the data into partitioned table $stagingHiveDB.$incrementalTable ")
					incrmntlDf.write.mode("overwrite").insertInto(s"$stagingHiveDB.$incrementalTable")

					println(s"Loading the data into partitioned table $stagingHiveDB.$deleteTable  ")
					deleteDf.write.mode("overwrite").insertInto(s"$stagingHiveDB.$deleteTable")

					//Step 1 - Extracting the contract_strt_dt from incremental data

					val distinctContractIncr = sc.broadcast(incrmntlDf.select($"CONTRACT").distinct.map(r => r.getString(0)).collect.toList)
					val distinctDelCon = sc.broadcast(deleteDf.select($"CONTRACT").distinct.map(r => r.getString(0)).collect.toList)
					val distinctDelete = sc.broadcast(deleteDf.select($"CONTRACT_START_DT").distinct.map(r => r.getString(0)).collect.toList)

					//Step 2 - Extracting the contracts which are updated from history 
					val joinIncDf = incrmntlDf.join(historyDf, incrmntlDf("CONTRACT_START_DT") === historyDf("CONTRACT_START_DT"), "full_outer").distinct

					val matchedDf = joinIncDf.filter(incrmntlDf("CONTRACT_START_DT").isNotNull && historyDf("CONTRACT_START_DT").isNotNull).select(incrmntlDf("*")).distinct

					//Step 3 - Extracting the contracts which not are updated from history 
					val unmatched = joinIncDf.filter(incrmntlDf("CONTRACT_START_DT").isNull).select(historyDf("*")).distinct

					//Step 4 - Extracting the contracts which newly added in incremental
					val newExtDf = joinIncDf.filter(historyDf("CONTRACT_START_DT").isNull).select(incrmntlDf("*")).distinct

					// Step 5 - Updating the data from history table also adding the newly added and untouched records
					val uniondf = matchedDf.union(unmatched).union(newExtDf).distinct

					// Step 6 - Deleting the records matching with deleted contracts
					val withdeleteDf = uniondf.filter($"CONTRACT_START_DT".isin(distinctDelete.value: _*) === false)

					val forHardDelete = sc.broadcast(withdeleteDf.select($"CONTRACT").distinct.map(r => r.getString(0)).collect.toList)

					//Incremental data after deleting the contracts
					HardDelConList = deleteDf.filter($"CONTRACT".isin(forHardDelete.value: _*) === false).map(r => r.getString(0)).collect.toList
					println("TDistinct contracts for final HardDelConList " + HardDelConList)

					val incrSelDf = withdeleteDf.filter($"CONTRACT".isin(distinctContractIncr.value: _*) || $"CONTRACT".isin(HardDelConList: _*) === false)

					//Purging the data older than 10 days for backup in incremental 
					purgePartition(incrementalTable, stagingHiveDB, "incremental")

					//Purging the data older than 10 days for backup in delete 
					purgePartition(deleteTable, stagingHiveDB , "delete")
					
					//Using temp table to load the hostory data
					println(s"Loading the data into table $stagingHiveDB.$ehubHistoryTempTable")
					withdeleteDf.write.mode("overwrite").saveAsTable(s"$stagingHiveDB.$ehubHistoryTempTable")

					//Reading from temp table to load the hostory data
					val ehubHostoryTempQuery = config.getString("ehub_history_temp_query").replaceAll(CogxConfigKey.stagingDBPlaceHolder, stagingHiveDB).toLowerCase()
					val tempData = spark.sql(ehubHostoryTempQuery)
					
					//Loading the updated data including history data 
					println(s"Loading the data into table $stagingHiveDB.$ehubHistoryTable")
					tempData.write.mode("overwrite").insertInto(s"$stagingHiveDB.$ehubHistoryTable")
					
					//count for Auditing 
					rowCount = incrSelDf.count()
					ABC_load_count = rowCount.toLong
					
					println("println: CogX Row Count => " + rowCount)
					
					val repartitionNum = config.getInt("repartitionNum")
					val ArraySizeLimit = config.getInt("ArraySizeLimit")
					
					val CogxUmHbaseDataSet = incrSelDf.as[ehubWpidExtract].repartition(repartitionNum)
					
					val RDDSet = CogxUmHbaseDataSet.rdd.repartition(repartitionNum).map(record => (record.CONTRACT, Set(record))).reduceByKey((a, b) => {
						if (a.size <= ArraySizeLimit) { a ++ b }
						else { print("======== Oversized CONTRACT (over size 3000):" + a.last.CONTRACT); a }
					}).repartition(repartitionNum)

					var DSSet = RDDSet.map(k => {
						(new StringBuilder((DigestUtils.md5Hex(String.valueOf(k._1))).substring(0, 8)).append(k._1).toString(), asJSONString(new ehubWpidHistory(k._2.toArray)))
					})
					.repartition(repartitionNum)

					var newDF = DSSet.toDF("rowKey", "jsonData").repartition(repartitionNum)

					println("Total New Count: " + newDF.count())
					
//					//TODO : Remove this step after sit is done
//					println(s"Loading the data to table $stagingHiveDB.cogx_benefits_json")
//					newDF.write.mode("overwrite").saveAsTable(s"$stagingHiveDB.cogx_benefits_json")

					val dataMap = Map("ehub_extract" -> newDF)

					return dataMap
	}

	def writeData(outDFs: Map[String, DataFrame]): Unit = {
	        
	        //Reading the processed data and persisting the same
			    val df1 = outDFs.getOrElse("ehub_extract", null).toDF("rowKey", "jsonData")
					df1.persist(StorageLevel.MEMORY_AND_DISK)
					
					//Need a list with hash function for rowkeys to be deleted from Hbase 
					val HardDelConListCd: List[String] = HardDelConList.map(x => DigestUtils.md5Hex(x).substring(0, 8) + x.toString())
					val hbaseTable = config.getString("hbase_schema") + ":" + config.getString("hbase_table_name")
				
					val conf = HBaseConfiguration.create();
					val table = new HTable(conf, hbaseTable);
					val columnFamily = config.getString("hbase_table_columnfamily")
					val columnName = config.getString("hbase_table_columnname")

							val putRDD = df1.rdd.map(x => {
								val rowKey = x.getAs[String]("rowKey")
										val holder = x.getAs[String]("jsonData")
										//(rowKey, holder)
										val p = new Put(Bytes.toBytes(rowKey))
										p.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes(holder))
										(new org.apache.hadoop.hbase.io.ImmutableBytesWritable, p)
							})

							if (env.equalsIgnoreCase("local")) {
								//// Run it on local ////
								getMapReduceJobConfiguration(hbaseTable)
							} else {
								/// Run it on Cluster now
								new PairRDDFunctions(putRDD).saveAsNewAPIHadoopDataset(getMapReduceJobConfiguration(hbaseTable))
								
								//Deleting the hbase rowkeys if needed
								HardDelConListCd.foreach(rowkey => {
									val marker = new KeyValue(Bytes.toBytes(rowkey), Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), HConstants.LATEST_TIMESTAMP, KeyValue.Type.DeleteColumn)
											table.delete(new Delete(Bytes.toBytes(rowkey)).addDeleteMarker(marker))
											println(s"Delete done for $rowkey")
								})
							}

	}

	def fileExtract(fileinput: String): DataFrame = {
			val fileread = spark.read.textFile(fileinput)
					val header = fileread.first()
					val ehubExtractDfWH = fileread.filter(row => row != header).map { list => list.toString().replace("|~", "¤").split("¤", -1) }.map(a => ehubWpidExtract(a(0), a(1), a(2), a(3), a(4), a(5), a(6), a(7), a(8), a(9), a(10), a(11), a(12), a(13))).toDF()
					ehubExtractDfWH
	}
	def fileDelete(fileinput: String): DataFrame = {
			val fileread = spark.read.textFile(fileinput)
					val header = fileread.first()
					val ehubExtractDfWH = fileread.filter(row => row != header).map { list => list.toString().replace("|~", "¤").split("¤", -1) }.map(a => ehubWpidDelete(a(0), a(1), a(2), a(3), a(4))).toDF()
					ehubExtractDfWH
	}

	def purgePartition(tablename: String, schema: String , tableType : String) {

		//			  Purging the last 10 days of data
	  //Select count(*) (Select distint load_dt from tbl)a; - if this count is >10;
    //Select min(distinct load_dt) from tbl;
    //Do alter partition with that min load_dt.
	  
	  var partitionsQuery =""
	  var getLoadDtQuery =""
	  if(tableType.equalsIgnoreCase("incremental")){
	     partitionsQuery = config.getString("incr_partition_count_query").replaceAll(CogxConfigKey.stagingDBPlaceHolder, stagingHiveDB).toLowerCase()
	     getLoadDtQuery = config.getString("incr_get_min_load_dt_query").replaceAll(CogxConfigKey.stagingDBPlaceHolder, stagingHiveDB).toLowerCase()
	  }else{
	    partitionsQuery = config.getString("del_partition_count_query").replaceAll(CogxConfigKey.stagingDBPlaceHolder, stagingHiveDB).toLowerCase()
	    getLoadDtQuery = config.getString("del_get_min_load_dt_query").replaceAll(CogxConfigKey.stagingDBPlaceHolder, stagingHiveDB).toLowerCase()
	  }
   
    val noOfPartitionDf = spark.sql(partitionsQuery).collect.head.getLong(0)
	  
    if(noOfPartitionDf >= 10){
       
      val getMinLoadDt = spark.sql(getLoadDtQuery).collect.head.getString(0)
      try {
		    println(s"Dropping the partition for load_dt $getMinLoadDt")
				spark.sql(s"""ALTER TABLE $schema.$tablename DROP PARTITION (load_dt="$getMinLoadDt")""")
				println(s"Partition droppped for load_dt $getMinLoadDt")
      }catch {
      case e: Exception => {
        println(s"Partition not found for $getMinLoadDt")
      }
     }
    }
    
	}
				//If it is an external table, we could use deleteFile method below to delete the external hdfs location

				def deleteFile(path: String) {
			val filePath = new Path(path)
					val HDFSFilesSystem = filePath.getFileSystem(new Configuration())
					if (HDFSFilesSystem.exists(filePath)) {
						HDFSFilesSystem.delete(filePath, true)
					}
		}

	

}