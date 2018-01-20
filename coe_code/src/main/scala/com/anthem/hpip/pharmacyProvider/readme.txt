
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
Step 3 - FTP hpip-1.0.0.jar
From:
    ../target/hpip-1.0.0.jar
To:
    /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-1.0.0.jar
    (RENAME TO: CenterOfExcellence-1.0.0.jar)
Push to HDFS
cd /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/CenterOfExcellence-1.0.0.jar /dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/

---------------------------------------------------------------
Step 4 - FTP query_pharmacyProvider.properties
From:
../conf/query_pharmacyProvider.properties
To:
/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/query_pharmacyProvider.properties
Push to HDFS:
cd /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/
hadoop fs -put -f /dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/query_pharmacyProvider.properties /dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/

---------------------------------------------------------------
Step 5 - Run Spark-submit
spark-submit \
--master yarn \
--deploy-mode cluster \
--name PHMP-CSV-DF-Test \
--num-executors 30 \
--executor-memory 30G \
--executor-cores 4 \
--driver-memory 16G \
--conf spark.yarn.executor.memoryOverhead=8192 \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--packages com.databricks:spark-csv_2.10:1.5.0 \
--class com.anthem.hpip.pharmacyProvider.PharmacyDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/CenterOfExcellence-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev pharmacyProvider

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
--class com.anthem.hpip.pharmacyProvider.HiveToCSVDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/CenterOfExcellence-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev pharmacyProvider

com.anthem.hpip.pharmacyProvider


Step 6 - To Debug: 
1 - Turn off log (This will has less output)
2 - run with yarn-client (This will has print output to local)
spark-submit \
--master yarn-client \
--name PHMP-CSV-DF-Test \
--num-executors 30 \
--executor-memory 30G \
--executor-cores 4 \
--driver-memory 16G \
--conf spark.yarn.executor.memoryOverhead=8192 \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--packages com.databricks:spark-csv_2.10:1.5.0 \
--class com.anthem.hpip.pharmacyProvider.PharmacyDriver \
/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/CenterOfExcellence-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev pharmacyProvider


Step 7 - Hue verification:
https://dwbdtest1r1e.wellpoint.com:8888/notebook/editor?editor=357328
select * from  dv_pdphpipph_nogbd_r000_wh.zz_phmp_mtclm;
select * from  dv_pdphpipph_nogbd_r000_wh.zz_phmp_customer;

Step 8 - Job Verification:
https://dwbdtest1r1e.wellpoint.com:8888/jobbrowser/

(Edge node: dwbdtest1r1e.wellpoint.com)

Step 9 - Verify the csv file created

hadoop fs -ls /dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/outbound/phmp/HiveToCSV/
hadoop fs -cat /dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/outbound/phmp/HiveToCSV/part-00000

/dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/outbound/phmp/HiveToCSV/part-00000

hadoop fs -get /dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/outbound/phmp/HiveToCSV/part-00000 /dv/data/ve2/pdp/hpip/phi/no_gbd/r000/outbound/phmp/HiveToCsv.csv



