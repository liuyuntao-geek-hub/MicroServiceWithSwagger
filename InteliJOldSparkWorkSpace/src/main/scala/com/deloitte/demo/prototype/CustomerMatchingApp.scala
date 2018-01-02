package com.deloitte.demo.prototype

/**
  * Created by yuntliu on 12/5/2017.
  */

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.commons.lang3.StringUtils
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.rdd.NewHadoopRDD
import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor}
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.HTable


object CustomerMatchingApp {

  // Define Priority of 3 input systems
  def getPriority(systems: Array[String]) : String = {
    if (systems.length == 0 || systems.contains("Triumph") || systems.contains("triumph") )
      return "Triumph";
    else if (systems.contains("Debt Manager") || systems.contains("debt manager") )
      return "Debt Manager";
    else return "UKBA";
  }

  //TODO Check if all the arguments are as expected
  /*
  def validateArgs (args: Array[String]) : Boolean = {
    //Check that # of arguments are per number of files and columns
    //Number of files is between 1 and 3
    //Check input files / input file location is correct
    //Check that column exists
    //Check weightage total is 1 for all columns
    if (args.length == 0)
      return false;
    else if ()
  }*/



  case class TableStructure(source_rowID: String, first_name: String, last_name: String, street: String, city: String, state: String, zip: String, phone: String, email: String, Source: String, rowID: String, clusterNumber: String)

  def main(args: Array[String]) {

    //Setup Application

    val conf = new SparkConf().setAppName("Customer Matching App")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._


    //TODO Validate arguments passed to the program
    //val argValidation = validateArgs(args)

    //-------------------LOAD INPUT FILE------------------
    val  schema_string = "rowID, source, source_rowID,first_name,last_name,street,city,state,zip,phone,email"
    val schema_rdd = StructType(schema_string.split(",").map(fieldName => StructField(fieldName, StringType, true)) )
    var combinedDF = sqlContext.createDataFrame(sc.emptyRDD[Row], schema_rdd)

    val numOfFiles = args(0).toInt
    val numOfCols = args(1).toInt
    val colArgStart = (numOfFiles*2) + 2

    /* ...TEST DATA  for input files...
     *
    val source1Loc = "/user/cloudera/CustomerMDM/data_debt_manager.csv"
    val source1Name = "Debt Manager"
    val source2Loc = "/user/cloudera/CustomerMDM/data_triumph.csmonotonicallyIncreasingIdv"
    val source2Name = "Triumph"
    val source3Loc = "/user/cloudera/CustomerMDM/data_ukba.csv"
    val source3Name = "UKBA"
    *
    */
    if (numOfFiles == 1)
    {
      val source1Name = args(2)
      val source1Loc = args(3)

      val inputDF1 = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "true") // Use first line of all files as header
        .option("inferSchema", "true") // Automatically infer data types
        .load(source1Loc)
      val df1 = inputDF1.withColumn("source", lit(source1Name))

      val dfAll = df1
      combinedDF = dfAll.withColumn("rowID", monotonicallyIncreasingId)
    } else if (numOfFiles == 2)
    {
      val source1Name = args(2)
      val source1Loc = args(3)
      val source2Name = args(4)
      val source2Loc = args(5)

      val inputDF1 = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "true") // Use first line of all files as header
        .option("inferSchema", "true") // Automatically infer data types
        .load(source1Loc)
      val df1 = inputDF1.withColumn("source", lit(source1Name))
      val inputDF2 = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "true") // Use first line of all files as header
        .option("inferSchema", "true") // Automatically infer data types
        .load(source2Loc)
      val df2 = inputDF2.withColumn("source", lit(source2Name))

      val dfAll = df1.unionAll(df2)
      combinedDF = dfAll.withColumn("rowID", monotonicallyIncreasingId)
    } else if (numOfFiles == 3)
    {
      val source1Name = args(2)
      val source1Loc = args(3)
      val source2Name = args(4)
      val source2Loc = args(5)
      val source3Name = args(6)
      val source3Loc = args(7)

      val inputDF1 = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "true") // Use first line of all files as header
        .option("inferSchema", "true") // Automatically infer data types
        .load(source1Loc)
      val df1 = inputDF1.withColumn("source", lit(source1Name))
      val inputDF2 = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "true") // Use first line of all files as header
        .option("inferSchema", "true") // Automatically infer data types
        .load(source2Loc)
      val df2 = inputDF2.withColumn("source", lit(source2Name))
      val inputDF3 = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "true") // Use first line of all files as header
        .option("inferSchema", "true") // Automatically infer data types
        .load(source3Loc)
      val df3 = inputDF3.withColumn("source", lit(source3Name))

      val dfAll = df1.unionAll(df2).unionAll(df3)
      combinedDF = dfAll.withColumn("rowID", monotonicallyIncreasingId)
    } else
    {
      //TODO End Program
    }
    //-----------------INPUT FILE LOADED------------------

    //-----------------CALCULATE DISTANCE BETWEEN IDENTIFIED COLUMNS FOR ALL ROWS TO PREPARE FOR CLUSTERING------------------

    /* ...TEST DATA for column names and weightage...
       *
      val col1Name = "email"
      val col1Weight = 0.4
      val col2Name = "phone"
      val col2Weight = 0.4
      val col3Name = "street"
      val col3Weight = 0.2
      *
    */


    var custDataWeightedColDistances = Array[(Long, Long, Double)]()
    if (numOfCols == 1)
    {

      val col1Name = args(colArgStart)
      val col1Weight = args(colArgStart+1).toDouble

      //Convert DataFrame to RDD (with RowID-Col1 columns)
      val custDataRowsForClusters = combinedDF.map(row => (row.getLong(row.fieldIndex("rowID")), row.get(row.fieldIndex(col1Name))))

      //Iterate through each row; compare distance of col1 for a row with all other rows; Using JaroWinklerDistance
      var i = 0
      var j = i + 1

      var custDataColDistancesBuffer = ArrayBuffer[(Long, Long, Double)]() //Row1ID - Row2ID - StringDistance_Col1
      for( i <- 0 to (custDataRowsForClusters.count().toInt - 2); j <- i+1 to (custDataRowsForClusters.count().toInt - 1)){
        var rowi = custDataRowsForClusters.collect()(i)
        var rowj = custDataRowsForClusters.collect()(j)
        custDataColDistancesBuffer += ((rowi._1, rowj._1,
          StringUtils.getJaroWinklerDistance(rowi._2.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._2.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""))))
      }

      // Output: Row1ID - Row2ID - StringDistance_Col1
      val custDataColDistances = custDataColDistancesBuffer.toArray

      //Calculate weighted average - 100% for col1
      var custDataColDistancesBuffer2 = ArrayBuffer[(Long, Long, Double)]() //Row1ID - Row2ID - WeightedAverageDistance
      for (eachElement <- custDataColDistances) {
        custDataColDistancesBuffer2 += ((eachElement._1, eachElement._2, (eachElement._3 *col1Weight)))
      }

      // Output: Row1ID - Row2ID - WeightedAverageDistance
      custDataWeightedColDistances = custDataColDistancesBuffer2.toArray

    } else if (numOfCols == 2)
    {

      val col1Name = args(colArgStart)
      val col1Weight = args(colArgStart+1).toDouble
      val col2Name = args(colArgStart+2)
      val col2Weight = args(colArgStart+3).toDouble

      //Convert DataFrame to RDD (with RowID-Col1-Col2 columns)
      val custDataRowsForClusters = combinedDF.map(row => (row.getLong(row.fieldIndex("rowID")), row.get(row.fieldIndex(col1Name)), row.get(row.fieldIndex(col2Name))))

      //Iterate through each row; compare distance for col1 and col2 for a row with all other rows; Using JaroWinklerDistance
      var i = 0
      var j = i + 1

      var custDataColDistancesBuffer = ArrayBuffer[(Long, Long, Double, Double)]() //Row1ID - Row2ID - StringDistance_Col1 - StringDistance_Col2
      for( i <- 0 to (custDataRowsForClusters.count().toInt - 2); j <- i+1 to (custDataRowsForClusters.count().toInt - 1)){
        var rowi = custDataRowsForClusters.collect()(i)
        var rowj = custDataRowsForClusters.collect()(j)
        custDataColDistancesBuffer += ((rowi._1, rowj._1,
          StringUtils.getJaroWinklerDistance(rowi._2.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._2.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", "")),
          StringUtils.getJaroWinklerDistance(rowi._3.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._3.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""))))
      }

      // Output: Row1ID - Row2ID - StringDistance_Col1 - StringDistance_Col2
      val custDataColDistances = custDataColDistancesBuffer.toArray

      //Calculate weighted average - each for col1 and col2
      var custDataColDistancesBuffer2 = ArrayBuffer[(Long, Long, Double)]() //Row1ID - Row2ID - WeightedAverageDistance
      for (eachElement <- custDataColDistances) {
        custDataColDistancesBuffer2 += ((eachElement._1, eachElement._2, ((eachElement._3 *col1Weight) + (eachElement._4 *col2Weight))))
      }

      // Output: Row1ID - Row2ID - WeightedAverageDistance
      custDataWeightedColDistances = custDataColDistancesBuffer2.toArray

    }  else if (numOfCols == 3)
    {

      val col1Name = args(colArgStart)
      val col1Weight = args(colArgStart+1).toDouble
      val col2Name = args(colArgStart+2)
      val col2Weight = args(colArgStart+3).toDouble
      val col3Name = args(colArgStart+4)
      val col3Weight = args(colArgStart+5).toDouble

      //Convert DataFrame to RDD (with RowID-Col1-Col2 columns)
      val custDataRowsForClusters = combinedDF.map(row => (row.getLong(row.fieldIndex("rowID")), row.get(row.fieldIndex(col1Name)), row.get(row.fieldIndex(col2Name)), row.get(row.fieldIndex(col3Name))))

      //Iterate through each row; compare distance for col1 and col2 for a row with all other rows; Using JaroWinklerDistance
      var i = 0
      var j = i + 1

      var custDataColDistancesBuffer = ArrayBuffer[(Long, Long, Double, Double, Double)]() //Row1ID - Row2ID - StringDistance_Col1 - StringDistance_Col2 - StringDistance_Col3
      for( i <- 0 to (custDataRowsForClusters.count().toInt - 2); j <- i+1 to (custDataRowsForClusters.count().toInt - 1)){
        var rowi = custDataRowsForClusters.collect()(i)
        var rowj = custDataRowsForClusters.collect()(j)
        custDataColDistancesBuffer += ((rowi._1, rowj._1,
          StringUtils.getJaroWinklerDistance(rowi._2.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._2.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", "")),
          StringUtils.getJaroWinklerDistance(rowi._3.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._3.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", "")),
          StringUtils.getJaroWinklerDistance(rowi._4.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""), rowj._4.toString.replaceAll("-", "").replaceAll(" ", "").replaceAll("@", "").replaceAll("""\.""", ""))))
      }

      // Output: Row1ID - Row2ID - StringDistance_Col1 - StringDistance_Col2
      val custDataColDistances = custDataColDistancesBuffer.toArray

      //Calculate weighted average - each for col1 and col2
      var custDataColDistancesBuffer2 = ArrayBuffer[(Long, Long, Double)]() //Row1ID - Row2ID - WeightedAverageDistance
      for (eachElement <- custDataColDistances) {
        custDataColDistancesBuffer2 += ((eachElement._1, eachElement._2, ((eachElement._3 *col1Weight) + (eachElement._4 *col2Weight)  + (eachElement._5 *col3Weight))))
      }

      // Output: Row1ID - Row2ID - WeightedAverageDistance
      custDataWeightedColDistances = custDataColDistancesBuffer2.toArray
    } else
    {
      //TODO End Program
    }
    //-----------------DISTANCE CALCULATED------------------


    //-------------------CREATE CLUSTERS--------------------
    val jaroWinklerDistThreshold =0.85
    var custClusterBuffer = ArrayBuffer[(Long, Long)]()  //ClusterNumber - RowID
    var custAlreadyAllocatedBuffer = ArrayBuffer[Long]() //RowID
    var currentRow1ID : Long = 0
    var currentRow2ID : Long = 0
    var weightedDistanceScore = 0.0
    var clusterNumber : Long = 0
    var clusterNumberIndex = 0
    var NewCluster : Long = 0
    for (eachElement <- custDataWeightedColDistances) {
      currentRow1ID = eachElement._1
      currentRow2ID = eachElement._2
      weightedDistanceScore = eachElement._3
      //Check 1: If the Row1ID of the element is not allocated to a cluster then create a new cluster and add the Row1ID to that cluster
      if (!custAlreadyAllocatedBuffer.exists(_ == currentRow1ID)) {
        NewCluster += 1
        clusterNumber = NewCluster
        custClusterBuffer.append((clusterNumber, currentRow1ID))
        custAlreadyAllocatedBuffer.append(currentRow1ID)
      } else	{
        // Get the existing cluster of Row1ID
        clusterNumberIndex = custAlreadyAllocatedBuffer.indexOf(currentRow1ID)
        clusterNumber = custClusterBuffer(clusterNumberIndex)._1
      }

      //Check 2: If the distance score is above threshold and Row2ID of the element is not allocated to a cluster then add the Row2ID to that cluster
      if ((weightedDistanceScore >= jaroWinklerDistThreshold) && (!custAlreadyAllocatedBuffer.exists(_ == currentRow2ID))) {
        custClusterBuffer.append((clusterNumber, currentRow2ID))
        custAlreadyAllocatedBuffer.append(currentRow2ID)
      }

      //Check 3: On the last element, if Row2ID of the element is not allocated to a cluster then create a new cluster and add the Row2ID to that cluster
      if (eachElement == custDataWeightedColDistances.last && (!custAlreadyAllocatedBuffer.exists(_ == currentRow2ID)))	{
        NewCluster+=1
        clusterNumber = NewCluster
        custClusterBuffer.append((clusterNumber, currentRow2ID))
        custAlreadyAllocatedBuffer.append(currentRow2ID)
      }
    }
    // Output: ClusterNumber - RowID
    val custClusters = custClusterBuffer.toArray
    val custClustersDF = sc.parallelize(custClusters).toDF("ClusterNumber", "RowID")

    //Add clusterNumber column to data
    val custData = combinedDF.join(custClustersDF, combinedDF.col("rowID").equalTo(custClustersDF("RowID"))).drop(custClustersDF.col("RowID"))

    // Export csv file
    custData.repartition(1)
      .write.format("com.databricks.spark.csv")
      .option("header", "true")
      .save("/user/cloudera/CustomerMDM/Customer_SVC_clusters.csv")

    //------------------------CLUSTERS CREATED------------------------


    //-----------------PREPARE ALL CLUSTERS FOR SURVIVORSHIPS------------------
    val header = custData.columns
    val custDataRows = custData.rdd
    //convert each Row to Array of Strings
    val custDataArray = custDataRows.map { _.toSeq.map {_.toString}.toArray }
    //key-value pairs where key is cluster number and value is the complete row
    val custDataMap = custDataArray.map(row => (row(11), row)) //TODO Column 12 is ClusterNumber
    //Find all unique keys
    val keys = custDataMap.keys.distinct
    //-----------------PREPARED ALL CLUSTERS FOR SURVIVORSHIPS-----------------

    //Survivorship for each cluster (Loop through all clusters)
    var clusterCount = 0
    var finalOutputRowBuffer = ArrayBuffer[Array[String]]()

    for(clusterCount <- 0 to (keys.count().toInt - 1)){
      // Get cluster for the key
      val key = keys.toArray()(clusterCount)
      val custDataEachCluster = custDataMap.lookup(key)
      //Create a map where the key is Source System
      val custDataEachClusterMap = sc.parallelize(custDataEachCluster).map(row => (row(9), row)) //TODO Column 10 is source
      val sourceSystems = custDataEachClusterMap.keys
      //Identify priority system within the source systems of cluster rows
      val prioritySystem = getPriority(sourceSystems.toArray)
      //Get survived row for the priority system

      //TODO: Get the priority of uploaded files.

      val survivedRow = custDataEachClusterMap.lookup(prioritySystem)
      finalOutputRowBuffer += survivedRow(0)
    }
    //Array of all survived rows
    val finalOutputRow = finalOutputRowBuffer.toArray
    val finalOutput = sc.parallelize(finalOutputRow)

    // Export text file
    finalOutput.map(a => a.mkString(",")).repartition(1).saveAsTextFile("/user/cloudera/CustomerMDM/matched_cust")


    /*---------CREATE HBASE TABLE - Code need to be updated per latest Hbase / Cloudera distribution--------------*/
    /*
      //Table Name
      val tableName = "CustomerMaster"

      //Add local HBase conf
      val confHbase = HBaseConfiguration.create()
      confHbase.addResource(new Path("file:///etc/hbase/conf/hbase-site.xml"))
      confHbase.set(TableInputFormat.INPUT_TABLE, tableName)
      val admin = new HBaseAdmin(confHbase)
      //Add column family
      if(!admin.isTableAvailable(tableName)) {
        print("Creating Table")
        val tableDesc = new HTableDescriptor(tableName)
        tableDesc.addFamily(new HColumnDescriptor("goldenrecord".getBytes()));
        admin.createTable(tableDesc)
      }else
      {
        print("Table already exists!!")
        val columnDesc = new HColumnDescriptor("goldenrecord");
        admin.disableTable(Bytes.toBytes(tableName));
        admin.addColumn(tableName, columnDesc);
        admin.enableTable(Bytes.toBytes(tableName));
      }
      val myTable = new HTable(confHbase, tableName);

      // Loop through all rows to create a record in Hbase
      for(rowCount <- 0 to finalOutputRow.length - 1){
        var p = new Put(new String("customer" + finalOutputRow(rowCount)(11)).getBytes()); //11 is Cluster Number column

        p.add("goldenrecord".getBytes(), "source_rowID".getBytes(), new String(finalOutputRow(rowCount)(0)).getBytes());
        p.add("goldenrecord".getBytes(), "first_name".getBytes(), new String(finalOutputRow(rowCount)(1)).getBytes());
        p.add("goldenrecord".getBytes(), "last_name".getBytes(), new String(finalOutputRow(rowCount)(2)).getBytes());
        p.add("goldenrecord".getBytes(), "street".getBytes(), new String(finalOutputRow(rowCount)(3)).getBytes());
        p.add("goldenrecord".getBytes(), "city".getBytes(), new String(finalOutputRow(rowCount)(4)).getBytes());
        p.add("goldenrecord".getBytes(), "state".getBytes(), new String(finalOutputRow(rowCount)(5)).getBytes());
        p.add("goldenrecord".getBytes(), "zip".getBytes(), new String(finalOutputRow(rowCount)(6)).getBytes());
        p.add("goldenrecord".getBytes(), "phone".getBytes(), new String(finalOutputRow(rowCount)(7)).getBytes());
        p.add("goldenrecord".getBytes(), "email".getBytes(), new String(finalOutputRow(rowCount)(8)).getBytes());
        p.add("goldenrecord".getBytes(), "Source".getBytes(), new String(finalOutputRow(rowCount)(9)).getBytes());
        p.add("goldenrecord".getBytes(), "rowID".getBytes(), new String(finalOutputRow(rowCount)(10)).getBytes());
        p.add("goldenrecord".getBytes(), "clusterNumber".getBytes(), new String(finalOutputRow(rowCount)(11)).getBytes());

        myTable.put(p);
      }

      myTable.flushCommits();

      val hBaseRDD = sc.newAPIHadoopRDD(confHbase, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

      //get the row count
      val count = hBaseRDD.count()
      print("HBase RDD count:"+count)

    */
    /*--------------Create Hive Table-------------*/

    {
      val sqlContext = new HiveContext(sc)
      import sqlContext.implicits._
      val outputDF = finalOutput.map{case Array(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11) => TableStructure(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11)}.toDF

      outputDF.write.saveAsTable("Customer_Master")
    }
  }
}
