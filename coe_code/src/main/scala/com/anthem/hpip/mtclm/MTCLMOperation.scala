package com.anthem.hpip.mtclm

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.substring

import com.anthem.hpip.config.ConfigKey
import com.anthem.hpip.helper.OperationSession
import com.anthem.hpip.helper.Operator

class MTCLMOperation(configPath: String, env: String, queryFileCategory: String) extends OperationSession(configPath, env, queryFileCategory) with Operator {

	def loadData(): Map[String, DataFrame] = {

			//Reading the data into Data frames
			println("Reading the data into Data frames")

			val clmQuery = config.getString("query_clm").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
			println(clmQuery)
			val clmLineQuery = config.getString("query_clm_line").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
			val clmPaidQuery = config.getString("query_clm_paid").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
			val fcltyClmQuery = config.getString("query_fclty_clm").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()
			val clmLineCoaQuery = config.getString("query_clm_line_coa").replaceAll(ConfigKey.sourceDBPlaceHolder, config.getString("inbound-hive-db")).toLowerCase()

			//Reading the queries from config file
			println("Reading the queries from config file")
			val clmDF = hiveContext.sql(clmQuery).repartition(200)
			val clmLineDF = hiveContext.sql(clmLineQuery).repartition(200)
			val clmPaidDF = hiveContext.sql(clmPaidQuery).repartition(200)
			val fcltyClmDF = hiveContext.sql(fcltyClmQuery).repartition(200)
			val clmLineCoaDF = hiveContext.sql(clmLineCoaQuery).repartition(200)

			//Creating Map of Data frames 
			println("Creating Map of Data frames")

			val mapDF = Map("clm" -> clmDF, "clm_line" -> clmLineDF, "clm_paid" -> clmPaidDF, "fclty_clm" -> fcltyClmDF, "clm_line_coa" -> clmLineCoaDF)
			mapDF

	}

	def processData(inMapDF: Map[String, DataFrame]): Map[String, DataFrame] = {

			//Processing the data
			println("Processing the data")

			//Reading the data frames as elements from Map
			println("Reading the data frames as elements from Map")
			val clmDF = inMapDF.getOrElse("clm", null)
			val clmLineDF = inMapDF.getOrElse("clm_line", null)
			val clmPaidDF = inMapDF.getOrElse("clm_paid", null)
			val fcltyClmDF = inMapDF.getOrElse("fclty_clm", null)
			val clmLineCoaDF = inMapDF.getOrElse("clm_line_coa", null)

			val lastUpdatedDate = config.getString("audit-column-name").toLowerCase
			//Processing the data using joins for MTCLM
			println("Processing the data using joins for MTCLM")

			val clmJoinClmLineDF = clmDF.join(clmLineDF, clmDF("clm_adjstmnt_key") === clmLineDF("clm_adjstmnt_key") && clmDF("adjdctn_dt") === clmLineDF("adjdctn_dt") && clmDF("clm_sor_cd") === clmLineDF("clm_sor_cd"), "inner").select(clmDF("clm_adjstmnt_key"), clmDF("mbrshp_sor_cd"), clmDF("clm_its_host_cd"), clmDF("srvc_rndrg_type_cd"), clmDF("src_prov_natl_prov_id"), clmDF("src_billg_tax_id"), clmDF("mbr_key"), clmDF("rx_filled_dt"), clmDF("clm_sor_cd"), clmDF("adjdctn_dt"))
			val clmJoinClmLineJoinclmPaidDF = clmJoinClmLineDF.join(clmPaidDF, clmJoinClmLineDF("clm_adjstmnt_key") === clmPaidDF("clm_adjstmnt_key"), "left_outer").select(clmJoinClmLineDF("clm_adjstmnt_key"), clmJoinClmLineDF("mbrshp_sor_cd"), clmJoinClmLineDF("clm_its_host_cd"), clmJoinClmLineDF("srvc_rndrg_type_cd"), clmJoinClmLineDF("src_prov_natl_prov_id"), clmJoinClmLineDF("src_billg_tax_id"), clmJoinClmLineDF("mbr_key"), clmJoinClmLineDF("rx_filled_dt"), clmPaidDF("gl_post_dt"), clmJoinClmLineDF("clm_sor_cd"), clmJoinClmLineDF("adjdctn_dt"))
			val mtclmDF = clmJoinClmLineJoinclmPaidDF.join(fcltyClmDF, clmJoinClmLineJoinclmPaidDF("clm_adjstmnt_key") === fcltyClmDF("clm_adjstmnt_key") && clmJoinClmLineJoinclmPaidDF("clm_sor_cd") === fcltyClmDF("clm_sor_cd"), "left_outer").select(clmJoinClmLineJoinclmPaidDF("clm_adjstmnt_key"), clmJoinClmLineJoinclmPaidDF("mbrshp_sor_cd"), clmJoinClmLineJoinclmPaidDF("clm_its_host_cd"), clmJoinClmLineJoinclmPaidDF("srvc_rndrg_type_cd"), clmJoinClmLineJoinclmPaidDF("src_prov_natl_prov_id"), clmJoinClmLineJoinclmPaidDF("src_billg_tax_id"), clmJoinClmLineJoinclmPaidDF("mbr_key"), clmJoinClmLineJoinclmPaidDF("rx_filled_dt"), clmJoinClmLineJoinclmPaidDF("gl_post_dt"), fcltyClmDF("admt_dt"), clmJoinClmLineJoinclmPaidDF("clm_sor_cd"), clmJoinClmLineJoinclmPaidDF("adjdctn_dt")).distinct
			val mtclmWithAuditColumn = mtclmDF.withColumn(lastUpdatedDate, lit(current_timestamp()))
			val mtclmColumnsOrderRearranged = mtclmWithAuditColumn.select(mtclmWithAuditColumn("clm_adjstmnt_key"), mtclmWithAuditColumn("mbrshp_sor_cd"), mtclmWithAuditColumn("clm_its_host_cd"), mtclmWithAuditColumn("srvc_rndrg_type_cd"), mtclmWithAuditColumn("src_prov_natl_prov_id"), mtclmWithAuditColumn("src_billg_tax_id"), mtclmWithAuditColumn("mbr_key"), mtclmWithAuditColumn("rx_filled_dt"), mtclmWithAuditColumn("gl_post_dt"), mtclmWithAuditColumn("admt_dt"), mtclmWithAuditColumn(lastUpdatedDate), mtclmWithAuditColumn("adjdctn_dt"), mtclmWithAuditColumn("clm_sor_cd"))

			clmDF.unpersist()
			clmLineDF.unpersist()
			clmPaidDF.unpersist()
			fcltyClmDF.unpersist()
			clmLineCoaDF.unpersist()
			clmJoinClmLineDF.unpersist()
			clmJoinClmLineJoinclmPaidDF.unpersist()
			mtclmDF.unpersist()
			mtclmWithAuditColumn.unpersist()

			//Processing the data using joins for MTCLM_COA
			println("Processing the data using joins for MTCLM_COA")
			val clmLineFilterdDF = clmLineDF.filter(clmLineDF("clm_line_encntr_cd").isin("N", "NA") && clmLineDF("clm_line_stts_cd").isin("PD", "APRVD", "PDZB", "APRZB", "VOID") && (clmLineDF("hlth_srvc_type_cd").notEqual("ADA")))
			val clmLineCoaFilterdDF = clmLineCoaDF.filter(((substring(clmLineCoaDF("prod_cf_cd"), 1, 1).isin("M", "P", "B")) || (clmLineCoaDF("prod_cf_cd").isin("TPPMC", "TADVZ", "TAOPZ"))) && ((clmLineCoaDF("prod_cf_cd").isin("BSEAT", "BSVEA") === false)))

			val coaClmJoinclmLineDF = clmDF.join(clmLineFilterdDF, clmDF("clm_adjstmnt_key") === clmLineFilterdDF("clm_adjstmnt_key") && clmDF("adjdctn_dt") === clmLineFilterdDF("adjdctn_dt") && clmDF("clm_sor_cd") === clmLineFilterdDF("clm_sor_cd"), "inner").select(clmDF("clm_adjstmnt_key"), clmLineFilterdDF("clm_line_nbr"), clmLineFilterdDF("inpat_cd"), clmLineFilterdDF("clm_line_encntr_cd"), clmLineFilterdDF("clm_line_stts_cd"), clmLineFilterdDF("hlth_srvc_type_cd"), clmDF("clm_sor_cd"), clmDF("adjdctn_dt"))
			val mtclmCoaDF = coaClmJoinclmLineDF.join(clmLineCoaFilterdDF, coaClmJoinclmLineDF("clm_adjstmnt_key") === clmLineCoaFilterdDF("clm_adjstmnt_key") && coaClmJoinclmLineDF("clm_line_nbr") === clmLineCoaFilterdDF("clm_line_nbr") && coaClmJoinclmLineDF("clm_sor_cd") === clmLineCoaFilterdDF("clm_sor_cd"), "inner").select(coaClmJoinclmLineDF("clm_adjstmnt_key"), coaClmJoinclmLineDF("clm_line_nbr"), coaClmJoinclmLineDF("inpat_cd"), clmLineCoaFilterdDF("mbu_cf_cd"), clmLineCoaFilterdDF("prod_cf_cd"), clmLineCoaFilterdDF("cmpny_cf_cd"), coaClmJoinclmLineDF("clm_line_encntr_cd"), coaClmJoinclmLineDF("clm_line_stts_cd"), coaClmJoinclmLineDF("hlth_srvc_type_cd"), coaClmJoinclmLineDF("clm_sor_cd"), coaClmJoinclmLineDF("adjdctn_dt")).distinct
			val mtclmCoaWithAuditColumn = mtclmCoaDF.withColumn(lastUpdatedDate, lit(current_timestamp()))
			val mtclmCoaColumnsOrderRearranged = mtclmCoaWithAuditColumn.select(mtclmCoaWithAuditColumn("clm_adjstmnt_key"), mtclmCoaWithAuditColumn("clm_line_nbr"), mtclmCoaWithAuditColumn("inpat_cd"), mtclmCoaWithAuditColumn("mbu_cf_cd"), mtclmCoaWithAuditColumn("prod_cf_cd"), mtclmCoaWithAuditColumn("cmpny_cf_cd"), mtclmCoaWithAuditColumn("clm_line_encntr_cd"), mtclmCoaWithAuditColumn("clm_line_stts_cd"), mtclmCoaWithAuditColumn("hlth_srvc_type_cd"), mtclmCoaWithAuditColumn(lastUpdatedDate), mtclmCoaWithAuditColumn("adjdctn_dt"), mtclmCoaWithAuditColumn("clm_sor_cd"))

			clmLineFilterdDF.unpersist()
			clmLineCoaFilterdDF.unpersist()
			coaClmJoinclmLineDF.unpersist()
			mtclmCoaDF.unpersist()
			mtclmCoaWithAuditColumn.unpersist()

			//Creating a map of data frames
			println("Creating a map of data frames")
			val targetTableNameMtclm = config.getString("mtclm_target_table").toLowerCase()
			val targetTableNameMtclmCoa = config.getString("mtclm_coa_target_table").toLowerCase()
			val outMapDF = Map(targetTableNameMtclm -> mtclmColumnsOrderRearranged, targetTableNameMtclmCoa -> mtclmCoaColumnsOrderRearranged)

			outMapDF

	}

	def writeData(map: Map[String, DataFrame]): Unit = {

			//Writing the data to a table in Hive
			println("Writing the data to a table in Hive")
			//Looping for map of data frames
			println("Looping for map of data frames")

			map.foreach(x => {
				println("-------Inside For loop--------")

				val hiveDB = config.getString("inbound-hive-db")
				val warehouseHiveDB = config.getString("warehouse-hive-db")
				println("hiveDB is " + hiveDB)
				println("warehouseHiveDB is " + warehouseHiveDB)

				val tablename = x._1
				println("tablename is" + tablename)

				//Displaying the sample of data 
				val df = x._2
				printf("Showing the contents of df")
				df.printSchema()
				df.show(false)

				//Truncating the previous table created
				println("Truncating the previous table created")
				hiveContext.sql("ALTER TABLE " + warehouseHiveDB + """.""" + tablename + " SET TBLPROPERTIES('EXTERNAL'='FALSE')")
				hiveContext.sql("truncate table " + warehouseHiveDB + """.""" + tablename)

				val partitionColumn1 = config.getString("mtclm_partition_col1").toLowerCase()
				val partitionColumn2 = config.getString("mtclm_partition_col2").toLowerCase()

				//Creating the table in Hive
				hiveContext.setConf("hive.exec.dynamic.partition", "true")
				hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")
				df.write.mode("overwrite").partitionBy(partitionColumn1).insertInto(warehouseHiveDB + """.""" + tablename)
				hiveContext.sql("ALTER TABLE " + warehouseHiveDB + """.""" + tablename + " SET TBLPROPERTIES('EXTERNAL'='TRUE')")
				println("Table created as " + tablename)

			})
			varianceCalculation(map)
	}

	def varianceCalculation(targetTableDFs: Map[String, DataFrame]): Unit = {
			//Calculating the variance percentage of row count with the previous count
			println("Inside variance calculation")

			targetTableDFs.foreach(x => {

				//Looping for all the map of dataframes
				println("Inside for loop")

				//Reading the required variables
				val warehouseHiveDB = config.getString("warehouse-hive-db")
				val tablename = x._1
				println("warehouseDb is " + warehouseHiveDB)
				println("tablename is" + tablename)
				val varianceTableName = config.getString("variance_table").toLowerCase
				val vdfcurrent = targetTableDFs.getOrElse(tablename, null)

				var previous_variance_data_query = ""

				//Reading the previous data query depending on the table name
				if (tablename.equalsIgnoreCase("mtclm")) {

					//Target table is MTCLM
					println("---The target table is MTCLM----")
					previous_variance_data_query = config.getString("previous_variance_data_QUERY_MTCLM")
					println(s"Data load query is  previous_variance_data_query_mtclm" + previous_variance_data_query)

				} else {

					//Target table is MTCLM_COA
					println("---The target table is MTCLM_COA----")
					previous_variance_data_query = config.getString("previous_variance_data_QUERY_MTCLM_COA")
					println(s"Data load query is  previous_variance_data_query_mtclm_coa" + previous_variance_data_query)

				}

				//Checking if the threshold is crossed for the variance
				val check_threshold = (x: Double, y: Double) => { if (x > y.toDouble) "true" else "false" }
				val prvVarDF = hiveContext.sql(previous_variance_data_query)

						import hiveContext.implicits._

						//Getting the record count fot the current data load
						val varianceCount = vdfcurrent.count().toDouble
						println("the record count for the current load is" + varianceCount)

						if (prvVarDF.head(1).isEmpty) {

							//Appending the values to the variance data frame for the first time
							println("Previous count is zero")
							val toBeInserted = Seq((tablename, "NA", "NA", "NA", "row count", "count(*)", varianceCount, 1.0, 5.0, "false"))
							val varianceDF = toBeInserted.map(a => { (MTCLMVarianceSchema(a._1, a._2, a._3, a._4, a._5, a._6, a._7, a._8, a._9, a._10)) }).toDF
							//Inserting into the variance table
							println("Inserting the values to the variance data frame for the first time")
							varianceDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)

							//showing the contents of variance data frame
							varianceDF.show()

						} else {
							//Getting the previous record count from the dataset
							val prvCount = prvVarDF.map(f => f.get(6)).first().toString().toDouble
									//	val prvCount = (i:Int)=>prvVarDF.rdd.zipWithIndex.filter(_._2==i).map(_._1).first().toString().split(",")(6)
									println("The old record count is " + prvCount)

									//Appending the values to the variance data frame
									val varianceDF = prvVarDF.map(f => MTCLMVarianceSchema(tablename, "NA", "NA", "NA", "row count", "count(*)", varianceCount, (((varianceCount - prvCount) / prvCount) * 100), 5.0, check_threshold((((varianceCount - prvCount) / varianceCount) * 100), 5.0))).toDF()
									//Inserting into the variance table
									varianceDF.withColumn("last_updt_dtm", lit(current_timestamp())).write.insertInto(warehouseHiveDB + """.""" + varianceTableName)

									//showing the contents of variance data frame
									varianceDF.show()

						}
			})
	}

}