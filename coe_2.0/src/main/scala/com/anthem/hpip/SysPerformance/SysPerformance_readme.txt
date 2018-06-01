===============================================================
************* Chapter 1 - Create Random Table *****************
===============================================================
Section 1 - Run Template on local

Step 1 - Create the following tables:
  ../ddl/dev/SysPerformanceTable.txt

 	table: dv_pdpspfiph_nogbd_r000_wh.zz_random_util
 	id_key = int 
	random_string = string 
 
 Step 2 - Run program to load random UUID String
 
 	com.anthem.hpip.SysPerformance.createLoadRandomData.createLoadRandomDataDriver
 	Parameter: C:\java\git\repos\coe_2.0\conf local TemplateLocal
 	
===============================================================
Section 2 - Run Template on Cluster 
	- Client mode first 
	- cluster mode second
---------------------------------------------------------------

Step 1 - Before to deploy to cluster: 
	*** reverse back the code in com.anthem.hpip.config.Spark2Config
	*** Forget to do this will cause mode conflict issue and fail on --deploymode cluster without explict Error

---------------------------------------------------------------
Step 2 - Maven build 
	pom.xml -> Maven clean
	pom.xml -> Maven test
    pom.xml -> Maven Install

---------------------------------------------------------------

Step 3 - FTP CenterOfExcellence-2.0.0.jar
From:
    ../target/CenterOfExcellence-2.0.0.jar
To:
    /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar

Push to HDFS
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
 
---------------------------------------------------------------
Step 4 - FTP query_smartProvider.properties
From:
../conf/query_smartProviderCoe.properties
../conf/log4j.xml
../conf/application_dev_coe.properties

To:
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties

Push to HDFS:
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/

---------------------------------------------------------------

Step 5 - Run client Mode:
(disable writing HIVE in com.anthem.hpip.SysPerformance.createLoadRandomData first then enable it)
 
 cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
spark2-submit \
--master yarn \
--deploy-mode client \
--name SysPerfTest-Cluster-mode \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 50 \
--executor-memory 20G \
--executor-cores 4 \
--conf spark.ui.port=5052 \
--conf spark.yarn.maxAppAttempts=1 \
--conf spark.yarn.driver.memoryOverhead=2048 \
--conf spark.yarn.executor.memoryOverhead=9096 \
--conf spark.network.timeout=800 \
--conf spark.driver.maxResultSize=0 \
--conf spark.kryoserializer.buffer.max=1024m \
--conf spark.rpc.message.maxSize=1024 \
--conf spark.sql.broadcastTimeout=4800 \
--conf spark.executor.heartbeatInterval=30s \
--conf spark.dynamicAllocation.executorIdleTimeout=180 \
--conf spark.dynamicAllocation.initialExecutors=30 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=30 \
--jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.SysPerformance.createLoadRandomData.createLoadRandomDataDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe

---------------------------------------------------
	- this works but => give error message as af35352 is not the owner to create and load data = But still worked
 
Step 6 - Run cluster Mode with HDFS:
(disable writing HIVE in com.anthem.hpip.SysPerformance.createLoadRandomData first then enable it)
 spark2-submit \
--master yarn \
--deploy-mode cluster \
--name SysPerfTest-Cluster-mode \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 50 \
--executor-memory 20G \
--executor-cores 4 \
--conf spark.ui.port=5052 \
--conf spark.yarn.maxAppAttempts=1 \
--conf spark.yarn.driver.memoryOverhead=2048 \
--conf spark.yarn.executor.memoryOverhead=9096 \
--conf spark.network.timeout=800 \
--conf spark.driver.maxResultSize=0 \
--conf spark.kryoserializer.buffer.max=1024m \
--conf spark.rpc.message.maxSize=1024 \
--conf spark.sql.broadcastTimeout=4800 \
--conf spark.executor.heartbeatInterval=30s \
--conf spark.dynamicAllocation.executorIdleTimeout=180 \
--conf spark.dynamicAllocation.initialExecutors=30 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=30 \
--jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.SysPerformance.createLoadRandomData.createLoadRandomDataDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe
 
 ===========================================================================
************* Chapter 2 - Create Sample Tables for testing *****************
============================================================================

Section 1 - Logic
-----------------------------------------------
Step 1 - Create three table with data:
	- id_key (incremental)
	- random_number (1000 with 500 range = have duplicates)
	- random_string (1000 with 500 range query from random table )

	zz_srctb_a
	zz_srctb_b

  ../ddl/dev/SysPerformanceTable.txt

Step 2 - create Stage table:
	- uuid_key (uuid)
	- srctb_a_id 
	- srctb_b_id
	- ab_matching_number
	- a_string
	- b_string

zz_tgttb_c
	
	This table come from joining between srctb_a and srctb_b
	- both need group by random_number first => use first to get unique id_key
	- joing based on random_number

------------------------------------------

Section 2 - Spark job to prepare src tables = zz_srctb_a & zz_srctb_b
(This job will also create random util table - providing uuid randomly select within a list)

 Step 1 - Run program to load Source tables
 
 	com.anthem.hpip.SysPerformance.createTestingTable.createTestingTableDriver
 	Parameter: C:\java\git\repos\coe_2.0\conf local TemplateLocal

	
===============================================================
Section 3 - Run Template on Cluster 
	- Client mode first 
	- cluster mode second
---------------------------------------------------------------

Step 1 - Before to deploy to cluster: 
	*** reverse back the code in com.anthem.hpip.config.Spark2Config
	*** Forget to do this will cause mode conflict issue and fail on --deploymode cluster without explict Error
	*** enable hive writing code 

---------------------------------------------------------------
Step 2 - Maven build 
	pom.xml -> Maven clean
	pom.xml -> Maven test
    pom.xml -> Maven Install

---------------------------------------------------------------

Step 3 - FTP CenterOfExcellence-2.0.0.jar
From:
    ../target/CenterOfExcellence-2.0.0.jar
To:
    /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar

Push to HDFS
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
 
---------------------------------------------------------------
Step 4 - FTP query_smartProvider.properties
From:
../conf/query_smartProviderCoe.properties
../conf/log4j.xml
../conf/application_dev_coe.properties

To:
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties

Push to HDFS:
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/

---------------------------------------------------------------

Step 5 - Run client Mode:
(disable writing HIVE in com.anthem.hpip.SysPerformance.createLoadRandomData first then enable it)
 
 cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
spark2-submit \
--master yarn \
--deploy-mode client \
--name SysPerfTest-createTestTable-client-mode \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 50 \
--executor-memory 20G \
--executor-cores 4 \
--conf spark.ui.port=5052 \
--conf spark.yarn.maxAppAttempts=1 \
--conf spark.yarn.driver.memoryOverhead=2048 \
--conf spark.yarn.executor.memoryOverhead=9096 \
--conf spark.network.timeout=800 \
--conf spark.driver.maxResultSize=0 \
--conf spark.kryoserializer.buffer.max=1024m \
--conf spark.rpc.message.maxSize=1024 \
--conf spark.sql.broadcastTimeout=4800 \
--conf spark.executor.heartbeatInterval=30s \
--conf spark.dynamicAllocation.executorIdleTimeout=180 \
--conf spark.dynamicAllocation.initialExecutors=30 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=30 \
--jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.SysPerformance.createTestingTable.createTestingTableDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe

---------------------------------------------------
	- this works but => give error message as af35352 is not the owner to create and load data = But still worked
 
Step 6 - Run cluster Mode with HDFS:
(disable writing HIVE in com.anthem.hpip.SysPerformance.createTestTable first then enable it)
 spark2-submit \
--master yarn \
--deploy-mode cluster \
--name SysPerfTest-createTestTable-Cluster-mode \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 50 \
--executor-memory 20G \
--executor-cores 4 \
--conf spark.ui.port=5052 \
--conf spark.yarn.maxAppAttempts=1 \
--conf spark.yarn.driver.memoryOverhead=2048 \
--conf spark.yarn.executor.memoryOverhead=9096 \
--conf spark.network.timeout=800 \
--conf spark.driver.maxResultSize=0 \
--conf spark.kryoserializer.buffer.max=1024m \
--conf spark.rpc.message.maxSize=1024 \
--conf spark.sql.broadcastTimeout=4800 \
--conf spark.executor.heartbeatInterval=30s \
--conf spark.dynamicAllocation.executorIdleTimeout=180 \
--conf spark.dynamicAllocation.initialExecutors=30 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=30 \
--jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.SysPerformance.createTestingTable.createTestingTableDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe
 
 
===========================================================================
************* Chapter 3 - Create HSQL solution  *****************
============================================================================
Step 0 - What operation we are going to perform

	Operation 1 - Distinct the source table A & B
	Operation 2 - Select the matching UUID string between table A & Table B 
		- Find the first match if there is duplicate on both side
	Operation 3 - Output the matched info into the target table

==============================================
Section 1 - Create the HSQL solution  
==============================================

Step 1 - HSQL direct solution
C:\java\git\repos\coe_2.0\src\main\scala\com\anthem\hpip\SysPerformance\SparkSQLSolution\hsql_count_row.sql

FTP to:
	/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl

Run the first test:
	cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl/
	beeline -u "jdbc:hive2://dwbdtest1r2m3.wellpoint.com:2181,dwbdtest1r1m.wellpoint.com:2181,dwbdtest1r2m.wellpoint.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2"  -f  /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl/hsql_count_row.sql > /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl//hsql_count_result.txt

Result file:
	/data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl/hsql_count_result.txt
	
Step 2 - Create SparkSQL job 

	*** reverse back the code in com.anthem.hpip.config.Spark2Config
	*** Remember to update Pom.xml with the non-provider section 
 
 	com.anthem.hpip.SysPerformance.SparkSQLSolution.SparkSQLSolutionDriver
 	Parameter: C:\java\git\repos\coe_2.0\conf local TemplateLocal
 
 
 
===============================================================
Section 2 - Run Template on Cluster 
	- Client mode first 
	- cluster mode second
---------------------------------------------------------------

Step 1 - Before to deploy to cluster: 
	*** reverse back the code in com.anthem.hpip.config.Spark2Config
	*** Forget to do this will cause mode conflict issue and fail on --deploymode cluster without explict Error

---------------------------------------------------------------
Step 2 - Maven build 
	pom.xml -> Maven clean
	pom.xml -> Maven test
    pom.xml -> Maven Install

---------------------------------------------------------------

Step 3 - FTP CenterOfExcellence-2.0.0.jar
From:
    ../target/CenterOfExcellence-2.0.0.jar
To:
    /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar

Push to HDFS
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
 
---------------------------------------------------------------
Step 4 - FTP query_smartProvider.properties
From:
../conf/query_smartProviderCoe.properties
../conf/log4j.xml
../conf/application_dev_coe.properties

To:
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties

Push to HDFS:
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/

---------------------------------------------------------------

Step 5 - Run client Mode:
(disable writing HIVE in com.anthem.hpip.SysPerformance.createLoadRandomData first then enable it)
 
(when running on client mode = some time failed because of permission issue = use cluster mode - not working = permission change caused by hadoop admin) 
 cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
spark2-submit \
--master yarn \
--deploy-mode client \
--name SysPerfTest-Cluster-mode \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 50 \
--executor-memory 20G \
--executor-cores 4 \
--conf spark.ui.port=5052 \
--conf spark.yarn.maxAppAttempts=1 \
--conf spark.yarn.driver.memoryOverhead=2048 \
--conf spark.yarn.executor.memoryOverhead=9096 \
--conf spark.network.timeout=800 \
--conf spark.driver.maxResultSize=0 \
--conf spark.kryoserializer.buffer.max=1024m \
--conf spark.rpc.message.maxSize=1024 \
--conf spark.sql.broadcastTimeout=4800 \
--conf spark.executor.heartbeatInterval=30s \
--conf spark.dynamicAllocation.executorIdleTimeout=180 \
--conf spark.dynamicAllocation.initialExecutors=30 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=30 \
--jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.SysPerformance.SparkSQLSolution.SparkSQLSolutionDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe
 
===========================================================================
************* Chapter 4 - Create DataFrame solution  *****************
============================================================================
 Note: This cannot be run on local = it is reading from the DB 
 
 ===============================================================
Section 1 - Run Template on Cluster 
	- Client mode first 
	- cluster mode second
---------------------------------------------------------------

Step 1 - Before to deploy to cluster: 
	*** reverse back the code in com.anthem.hpip.config.Spark2Config
	*** Forget to do this will cause mode conflict issue and fail on --deploymode cluster without explict Error

---------------------------------------------------------------
Step 2 - Maven build 
	pom.xml -> Maven clean
	pom.xml -> Maven test
    pom.xml -> Maven Install

---------------------------------------------------------------

Step 3 - FTP CenterOfExcellence-2.0.0.jar
From:
    ../target/CenterOfExcellence-2.0.0.jar
To:
    /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar

Push to HDFS
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
 
---------------------------------------------------------------
Step 4 - FTP query_smartProvider.properties
From:
../conf/query_smartProviderCoe.properties
../conf/log4j.xml
../conf/application_dev_coe.properties

To:
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties

Push to HDFS:
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/

---------------------------------------------------------------

Step 5 - Run client Mode:
(disable writing HIVE in com.anthem.hpip.SysPerformance.createLoadRandomData first then enable it)
 
 cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
spark2-submit \
--master yarn \
--deploy-mode client \
--name SysPerfTest-Cluster-mode \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 50 \
--executor-memory 20G \
--executor-cores 4 \
--conf spark.ui.port=5052 \
--conf spark.yarn.maxAppAttempts=1 \
--conf spark.yarn.driver.memoryOverhead=2048 \
--conf spark.yarn.executor.memoryOverhead=9096 \
--conf spark.network.timeout=800 \
--conf spark.driver.maxResultSize=0 \
--conf spark.kryoserializer.buffer.max=1024m \
--conf spark.rpc.message.maxSize=1024 \
--conf spark.sql.broadcastTimeout=4800 \
--conf spark.executor.heartbeatInterval=30s \
--conf spark.dynamicAllocation.executorIdleTimeout=180 \
--conf spark.dynamicAllocation.initialExecutors=30 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=30 \
--jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.SysPerformance.SparkDataFrameSolution.SparkDataFrameSolutionDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe
 
 
 ================================================================
===========================================================================
************* Chapter 5 - Increase load for testing  *****************
============================================================================

Step 1 - HSQL direct solution
C:\java\git\repos\coe_2.0\src\main\scala\com\anthem\hpip\SysPerformance\SparkDataFrameSolution\InsertMoreData.sql

FTP to:
	/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl

Run the first test:
	cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl/
	beeline -u "jdbc:hive2://dwbdtest1r2m3.wellpoint.com:2181,dwbdtest1r1m.wellpoint.com:2181,dwbdtest1r2m.wellpoint.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2"  -f  /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl/InsertMoreData.sql > /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl//InsertMoreData_result.txt

Result file:
	/data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/etl/hsql_count_result.txt
	

===================================================================

Driver logs can be check at cluster:
Driver logs
https://dwbdtest1r1m.wellpoint.com:8090/cluster/app/application_1527038069742_10176
https://dwbdtest1r1m.wellpoint.com:8090/cluster/app/application_1527038069742_14673

Executor logs can be check at Spark UI:
Executor logs - Spark Top level
https://dwbdtest1r1e.wellpoint.com:8888/jobbrowser/

Spark Ui Detail Level
https://dwbdtest1r2m3.wellpoint.com:18489/history/application_1527038069742_10176/1/

===========================================================

select * from dv_pdpspfiph_nogbd_r000_wh.zz_random_util limit 100;

select count(*) from dv_pdpspfiph_nogbd_r000_wh.zz_random_util;
select count(*) from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a;
select count(*) from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_b;

select * from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a limit 500;
select * from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_b limit 500;




 