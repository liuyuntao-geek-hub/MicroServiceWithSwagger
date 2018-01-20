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
========== ***** Final Dev ******* =================
>spark-submit \
--master yarn \
--deploy-mode cluster \
--name MTCLM_DEV \
--jars /data/01/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/grizzled-slf4j_2.10-1.3.1.jar \
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
    
    
=============== ***** SIT ***** ======================    
spark-submit \
--master yarn \
--deploy-mode cluster \
--name MTCLM_DEV \
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



========== ***** Using Edge node ******* =================
>spark-submit \
	--master yarn \
	--deploy-mode cluster \
    --name MTCLM_DEV \
    --conf "spark.ui.port=10105" \
    --num-executors 10 \
    --executor-memory 20G \
    --executor-cores 4 \
    --driver-memory 8G \
    --conf spark.network.timeout=600 \
    --conf spark.driver.maxResultSize=2g \
    --class com.anthem.hpip.mtclm.MTCLMDriver \
    /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/jar/hpip-1.0.0.jar \
    /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/conf dev hpip_priority_1 



==================FOR DEV TARGET TIN ==========================
>spark-submit \
--master yarn \
--deploy-mode cluster \
--name TARGET_TIN \
--jars /data/01/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/grizzled-slf4j_2.10-1.3.1.jar \
--conf "spark.ui.port=10105" \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--class com.anthem.hpip.targettin.TargetTinDriver \
--packages com.crealytics:spark-excel_2.10:0.8.3 \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev hpip_priority_1

==================FOR SIT TARGET TIN ==========================
>spark-submit \
--master yarn \
--deploy-mode cluster \
--name TARGET_TIN \
--class com.anthem.hpip.targettin.TargetTinDriver \
--packages com.crealytics:spark-excel_2.10:0.8.3 \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit hpip_priority_1

==================FOR DEV SPEND ==========================
spark-submit \
--name SPEND \
--master yarn \
--conf "spark.ui.port=10107" \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--deploy-mode cluster \
--class com.anthem.hpip.spend.SpendDriver  \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0-spend.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev hpip_priority_1

==================FOR SIT SPEND ==========================
spark-submit \
--name SPEND \
--master yarn \
--conf "spark.ui.port=10107" \
--num-executors 10 \
--executor-memory 20G \
--executor-cores 4 \
--driver-memory 8G \
--deploy-mode cluster \
--class com.anthem.hpip.spend.SpendDriver  \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar \
hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ sit hpip_priority_1

====================================================
Hive DDL Execution :-

beeline -u "jdbc:hive2://dwbdtest1r2m3.wellpoint.com:2181,dwbdtest1r1m.wellpoint.com:2181,dwbdtest1r2m.wellpoint.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2" -f mtclm.hql
    