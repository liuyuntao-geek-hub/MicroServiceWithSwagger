HPIP - (HCA Provider Insights Product)

Deployment and Execution steps:
Step 1 - copy over the following config files to Edge node server:
From:
	    ${localRepo}\hpip_etl\hpip\conf\*
		- for example: ${localRepo}=C:\java\git\repos\NarsiHome\    
To:
	/data/01/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/conf
	then copy the same conf files to hdfs 
	 hdfs dfs -put -f /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/conf/*  hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/conf
	
	
Step 2 - copy over the following jar to Edge node server
From: 
    ${localRepo}\hpip_etl\hpip\target\hpip-1.0.0.jar
To:     
    /data/01/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/jar
    
   then copy the same conf files to hdfs 
	 hdfs dfs -put -f /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/jar/*  hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/jar
	
    
Step 3 - Execute the following command: Using with Spark shell

Executing with Spark Submit

>spark-submit \
--master yarn-cluster
--name <NAME OF JOB>
--class com.anthem.hpip.<Driver Class Name> \
hpip.jar \
<Conf file path> <Environment: dev/sit/uat/prod>


For MTCLM:- Please make sure the following Final Section works
========== ***** MTCLM DEV ******* =================
>spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_MTCLM \
--num-executors 30 \
--executor-memory 30G \
--executor-cores 4 \
--driver-memory 16G \
--conf spark.yarn.executor.memoryOverhead=8192 \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--class com.anthem.hpip.mtclm.MTCLMDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev hpip_priority_1
    
    
=============== *****MTCLM  SIT ***** ======================    
spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_MTCLM \
--num-executors 30 \
--executor-memory 30G \
--executor-cores 4 \
--driver-memory 16G \
--conf spark.yarn.executor.memoryOverhead=8192 \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--class com.anthem.hpip.mtclm.MTCLMDriver \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit hpip_priority_1

==================FOR DEV TARGET TIN ==========================
>spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_TARGET_TIN \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--class com.anthem.hpip.targettin.TargetTinDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpiptin-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev hpip_priority_1

==================FOR SIT TARGET TIN ==========================
>spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_TARGET_TIN \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--class com.anthem.hpip.targettin.TargetTinDriver \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit hpip_priority_1

==================FOR DEV SPEND ==========================
spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_SPEND \
--conf "spark.ui.port=10107" \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--class com.anthem.hpip.spend.SpendDriver  \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev hpip_priority_1

==================FOR SIT SPEND ==========================
spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_SPEND \
--conf "spark.ui.port=10107" \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--class com.anthem.hpip.spend.SpendDriver  \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit hpip_priority_1

====================================================
Hive DDL Execution :-

beeline -u "jdbc:hive2://dwbdtest1r2m3.wellpoint.com:2181,dwbdtest1r1m.wellpoint.com:2181,dwbdtest1r2m.wellpoint.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2" -f mtclm.hql
    
    

===============================================================================================================
With Custom Log4j.xml
===============================================================================================================
>spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_TARGET_TIN \
--files hdfs:///dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/log4j.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--class com.anthem.hpip.targettin.TargetTinDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev hpip_priority_1

================TARGET TIN DEV ========================
spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_TARGET_TIN \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--files hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/log4j.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--class com.anthem.hpip.targettin.TargetTinDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev hpip_priority_1

===============================================================================================
Spark2 TARGET_TIN
================================================================================================
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_TARGET_TIN \
--driver-memory 8G \
--driver-cores 5 \
--num-executors 10 \
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
--jars /opt/cloudera/parcels/CDH-5.8.3-1.cdh5.8.3.p2095.2180/jars/hive-contrib-1.1.0-cdh5.8.3.jar \
--files hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.targettin.TargetTinDriver \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0_spark2.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit_nogbd hpip_priority_1


===============================================================================================
Spark2 Spend
================================================================================================
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_SPEND \
--driver-memory 8G \
--driver-cores 5 \
--num-executors 10 \
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
--jars /opt/cloudera/parcels/CDH-5.8.3-1.cdh5.8.3.p2095.2180/jars/hive-contrib-1.1.0-cdh5.8.3.jar \
--files hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.spend.SpendDriver \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0_spark2.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit_nogbd hpip_priority_1


===============================================

===============================================================================================
Spark2 MTCLM
================================================================================================
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_MTCLM \
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
--jars /opt/cloudera/parcels/CDH-5.8.3-1.cdh5.8.3.p2095.2180/jars/hive-contrib-1.1.0-cdh5.8.3.jar \
--files hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.mtclm.MTCLMDriver \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0_spark2.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit_nogbd hpip_priority_1

===============================================================================================
Spark2 RENDERINGSPEND
================================================================================================
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_RENDERING_SPEND_E \
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
--conf spark.dynamicAllocation.minExecutors=30 \
--jars /opt/cloudera/parcels/CDH-5.8.3-1.cdh5.8.3.p2095.2180/jars/hive-contrib-1.1.0-cdh5.8.3.jar \
--files hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.mtclm.MTCLMDriver \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0_spark2.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit_nogbd hpip_priority_1

============================================================================================================
--conf spark.dynamicAllocation.enabled=false \

--conf spark.dynamicAllocation.executorIdleTimeout=180 \
--conf spark.dynamicAllocation.initialExecutors=30 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=30 \



==================================================================================================================

>> yarn logs -appOwner-appOwner srcpdphpipbthts \
-applicationId application_1512531747652_17155 >> application_1512531747652_17155.log