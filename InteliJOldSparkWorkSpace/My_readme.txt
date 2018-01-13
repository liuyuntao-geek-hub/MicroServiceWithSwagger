Jar is at:
C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\InteliJOldSparkWorkSpace\target\OldSparkScala.jar
To Server:
cd /home/yuntliu/workstation/sparkPlayGround/anthem/padp/sas/bin

Copy configuration
C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\InteliJOldSparkWorkSpace\server_environment.json
To Server:
cd /home/yuntliu/workstation/sparkPlayGround/anthem/padp/sas/bin/environment.json

copy:
C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\InteliJOldSparkWorkSpace\data
To
/home/yuntliu/workstation/sparkPlayGround/anthem/padp/sas/bin/data/

put:
hadoop fs -put data/* /user/yuntliu/anthem/padp/sas/data/input/
hadoop fs -ls /user/yuntliu/anthem/padp/sas/data/input

##### = notworking = #####
hadoop fs -put application.properties /user/yuntliu/anthem/padp/sas/data/

vi
vi /home/yuntliu/workstation/sparkPlayGround/anthem/padp/sas/bin/application.properties
-----------------------------------------
inputFilePath=/user/yuntliu/anthem/padp/sas/data/input/
-------------------------------------------
####################################
    = use application to read parameters to give configuration file in resource folder = not working


-------------------------------------------------
Cloudera Manager URL: http://10.118.36.60:7180/
HUE URL: http://10.118.36.60:8888/

Username: xxxxxx
Password (Linux Login): xxxxx => Linux OS password
Password: xxxxxx => Hadoop related password

Server IP	Hostname
10.118.36.60	ussltcsnl2608.solutions.glbsnet.com
10.118.36.61	ussltcsnl2609.solutions.glbsnet.com
10.118.36.103	ussltcsnl2838.solutions.glbsnet.com
-------------------------------------------------


cd /home/yuntliu/workstation/sparkPlayGround/anthem/padp/sas/bin
export PATH=/home/yuntliu/workstation/sparkPlayGround/kafka/lib:/opt/cloudera/parcels/CDH-5.7.0-1.cdh5.7.0.p0.45/lib/spark/bin:$PATH
spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.NoFrameworkRunnable.FirstScalaDriver OldSparkScala.jar FirstRun
spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.dataScience.MasterDataManagementStringWrapper OldSparkScala.jar DataScience yarn-client
spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.hiveOperation.hiveWriteWrapper OldSparkScala.jar HiveWrite yarn-client
spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.sasLogic.wordCountWrapper OldSparkScala.jar WordCount yarn-client
spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.hiveOperation.csvHoHiveWrapper OldSparkScala.jar HiveToCSV yarn-client
spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.excelOperation.readExcelToHiveWrapper OldSparkScala.jar excelToCSV yarn-client
spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.prototype.HBaseOperation.SparkRddToHFileInsert OldSparkScala.jar excelToCSV yarn-client

spark-submit --name "FirstRun" --master local[4] --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype local[4]
spark-submit --name "FirstRun" --master yarn-client --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn-client

spark-submit  --master yarn --name "FirstRun" --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn-client

spark-submit --name "FirstRun" --master yarn-client --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn-client
    - This need the server has java 8


spark-submit  --master yarn --name "FirstRun"  --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar DroolsPrototype

hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc

spark-submit --name "FirstRun" --master yarn --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn

--- The following Yarn works -----
spark-submit \
--master yarn \
--deploy-mode cluster \
--name FirstRun \
--num-executors 30 \
--executor-memory 30G \
--executor-cores 4 \
--driver-memory 16G \
--conf spark.yarn.executor.memoryOverhead=8192 \
--conf spark.network.timeout=600 \
--conf spark.driver.maxResultSize=2g \
--packages com.databricks:spark-csv_2.10:1.5.0 \
--class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar  \
DroolsPrototypo


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




  * This solution is using crealytics = Not really working because the apple throw Garbage collection Error:
  *   Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded

    - This works when having different configure file at exec folder: environment.json
    - We should use --master yarn
        - Issues:
            - This will not read any local file: file:///xxxx will not work on this
            - logs and println() will not work
    - On testing we use --master local[2] on desktop
    - On testing we use --master yarn-client on SNET
    - We could specify the jar and configuration files in hdfs as far as we pass the hdfs:///
    Example:
-------------------------------------------------------------------
****** Anthem Project ************
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
--class com.anthem.hpip.asoPricing.ASOPDriver \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-asop-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev ASOPricing

****** Anthem Project ************
To Debug:
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
--class com.anthem.hpip.asoPricing.ASOPDriver \
/dv/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip-asop-1.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/ dev ASOPricing
-------------------------------------------------------------------


=================== To Test run the Spark Shell =======================
cd /home/yuntliu/workstation/sparkPlayGround/kafka/lib
spark-shell --master yarn-client  --jars kafka_2.11-0.10.0.1.jar,scala-parser-combinators_2.11-1.0.4.jar,slf4j-log4j12-1.7.21.jar,spark-streaming-kafka-0-10_2.11-2.1.1.jar,spark-tags_2.11-2.1.1.jar
=======================================================================
    - This will work = switch between different properties
##### = notworking = ##### spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.firstRunnable.FirstScalaDriver OldSparkScala.jar /user/yuntliu/anthem/padp/sas/data/application.properties
    - Giving parameters to reach configuration files in regular path to replace resource folder = not work
##### = notworking = ##### spark-submit --name "FirstRun" --master yarn-client --packages com.databricks:spark-csv_2.10:1.5.0 --class com.deloitte.demo.firstRunnable.FirstScalaDriver  OldSparkScala.jar   /user/yuntliu/anthem/padp/sas/data/application.properties
    - Packages not working => need fat jar for databrick to work

========================================
When Missing Jars:
1 - Build the Fat Jar
2 - 7.zip to open the Fat Jar => remove the coding com.deloitt.demo
3 - Rename the jar to be:DependencyOldSparkScala.jar
4 - FTP to the server
5 - Comment out the Fat Jar in POM

Verify:
HUE URL: http://10.118.36.60:8888/
================================
Logging comparison
------------------------------
- Log4j:
	- Only on Spark => Trait logging not working
	- outside Spark => Everything works
	- Customized configuration works
- Slf4j:
	- on Spark => Trait logging not working
	- outside Spark => Everything works
	- auto binding to Log4j = only need to add slf4j-log4j2 into the maven - or class path
	- log4J configuration coding still working => out put the the correct folder with the specified logging
	- * subclass implementation = working correctly
- Grizzled slf4J:
	- on Spark => Trait logging not working
	- The logging on File => not giving the right class name
	- auto binding to Log4j = only need to add slf4j-log4j2 into the maven - or class path
	- log4J configuration coding still working => out put the the correct folder with the specified logging
	- * subClass implementation = not working correctly
===================================


HBase Note:
=======================================================================
C:\Users\yuntliu\Documents\workstation\Study\New_bigData\HBase

When pushing the file with Space to HDFS
hadoop fs -put /home/yuntliu/workstation/sparkPlayGround/anthem/padp/sas/bin/data/excel/Monthly%20IFT%20-%20CA%20-%20May_2016.xlsm /user/yuntliu/anthem/padp/sas/data/input/excel/
hadoop fs -put /home/yuntliu/workstation/sparkPlayGround/anthem/padp/sas/bin/data/excel/Monthly%20IFT%20-%20CA%20-%20Mar_2017.xlsm /user/yuntliu/anthem/padp/sas/data/input/excel/
hadoop fs -put /home/yuntliu/workstation/sparkPlayGround/anthem/padp/sas/bin/data/excel/Monthly%20IFT%20-%20CO%20-%20May_2016.xlsm /user/yuntliu/anthem/padp/sas/data/input/excel/


hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles hdfs:///user/yuntliu/HbaseWorkShop/HFileHome/tmp Anthem_PADP:customer

package level log4j
====================================
spark-submit \
--master yarn \
--deploy-mode cluster \
--name HPIP_TARGET_TIN \
--files file:///dv/app/ve2/pdp/hpip/phi/no_gbd/r000/control/log4j.xml \
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

==============================






