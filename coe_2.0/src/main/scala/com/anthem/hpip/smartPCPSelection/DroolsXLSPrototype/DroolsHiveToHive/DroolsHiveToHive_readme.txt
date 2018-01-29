
Step 0 - Create tables:




Step 1 - Test run:
	- com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.drools.TestRun.jar

Step 2 - Test run Spark:
	com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.DroolsOnSpark.DroolsProductDriver
	Parameters: C:\java\git\repos\ps_poc\SmartSelection_ETL\conf local TemplateLocal

===============================================================
Section 2 - Run Template on Cluster 
	- Client mode first 
	- cluster mode second
---------------------------------------------------------------

Step 0 - Before to deploy to cluster: 
	*** reverse back the code in com.anthem.hpip.config.Spark2Config
	*** Forget to do this will cause mode conflict issue and fail on --deploymode cluster without explict Error

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
Step 3 - FTP SmartPCPSelect-2.0.0.jar
From:
    ../target/SmartPCPSelect-2.0.0.jar
To:
    /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/SmartPCPSelect-2.0.0.jar

Push to HDFS
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/SmartPCPSelect-2.0.0.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/

***Not needed unless it is missing in the lib *** C:\java\git\repos\COE_ETL\conf\grizzled-slf4j_2.10-1.3.1.jar => /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/jar


---------------------------------------------------------------

---------------------------------------------------------------
Step 4 - FTP query_smartProvider.properties
From:
../conf/query_smartProviderCoe.properties
../conf/log4j.xml
../conf/application_dev_coe.properties
../conf/rules/DroolsRuleDecisionTableDiscount.xls
To:
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties
/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/DroolsRuleDecisionTableDiscount.xls
Push to HDFS:
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProviderCoe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/application_dev_coe.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/DroolsRuleDecisionTableDiscount.xls /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/

---------------------------------------------------------------

Step 5 - Run Spark-submit => client mode 
export JAVA_HOME=/usr/java/latest/

**** spark2-shell --conf spark.ui.port=5052

-------------------------------
run local
	--files /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/DroolsRuleDecisionTableDiscount.xls
		=> this is not required for client run
		=> cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/ => need to have the file in the current folder with the rule file
		=> code need to sc.addFile(filename only) SparkFiles.get(filename only)
*************** client mode will not work if it is query DB **************************
This is the kerberos setup
***************************************************************************************
-----------------------------------
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
spark2-submit \
--master yarn \
--deploy-mode client \
--name Drools-client-SPARK22 \
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
--class com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.DroolsHiveToHive.DroolsHiveToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/SmartPCPSelect-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe
---------------------------------------------------
	- this works

/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/DroolsRuleDecisionTableDiscount.xls,



-------------------------------
run cluster
	--files /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/DroolsRuleDecisionTableDiscount.xls
		=> this is required for cluster
		=> code need to sc.addFile(filename only) SparkFiles.get(filename only)
		=> No need to be in the current folder with the rule file
-----------------------------------

cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name Drools-ToHive-cluster-SPARK22 \
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
--files /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/DroolsRuleDecisionTableDiscount.xls,hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.DroolsHiveToHive.DroolsHiveToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/SmartPCPSelect-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe
---------------------------------------------------
	- this works


-------------------------------
run cluster with hdfs rule file
	--files /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/DroolsRuleDecisionTableDiscount.xls
		=> this is required for cluster
		=> code need to sc.addFile(filename only) SparkFiles.get(filename only)
		=> No need to be in the current folder with the rule file
-----------------------------------
export JAVA_HOME=/usr/java/latest/
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name Drools-HiveDFToHive-hdfs-cluster-400M-SPARK22 \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 50 \
--executor-memory 20G \
--executor-cores 4 \
--queue pca_yarn \
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
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/DroolsRuleDecisionTableDiscount.xls,hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.DroolsHiveToHive.DroolsHiveToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/SmartPCPSelect-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe






