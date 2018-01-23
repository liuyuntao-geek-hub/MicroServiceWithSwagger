*** This is the package to test local environment and cluster environment ***
*** Only basic test included ***

Section 1 - Run Template on Local
Step 1 - Change the code in com.anthem.hpip.config.Spark2Config 
	- pick up the local run section 
	(in InteliJ need the next step:)
	- pom.xml => change to <scope>compile</scope>
	- pom.xml has a run local Session
Step 1 - Run com.anthem.hpip.Template.TemplateDriver within eClipse with the following Parameter

	C:\java\git\repos\coe_2.0\conf local TemplateLocal

Step 2 - Before to deploy to cluster: 
	*** reverse back the code in com.anthem.hpip.config.Spark2Config
	*** Forget to do this will cause mode conflict issue and fail on --deploymode cluster without explict Error


===============================================================
Section 2 - Run Template on Cluster 
	- Client mode first 
	- cluster mode second
---------------------------------------------------------------
Step 1 - Maven build 
	pom.xml -> Maven clean
	pom.xml -> Maven test
    pom.xml -> Maven Install

---------------------------------------------------------------
Step 2 - FTP data file: testdata_combined.csv
From:
    ../data/testdata_combined.csv
To:
    /dv/data/ve2/pdp/spfi/phi/no_gbd/r000/inbound
hadoop fs -put /dv/data/ve2/pdp/spfi/phi/no_gbd/r000/inbound/testdata_combined.csv /dv/hdfsdata/ve2/pdp/spfi/phi/no_gbd/r000/inbound/


---------------------------------------------------------------
Step 3 - FTP CenterOfExcellence-2.0.0.jar
From:
    ../target/CenterOfExcellence-2.0.0.jar
To:
    /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar

Push to HDFS
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/

***Not needed unless it is missing in the lib *** C:\java\git\repos\COE_ETL\conf\grizzled-slf4j_2.10-1.3.1.jar => /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/jar


---------------------------------------------------------------
Step 4 - FTP query_smartProvider.properties
From:
../conf/query_smartProviderCoe.properties
../conf/log4j.xml
To:
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProvider.properties
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties
Push to HDFS:
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
---------------------------------------------------------------
Step 5 - Run Spark-submit => client mode 
export JAVA_HOME=/usr/java/latest/

**** spark2-shell --conf spark.ui.port=5052

spark2-submit \
--master yarn \
--deploy-mode client \
--name TemplateDriver-Test-SPARK22 \
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
--jars /opt/cloudera/parcels/CDH-5.8.3-1.cdh5.8.3.p2095.2180/jars/hive-contrib-1.1.0-cdh5.8.3.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.Template.TemplateDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe



Step 6 - Run Spark-submit => cluster mode 
export JAVA_HOME=/usr/java/latest/

**** spark2-shell --conf spark.ui.port=5052

spark2-submit \
--master yarn \
--deploy-mode cluster \
--name TemplateDriver-Test-SPARK22 \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 50 \
--executor-memory 20G \
--executor-cores 4 \
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
--conf spark.dynamicAllocation.minExecutors=10 \
--jars /opt/cloudera/parcels/CDH-5.8.3-1.cdh5.8.3.p2095.2180/jars/hive-contrib-1.1.0-cdh5.8.3.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.Template.TemplateDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe

==================================================================
Section 3 - CSV & Hive to Hive
---------------------------------------------------------------
Step 1 - Run ddl 
	-{project path}/ddl/dev/templateRequiredTable.txt
	- These tables are created as internal table
	- No partition specified to simplify the example
	- *** This item need further refactoring
	
Step 2 - The following package cannot run local because it need to access the HIVE DB on cluster

--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver \

Step 3 - Run as client mode:
export JAVA_HOME=/usr/java/latest/
spark2-submit \
--master yarn \
--deploy-mode client \
--name COECSV-HIVE-Test-SPARK22 \
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
--jars /opt/cloudera/parcels/CDH-5.8.3-1.cdh5.8.3.p2095.2180/jars/hive-contrib-1.1.0-cdh5.8.3.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe

Step 4 - Run as cluster mode

--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver 

export JAVA_HOME=/usr/java/latest/
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name COE-CSV-HIVE-HIVE-SPARK22 \
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
--jars /opt/cloudera/parcels/CDH-5.8.3-1.cdh5.8.3.p2095.2180/jars/hive-contrib-1.1.0-cdh5.8.3.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe

Step 5 - Verify the job at Hue:

https://dwbdtest1r1e.wellpoint.com:8888/jobbrowser/

Step 6 - Verify the tables at Hue

-- Source Hive table: 
select * from dv_pdphpipph_nogbd_r000_in.clm  where ADJDCTN_DT BETWEEN '2016-04-01' and '2016-04-02' limit 100;
select count(*) from dv_pdphpipph_nogbd_r000_in.clm  where ADJDCTN_DT BETWEEN '2016-04-01' and '2016-04-02';
select  TRIM(CLM_ADJSTMNT_KEY) as CLM_ADJSTMNT_KEY, TRIM(MBRSHP_SOR_CD) as MBRSHP_SOR_CD, TRIM(CLM_ITS_HOST_CD) as CLM_ITS_HOST_CD, TRIM(SRVC_RNDRG_TYPE_CD) as SRVC_RNDRG_TYPE_CD, TRIM(SRC_PROV_NATL_PROV_ID) as SRC_PROV_NATL_PROV_ID, TRIM(SRC_BILLG_TAX_ID) as SRC_BILLG_TAX_ID, TRIM(MBR_KEY) as MBR_KEY, RX_FILLED_DT ,TRIM(CLM_SOR_CD) as CLM_SOR_CD from dv_pdphpipph_nogbd_r000_in.CLM where ADJDCTN_DT BETWEEN '2016-04-01' and '2016-06-30';

-- Source csv file:
/dv/hdfsdata/ve2/pdp/spfi/phi/no_gbd/r000/inbound/testdata_combined.csv

-- Target HIVE table:
select * from dv_pdpspfiph_nogbd_r000_wh.zz_phmp_mtclm limit 100;

-- Target CSV HIVE table:
select * from dv_pdpspfiph_nogbd_r000_wh.zz_phmp_customer limit 10;



====================================================
The following is for reference only 
=====================================================
Migration to Spark 2.2

Step 1 - Copy pom.xml
	- Maven Build Test => failures

Action 1 - Change all Spark dependency from provided to compile
------------------------
	<!--<scope>provided</scope>-->
	<scope>compile</scope>
----------------------------


Action 2 - Disable the Anthem repo
----------------------------------------------
	<repositories>
		<!-- <repository> <id>anthem</id> <url>https://artifactory.anthem.com:443/artifactory/public</url>
			<releases> <enabled>true</enabled> </releases> <snapshots> <enabled>false</enabled>
			</snapshots> </repository> -->
		<!-- Spark2 jars are missing in JFROG artifactory -->
		<repository>
			<id>cloudera-releases</id>
			<url>https://repository.cloudera.com/artifactory/cloudera-repos</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>
----------------------------------------------

Action 3 - Sync Maven Porject within InteliJi
	(Critical)

Step 2 - get error:
-----------------------------------------------------
Could not locate executable null\bin\winutils.exe in the Hadoop binaries
-----------------------------------------------------
Action 1 - Download:
http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe
http://www.barik.net/archive/2015/01/19/172716/
	=> hadoop-2.6.0.tar.gz
	To:
C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\libs\winutils\bin\winutils.exe
C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\libs
------
	- this will set up the Hadoop on your local environment

Action 2 - Setup Windows environment variable:

** HADOOP_HOME=C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\libs\winutils
HADOOP_HOME=C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\libs\hadoop-2.6.0.tar\hadoop-2.6.0
PATH=%HADOOP_HOME%\bin;%PATH%
SPARK_DIST_CLASSPATH=C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\libs\hadoop-2.6.0.tar\hadoop-2.6.0\bin

Action 3 - Restart InteliJ

Step 3 - Refactoring Backbone code to spark 2.2
	com.anthem.hpip.helper
	com.anthem.hpip.config

Step 4 - Running Local
	com.anthem.hpip.Template.TemplateDriver
Parameters
C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\InteliJSpark22COE\conf
local
TemplateLocal

Step 5 - Code change:
	hiveCOntext => spark
      //df.write.mode("overwrite").partitionBy(partitionColumn).insertInto(warehouseHiveDB + """.""" + tablename)
      df.write.mode("overwrite").insertInto(warehouseHiveDB + """.""" + tablename)


Step 6 - Notes from Narsi during Migration
--------------------------------------------

RuntimeError1:  User class threw exception: org.apache.spark.sql.AnalysisException: Unable to generate an encoder for inner class `com.anthem.hpip.targettin.TargetTinOperation$hpipAudit` without access to the scope that this class was defined in. Try moving this class out of its parent class.;

FIX: this is scala version upgrade issue. Moved case clss out of parent class.

RuntimeError2: User class threw exception: org.apache.spark.sql.AnalysisException: Table or view not found: dv_pdphpipph_nogbd_r000_wh.hpip_audit;
Fix: added  --files /etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml   to spark2-submit


RuntimeError3:
User class threw exception: org.apache.spark.sql.AnalysisException: insertInto() can't be used together with partitionBy(). Partition columns have already be defined for the table. It is not necessary to use partitionBy().;
https://dwbdtest1r1e.wellpoint.com:8888/jobbrowser/jobs/application_1515713545525_12502

Fix: Remove partitionBy(columnname)   for df.write


Compile Time Error:  Anthem JFROG Artifactory doesn’t have Spark2.2 cloudera jars
Temporary FIX: I  have added cloudera repo to pom repositories

CompileTime Warning: Dataframe.UnionALL is showing as deprecated
Fix: I have used union
================================================================