
===============================================================
Step 0 - Run query in ddl/phmp.hql to create table at Hive

---------------------------------------------------------------
Step 1 - Maven build pom.xml -> Maven test
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
    /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar

Push to HDFS
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-1.0.0.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/

C:\java\git\repos\COE_ETL\conf\grizzled-slf4j_2.10-1.3.1.jar => /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/jar


---------------------------------------------------------------
Step 4 - FTP query_smartProvider.properties
From:
../conf/query_smartProvider.properties
../conf/log4j.xml
To:
/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/query_smartProvider.properties
/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/log4j.xml
Push to HDFS:
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/query_smartProvider.properties /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/

---------------------------------------------------------------
Step 5 - Run Spark-submit
export JAVA_HOME=/usr/java/latest/

**** spark2-shell --conf spark.ui.port=5052

spark2-submit \
--master yarn \
--deploy-mode cluster \
--name SMPD-CSV-HIVE-Test-SPARK22 \
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
--files hdfs:///ts/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev smartProvider


spark-submit \
--master yarn \
--deploy-mode cluster \
--name SMPD-CSV-HIVE-Test \
--num-executors 30 \
--executor-memory 30G \
--executor-cores 4 \
--driver-memory 16G \
--conf spark.yarn.executor.memoryOverhead=8192 \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--packages com.databricks:spark-csv_2.10:1.5.0 \
--jars /data/01/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/jar/grizzled-slf4j_2.10-1.3.1.jar \
--class com.anthem.hpip.smartProvider.prototype.CSVToHive.CSVToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/CenterOfExcellence-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev smartProvider

Step 6 - Run HiveToCSVDriver
spark-submit \
--master yarn \
--deploy-mode cluster \
--name PHMP-HIVE-TO-CSV \
--num-executors 30 \
--executor-memory 30G \
--executor-cores 4 \
--driver-memory 16G \
--conf spark.yarn.executor.memoryOverhead=8192 \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--packages com.databricks:spark-csv_2.10:1.5.0 \
--jars /data/01/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/grizzled-slf4j_2.10-1.3.1.jar \
--class com.anthem.hpip.smartProvider.prototype.CSVToHive.CSVToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/smpd-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev smartProvider



Step 6 - To Debug:
1 - Turn off log (This will has less output)
2 - run with yarn-client (This will has print output to local)
spark-submit \
--master yarn-client \
--name SMPD-CSV-HIVE-Test \
--num-executors 30 \
--executor-memory 30G \
--executor-cores 4 \
--driver-memory 16G \
--conf spark.yarn.executor.memoryOverhead=8192 \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--jars /data/01/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/grizzled-slf4j_2.10-1.3.1.jar \
--packages com.databricks:spark-csv_2.10:1.5.0 \
--class com.anthem.hpip.smartProvider.prototype.CSVToHive.CSVToHiveDriver \
/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/smpd-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev smartProvider


Step 7 - Hue verification:
https://dwbdtest1r1e.wellpoint.com:8888/notebook/editor?editor=357328
select * from  dv_pdphpipph_nogbd_r000_wh.zz_phmp_mtclm;
select * from  dv_pdphpipph_nogbd_r000_wh.zz_phmp_customer;

Step 8 - Job Verification:
https://dwbdtest1r1e.wellpoint.com:8888/jobbrowser/

(Edge node: dwbdtest1r1e.wellpoint.com)

