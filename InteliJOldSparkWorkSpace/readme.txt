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

/user/yuntliu/anthem/padp/sas/data/input/

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

Username: yuntliu
Password (Linux Login): Temp4Now => Linux OS password
Password: Password.1 => Hadoop related password

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
spark-submit --name "FirstRun" --master yarn-client --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn-client
    - This need the server has java 8


spark-submit  --master yarn --name "FirstRun"  --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar DroolsPrototype

hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc

spark-submit --name "FirstRun" --master yarn --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn

--- The following Yarn works -----

spark-submit --name "FirstRun" --master yarn --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn

hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc

================================================================================================================
Run it as Local Mode: 
Step 1 - com.deloitte.demo.DroolsXLSPrototype.DroolsProductOperation

	val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{rulesProcessor.applyDiscountRule(row,FilePath)})).toDF("Type", "Discount")

Step 2 - com.deloitte.demo.framework.OperationSession

	val exDocReader:Config = ConfigFactory.parseFile(new File( externalConfig.getOrElse( "environment.json")))

Step 3 - Run on local

	com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver

Run it as Local on Server
Step 1 - Maven install on local

Step 2 - FTP:
From: C:\java\git\repos\ps_poc\InteliJOldSparkWorkSpace\target
To: /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc

Step 3 - Fix the environment.json
  "Drools":
  {
    "DroolsXLSRuleFile": "/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/DroolsRuleDecisionTableDiscount.xls"
  }


Step 4 - Run it:
spark-submit --name "FirstRun" --master local[4] --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype local[4]
 
Step 5 - Run it with local Jar:
spark-submit --name "FirstRun" --master local[4] --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala_Local.jar DroolsPrototype local[4]
 
 
Run it as Yar submitssion with File Reader
Step 1 - com.deloitte.demo.DroolsXLSPrototype.DroolsProductOperation

	val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{rulesProcessor.applyDiscountRule(row,FilePath)})).toDF("Type", "Discount")

Step 2 - com.deloitte.demo.framework.OperationSession

  val appConfFile = hdfs.open(new Path("/dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))
  val appConfReader = new InputStreamReader(appConfFile)
  val exDocReader:Config = ConfigFactory.parseReader(appConfReader)
   
Step 3 - Maven install on local:

Step 4 - FTP:
 From: C:\java\git\repos\ps_poc\InteliJOldSparkWorkSpace\target
To: /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc

Step 5 - load to HDFS
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc

Step 6 - Run on remote Yarn

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
DroolsPrototype

--------------------------------
Caused by: java.io.FileNotFoundException: /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/DroolsRuleDecisionTableDiscount.xls (No such file or directory)
	at java.io.FileInputStream.open0(Native Method)
	at java.io.FileInputStream.open(FileInputStream.java:195)
	at java.io.FileInputStream.<init>(FileInputStream.java:138)
	at org.drools.core.io.impl.FileSystemResource.getInputStream(FileSystemResource.java:123)
	at org.drools.compiler.kie.builder.impl.KieFileSystemImpl.write(KieFileSystemImpl.java:58)
Cannot open the file	
----------------------------------------


Run it as Yar submitssion with File stream
Step 1 - com.deloitte.demo.DroolsXLSPrototype.DroolsProductOperation

	val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{rulesProcessor.applyDiscountRule(row,FilePath)})).toDF("Type", "Discount")

Step 2 - com.deloitte.demo.framework.OperationSession

  val appConfFile = hdfs.open(new Path("/dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))
  val appConfReader = new InputStreamReader(appConfFile)
  val exDocReader:Config = ConfigFactory.parseReader(appConfReader)
   
Step 3 - Maven install on local:

Step 4 - FTP:
 From: C:\java\git\repos\ps_poc\InteliJOldSparkWorkSpace\target
To: /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc

Step 5 - load to HDFS
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc

Step 6 - Run on remote Yarn

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
DroolsPrototype

Read as Stream:
==========================================================

18/01/12 17:03:50 WARN TraitOperator: *** Start load ***
18/01/12 17:03:52 WARN framework.OperationSession$: OperationSession Start
18/01/12 17:03:52 WARN security.UserGroupInformation: PriviledgedActionException as:af35352 (auth:SIMPLE) cause:org.apache.hadoop.security.authentication.client.AuthenticationException: GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)
18/01/12 17:03:52 WARN kms.LoadBalancingKMSClientProvider: KMS provider at [https://dwbddev1vm13.wellpoint.com:16000/kms/v1/] threw an IOException [org.apache.hadoop.security.authentication.client.AuthenticationException: GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)]!!
18/01/12 17:03:52 ERROR executor.Executor: Exception in task 0.0 in stage 3.0 (TID 6)
java.lang.RuntimeException: Cannot find KieModule: org.default:artifact:1.0.0-SNAPSHOT
	at org.drools.compiler.kie.builder.impl.KieServicesImpl.newKieContainer(KieServicesImpl.java:97)
	at com.deloitte.demo.DroolsXLSPrototype.drools.KieSessionFactory.getNewKieSessionStream(KieSessionFactory.java:128)
	at com.deloitte.demo.DroolsXLSPrototype.drools.KieSessionFactory.getKieSessionStream(KieSessionFactory.java:41)
	at com.deloitte.demo.DroolsXLSPrototype.drools.KieSessionApplier.applyDiscountStreamRule(KieSessionApplier.java:46)
	at com.deloitte.demo.DroolsXLSPrototype.rulesProcessorstream$.applyDiscountRule(DroolsProductOperation.scala:106)
	at com.deloitte.demo.DroolsXLSPrototype.DroolsProductOperation$$anonfun$3.apply(DroolsProductOperation.scala:48)

===========================================================


Run it as Local Mode with embedded xls: 
Step 1 - com.deloitte.demo.DroolsXLSPrototype.DroolsProductOperation

   val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{rulesProcessorResource.applyDiscountRule(row,FilePath)})).toDF("Type", "Discount")
 
Step 2 - com.deloitte.demo.framework.OperationSession

	val exDocReader:Config = ConfigFactory.parseFile(new File( externalConfig.getOrElse( "environment.json")))

Step 3 - Run on local

	com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver

Run it as Local on Server
Step 1 - Maven install on local




Run it as Local on Server as Local mode using Resource folder
Step 1 - Maven install on local

Step 2 - FTP:
From: C:\java\git\repos\ps_poc\InteliJOldSparkWorkSpace\target
To: /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc


Step 3 - Run it:
spark-submit --name "FirstRun" --master local[4] --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype local[4]
 
 -- Not working: need to have the Sparkfile.get()


 Note: 
 - If distributed function calling any of the sc related attributes = failure on kerbors
 
 
 S
 
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
 --conf spark.ui.port=5052 \
 --files DroolsRuleDecisionTableDiscount.xls \
 --packages com.databricks:spark-csv_2.10:1.5.0 \
 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver \
 hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar  \
DroolsPrototype
 
 
 
 
  spark-submit --name "FirstRun" --master yarn --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn
 
   spark-submit --name "FirstRun" --master local[4] --conf spark.ui.port=5052 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn
  
 
 
 
 
 Deployment cluster = remove the access to edge node file:
 	- Have to use --files operation to upload property files
 	
 	
 
 Final:
 Local:
 
 Step 1 - com.deloitte.demo.DroolsXLSPrototype.DroolsProductOperation
     val outDFs = sqlContext.createDataFrame(inDFs.getOrElse("NewData1", null).map(row=>{rulesProcessor.applyDiscountRule(row,FilePath)})).toDF("Type", "Discount")
 
 Step 2 - com.deloitte.demo.framework.OperationSession
 
 val hdfs: FileSystem = FileSystem.get(sc.hadoopConfiguration)
 
  val appConfFile = hdfs.open(new Path("/dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))
  val appConfReader = new InputStreamReader(appConfFile)
  val exDocReader:Config = ConfigFactory.parseReader(appConfReader)

  val conf = new SparkConf()//.setAppName("override").setMaster("local[4]")

Step 3  - FTP:
From: C:\java\git\repos\ps_poc\InteliJOldSparkWorkSpace\target
To: /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc


Step 4 - push file to HDFS 
 hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc 
 hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc 


Step 5 - Run on remote with deploy-mode cluster
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
 --conf spark.ui.port=5052 \
 --files DroolsRuleDecisionTableDiscount.xls \
 --packages com.databricks:spark-csv_2.10:1.5.0 \
 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver \
 hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar  \
DroolsPrototype 
 

=============================================================================================
	- With Deploy-cluster: driver will be run on a random server:
	Problem:
		--files not working properly
		
		
		
		
		- have to do --files for property files if executors need them (All file can only be in root)
		    	sc.addFile("DroolsRuleDecisionTableDiscount.xls")
    			var FilePath = SparkFiles.get("DroolsRuleDecisionTableDiscount.xls")
		- use FileSystem if only driver need them
			val hdfs: FileSystem = FileSystem.get(sc.hadoopConfiguration)
			val appConfFile = hdfs.open(new Path("/dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))
		- Cannot give local[4]
			val conf = new SparkConf().setAppName("override").setMaster("local[4]")



==========================================================================================
==========================================================================================

Local on Desktop
 val conf = new SparkConf().setAppName(AppName).setMaster(master)
  val sc = SparkContext.getOrCreate(conf);
    val exDocReader:Config = ConfigFactory.parseFile(new File( externalConfig.getOrElse( "environment.json")))




Local mode on Server
val conf = new SparkConf().set("hive.execution.engine", "spark").set("spark.acls.enable", "true")
  val sc = new SparkContext(conf)
    val exDocReader:Config = ConfigFactory.parseFile(new File( externalConfig.getOrElse( "environment.json")))

   spark-submit --name "FirstRun" --master local[4] --conf spark.ui.port=5052  --files DroolsRuleDecisionTableDiscount.xls --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype local[4]
  

   spark-submit --name "FirstRun" --master yarn-client  --deploy-mode client --conf spark.ui.port=5052  --files DroolsRuleDecisionTableDiscount.xls --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn-client
  
   spark-submit --name "FirstRun" --master yarn --conf spark.ui.port=5052  --files DroolsRuleDecisionTableDiscount.xls --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype yarn
 

 val appConfFile = hdfs.open(new Path("/dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))
  val appConfReader = new InputStreamReader(appConfFile)
 val exDocReader:Config = ConfigFactory.parseReader(appConfReader)


==================================================================
--deploy-mode client: Driver is edgenode running the submit
==================================================================


Only Local works - All followings are local -> last parameters
   spark-submit --name "FirstRun" --master local[4] --conf spark.ui.port=5052  --files DroolsRuleDecisionTableDiscount.xls --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype local[4] 
   spark-submit --name "FirstRun" --master yarn-client  --deploy-mode client --conf spark.ui.port=5052  --files DroolsRuleDecisionTableDiscount.xls --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype local[4]  
   spark-submit --name "FirstRun" --master yarn --conf spark.ui.port=5052  --files DroolsRuleDecisionTableDiscount.xls --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver OldSparkScala.jar DroolsPrototype local[4]

	
Read hdfs Stream works
	val hdfs: FileSystem = FileSystem.get(sc.hadoopConfiguration)
	val appConfFile = hdfs.open(new Path("/dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))

Spark functions:
	file:// (Default)
	hdfs://
 	val testDF = sqlContext.read.format( "com.databricks.spark.csv" ).option( "header", "true" )option( "inferSchema", "true" ).load( path )


Java class reading files = local drive only 
	val exDocReader:Config = ConfigFactory.parseFile(new File( externalConfig.getOrElse("environment.json")))


-----------------------------------------------------------

yarn-client & yarn

Read hdfs Stream works
	val hdfs: FileSystem = FileSystem.get(sc.hadoopConfiguration)
	val appConfFile = hdfs.open(new Path("/dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))

Spark functions:
	file://
	hdfs://(Default)
	val testDF = sqlContext.read.format( "com.databricks.spark.csv" ).option( "header", "true" )option( "inferSchema", "true" ).load( path )

Java functions:
	Reading files - val exDocReader:Config = ConfigFactory.parseFile(new File( externalConfig.getOrElse("environment.json")))
	Never worked
	
	
==================================================================
--deploy-mode cluster: Driver is random
==================================================================

Only Local: still local, but will be somewhere

Read hdfs Stream works
	val hdfs: FileSystem = FileSystem.get(sc.hadoopConfiguration)
	val appConfFile = hdfs.open(new Path("/dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json"))

Spark functions: ????? 
	file:// 
	hdfs:// (Only This works)
 	val testDF = sqlContext.read.format( "com.databricks.spark.csv" ).option( "header", "true" )option( "inferSchema", "true" ).load( path )


Java class reading files = local drive only 
	Has to use --file
 
FTP:
From: C:\java\git\repos\ps_poc\InteliJOldSparkWorkSpace\target
To: /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc


push file to HDFS 
 hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc 
 hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/environment.json /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc 


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
 --conf spark.ui.port=5052 \
 --files DroolsRuleDecisionTableDiscount.xls \
 --packages com.databricks:spark-csv_2.10:1.5.0 \
 --class com.deloitte.demo.DroolsXLSPrototype.DroolsProductDriver \
 hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/poc/OldSparkScala.jar  \
DroolsPrototype local[4]
======================================================================











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
DroolsPrototype



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






