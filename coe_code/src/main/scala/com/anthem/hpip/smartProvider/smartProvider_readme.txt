
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
    /dv/data/ve2/pdp/hpip/phi/no_gbd/r000/inbound
hadoop fs -put /dv/data/ve2/pdp/hpip/phi/no_gbd/r000/inbound/testdata_combined.csv /dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/inbound/
---------------------------------------------------------------
Step 3 - FTP smpd-1.0.0.jar
From:
    ../target/smpd-1.0.0.jar
To:
    /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/CenterOfExcellence-1.0.0.jar

Push to HDFS
cd /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/CenterOfExcellence-1.0.0.jar /dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/

C:\java\git\repos\COE_ETL\conf\grizzled-slf4j_2.10-1.3.1.jar => /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/jar


---------------------------------------------------------------
Step 4 - FTP query_pharmacyProvider.properties
From:
../conf/query_smartProvider.properties
To:
/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/query_smartProvider.properties
Push to HDFS:
cd /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/query_smartProvider.properties /dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/

---------------------------------------------------------------
Step 5 - Run Spark-submit
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

