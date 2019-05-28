
===========================================================================
*********** Section 1 - To resolve the Maven compiling Issue **************
===========================================================================

Step 1 - Manually copy the whole folder:
C:\java\maven\localrepoNew\eigenbase\eigenbase-properties\1.1.4
	- Manually remove the _remote.property file in it
Disable the following not exist 

Step 2 - Remove the following package from Maven: It seems they do not exist any more
<!--
<dependency>
	<groupId>net.sourceforge.jtds</groupId>
	<artifactId>jtds</artifactId>
	<version>1.3.1</version>
	<classifier>patched</classifier>
</dependency>

<dependency>
	<groupId>oracle.jdbc.driver</groupId>
	<artifactId>oraclethin</artifactId>
	<version>11gr2</version>
</dependency>

<dependency>
	<groupId>com.teradata.jdbc</groupId>
	<artifactId>terajdbc4</artifactId>
	<version>16.20.00.10</version>
</dependency>

<dependency>
	<groupId>com.teradata.jdbc</groupId>
	<artifactId>tdgssconfig</artifactId>
	<version>16.20.00.10</version>
</dependency>

  -->
  
Step 4 -  Manually find the jar for 
<dependency>
	<groupId>com.teradata.jdbc</groupId>
	<artifactId>terajdbc4</artifactId>
	<version>16.20.00.10</version>
</dependency>

<dependency>
	<groupId>com.teradata.jdbc</groupId>
	<artifactId>tdgssconfig</artifactId>
	<version>16.20.00.10</version>
</dependency>

Then: put them into the local repo specific folder

  
Step 3 - Run configure: make profile = build-development

===================================================================

===========================================================================
*********** Section 2 - Manual Deployment to execute loading **************
===========================================================================

Step 1 - SFTP to dwbdtest1r5e1.wellpoint.com 
	- login as personal user, such as af35352
	From:
		C:\java\git\repos\cognitiveclaim_dev\ds-cogx-batch\ds-cogx-batch\target\cogxHDFSHBaseETL-1.0.jar
	To:
		/data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar
		
Step 2 - Test Teradata connection:
	(Info from ~project/config/development/application.conf)
	
	- DWTEST3_LDAP: login as personal ID: AF35352
	select * from T89_ETL_VIEWS_ENT.UM_SRVC_STTS
	
Step 3 - build jceks
	- login dwbdtest1r5e1.wellpoint.com 
		- ssh as personal id: af35352
-----------------------------------------------------
cd /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/
mkdir jcekstore
cd jcekstore
hadoop fs -mkdir /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/
hadoop credential create af35352CogxPass -provider jceks://hdfs/dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/af35352Cogx.jceks
hadoop fs -ls /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/

hadoop credential create af35352TempCogxPass -provider jceks://hdfs/dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/af35352TempCogx.jceks

======================================================
cd /home/af35352
mkdir jcekstore
cd jcekstore
/home/af35352/jcekstore
hadoop fs -ls /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/
	          /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/
	- The jceks already exist and working = This is for teradata

hadoop fs -mkdir /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/
hadoop credential create af35352CogxPass -provider jceks://hdfs/dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/af35352Cogx.jceks
hadoop fs -ls /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/



------------------------------------------------------------
Step 4 - Edit  ~project/src/main/resources/application.conf
------------------------------
dataSourceConfig:
  credentialproviderurl: jceks://hdfs/dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/af35352Cogx.jceks
  username: af35352
  password: af35352CogxPass
---------------------------------
****** = This need recompile to create new jar = ***************

Step 5 - Create Keytab:
cd 
cd /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/
mkdir keytabstore
chmod 755 keytabstore

cd 
mkdir keytabstore
chmod 755 keytabstore
cd keytabstore
-------------------------------------------------------------
login as srcccpcogxapidv
ktutil
ktutil: addent -password -p srcccpcogxapidv@DEVAD.WELLPOINT.COM -k 1 -e rc4-hmac
ktutil: addent -password -p srcccpcogxapidv@DEVAD.WELLPOINT.COM -k 1 -e aes256-cts
Password for af35352@DEVAD.WELLPOINT.COM::*******
ktutil: wkt srcccpcogxapidv.keytab
ktutil: exit
kinit srcccpcogxapidv@DEVAD.WELLPOINT.COM -k -t srcccpcogxapidv.keytab

-----------------------------------------------------------------
Setup personal keytab

cd 
mkdir keytabstore
chmod 755 keytabstore
------------------------------------------
ktutil
addent -password -p AF35352@DEVAD.WELLPOINT.COM -k 1 -e rc4-hmac
 - Password for AF35352@DEVAD.WELLPOINT.COM: Enter Password for your Domain ID
addent -password -p AF35352@DEVAD.WELLPOINT.COM -k 1 -e aes256-cts
 - Password for AF35352@DEVAD.WELLPOINT.COM: Enter Password for your Domain ID
ktutil:  wkt AF35352.keytab
ktutil:  quit
kinit AF35352@DEVAD.WELLPOINT.COM -k -t AF35352.keytab
==========================================================

Step 6 - Run hbase code on server as client mode
-------------------

spark2-submit --master yarn --deploy-mode client  \
--files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml \
--name "COGX HBase ETL: cogx_um_load - by AF35352" \
--principal AF35352 \
--keytab /home/af35352/keytabstore/AF35352.keytab  \
--driver-memory 5G \
--executor-memory 5G \
--executor-cores 10 \
--num-executors 4 \
--driver-class-path /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar \
--jars /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar \
--conf "spark.driver.maxResultSize=5G" \
--conf "spark.port.maxRetries=100" \
--conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" \
--conf "spark.yarn.security.tokens.hbase.enabled=true" \
--conf "spark.dynamicAllocation.enabled=true" \
--class "com.anthem.cogx.pi.etl.Driver" \
/data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar cogx_um_load


Application report for application_1557843199409_16763
application_1557843199409_17302 
------------------------------------------

spark2-submit --master yarn --deploy-mode client  \
--files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml \
--name "COGX HBase ETL: cogx_um_load - by AF35352" \
--principal AF35352 \
--keytab /home/af35352/keytabstore/AF35352.keytab  \
--driver-memory 5G \
--executor-memory 5G \
--executor-cores 10 \
--num-executors 4 \
--driver-class-path /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETLL_POC-2.0.jar \
--jars /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL_POC-2.0.jar \
--conf "spark.driver.maxResultSize=5G" \
--conf "spark.port.maxRetries=100" \
--conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" \
--conf "spark.yarn.security.tokens.hbase.enabled=true" \
--conf "spark.dynamicAllocation.enabled=true" \
--class "com.anthem.cogx.pi.etl.Driver" \
/data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETLL_POC-2.0.jar cogx_um_load



------------------------------

Step 7 - Run hbase code on server as cluster mode
-------------------

spark2-submit --master yarn --deploy-mode cluster  \
--files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml \
--name "COGX HBase ETL: cogx_um_load - by AF35352" \
--principal AF35352 \
--keytab /home/af35352/keytabstore/AF35352.keytab  \
--driver-memory 5G \
--executor-memory 5G \
--executor-cores 10 \
--num-executors 4 \
--driver-class-path /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar \
--jars /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar \
--conf "spark.driver.maxResultSize=5G" \
--conf "spark.port.maxRetries=100" \
--conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" \
--conf "spark.yarn.security.tokens.hbase.enabled=true" \
--conf "spark.dynamicAllocation.enabled=true" \
--class "com.anthem.cogx.pi.etl.Driver" \
/data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar cogx_um_load

------------------------------
Step 8 - fix the cluster issue 

Permenant fix for deploy-mode cluster:
	Step 1 - change code with: 
		sparkSession.sparkContext.addJar("hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-1.0.jar")
	Step 2 - add jars to hdfs
		hadoop fs -rm  /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-1.0.jar
	hadoop fs -put /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/
		
	Step 3 - run deploy-mode cluster

hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-1.0.jar

spark2-submit --master yarn --deploy-mode cluster  \
--files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml \
--name "COGX HBase ETL: cogx_um_load - by AF35352" \
--principal AF35352 \
--keytab /home/af35352/keytabstore/AF35352.keytab  \
--driver-memory 5G \
--executor-memory 5G \
--executor-cores 10 \
--num-executors 4 \
--driver-class-path hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-1.0.jar \
--jars hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-1.0.jar \
--conf "spark.driver.maxResultSize=5G" \
--conf "spark.port.maxRetries=100" \
--conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" \
--conf "spark.yarn.security.tokens.hbase.enabled=true" \
--conf "spark.dynamicAllocation.enabled=true" \
--class "com.anthem.cogx.pi.etl.Driver" \
hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-1.0.jar cogx_um_load
--------------------------------------

	Step 1 - SFTP:
		From: C:\java\git\repos\cognitiveclaim_dev\ds-cogx-batch\sprint-03\ds-cogx-batch\target\cogxHDFSHBaseETL-sprint-03-3.0.jar
		To: /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/
	Step 2 - add jars to hdfs
		hadoop fs -rm  /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-sprint-03-3.0.jar
	hadoop fs -put /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-sprint-03-3.0.jar /dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/
		

spark2-submit --master yarn --deploy-mode client  \
--files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml \
--name "COGX HBase ETL: cogx_um_load - by AF35352" \
--principal AF35352 \
--keytab /home/af35352/keytabstore/AF35352.keytab  \
--driver-memory 5G \
--executor-memory 5G \
--executor-cores 10 \
--num-executors 4 \
--driver-class-path /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-sprint-03-3.0.jar \
--jars /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-sprint-03-3.0.jar \
--conf "spark.driver.maxResultSize=5G" \
--conf "spark.port.maxRetries=100" \
--conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" \
--conf "spark.yarn.security.tokens.hbase.enabled=true" \
--conf "spark.dynamicAllocation.enabled=true" \
--class "com.anthem.cogx.pi.etl.DriverWithoutYaml" \
/data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-sprint-03-3.0.jar cogx_um_load


You can kill the spark appid using below command: 
yarn application -kill application_1558649704445_1720


spark2-submit --master yarn --deploy-mode cluster  \
--files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml \
--name "COGX HBase ETL: cogx_um_load - by AF35352" \
--principal AF35352 \
--keytab /home/af35352/keytabstore/AF35352.keytab  \
--driver-memory 5G \
--executor-memory 5G \
--executor-cores 10 \
--num-executors 4 \
--driver-class-path hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-sprint-03-3.0.jar \
--jars hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-sprint-03-3.0.jar \
--conf "spark.driver.maxResultSize=5G" \
--conf "spark.port.maxRetries=100" \
--conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" \
--conf "spark.yarn.security.tokens.hbase.enabled=true" \
--conf "spark.dynamicAllocation.enabled=true" \
--class "com.anthem.cogx.pi.etl.DriverWithoutYaml" \
hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-sprint-03-3.0.jar cogx_um_load

https://dwbdtest1r1m.wellpoint.com:8090/cluster/app/application_1558649704445_1720 
application_1558649704445_1720 
-----------------------------

Error:
----------------
Exception in thread "main" org.apache.hadoop.security.KerberosAuthException: Login failure for user: af35352 from keytab /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/keytabstore/af35352.keytab javax.security.auth.login.LoginException: Pe-authentication information was invalid (24)
        at org.apache.hadoop.security.UserGroupInformation.loginUserFromKeytab(UserGroupInformation.java:1130)
        at org.apache.spark.deploy.SparkSubmit$.doPrepareSubmitEnvironment(SparkSubmit.scala:397)
        at org.apache.spark.deploy.SparkSubmit$.prepareSubmitEnvironment(SparkSubmit.scala:250)
        at org.apache.spark.deploy.SparkSubmit$.submit(SparkSubmit.scala:171)
        at org.apache.spark.deploy.SparkSubmit$.main(SparkSubmit.scala:137)
        at org.apache.spark.deploy.SparkSubmit.main(SparkSubmit.scala)

-------------------------------------------
Fix:
-----------------------------------------------------------------
Setup personal keytab
------------------------------------------
ktutil
addent -password -p AF35352@DEVAD.WELLPOINT.COM -k 1 -e rc4-hmac
 - Password for AF35352@DEVAD.WELLPOINT.COM: Enter Password for your Domain ID
addent -password -p AF35352@DEVAD.WELLPOINT.COM -k 1 -e aes256-cts
 - Password for AF35352@DEVAD.WELLPOINT.COM: Enter Password for your Domain ID
ktutil:  wkt AF35352.keytab
ktutil:  quit
kinit AF35352@DEVAD.WELLPOINT.COM -k -t AF35352.keytab
klist
-------------------------------------


--------------------------------
-sh-4.1$ spark2-submit --master yarn --deploy-mode client  --files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml --name "COGX HBase ETL: cogx_um_load - by AF35352" --driver-memory 5G --executor-memory 5G --executor-cores 10 --num-executors 4 --conf "spark.driver.maxResultSize=5G" --conf "spark.port.maxRetries=100" --conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" --conf "spark.yarn.security.tokens.hbase.enabled=true" --conf "spark.dynamicAllocation.enabled=true" --class "com.anthem.cogx.pi.etl.Driver" /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar cogx_um_load
[info] Unravel Sensor 4.3.1.5b0044/1.3.10.2-unravel-1703 initializing.
Exception in thread "main" Can't construct a java object for tag:yaml.org,2002:com.anthem.etl.config.TableConfig; exception=Class not found: com.anthem.etl.config.TableConfig
 in "<reader>", line 2, column 5:
    --- !!com.anthem.etl.config.TableConfig
        ^

        at org.yaml.snakeyaml.constructor.Constructor$ConstructYamlObject.construct(Constructor.java:333)
        at org.yaml.snakeyaml.constructor.BaseConstructor.constructObject(BaseConstructor.java:183)
        at org.yaml.snakeyaml.constructor.BaseConstructor.constructDocument(BaseConstructor.java:142)

-------------------------------------------------------
Fix: Need to make the jar available by pushing to --driver-class-path => This solution only work on deploy-mode client 

--driver-class-path /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar \
--jars /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-1.0.jar \
-----------------------------------------------------------
Permenant fix for deploy-mode cluster:
	Step 1 - change code with: 
		sparkSession.sparkContext.addJar("hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/af35352Test/cogxHDFSHBaseETL-1.0.jar")
	Step 2 - add jars to hdfs
	Step 3 - run deploy-mode cluster
-------------------------------------------------------------------


Error: 
=============================================================
missing teradata jars:
Fix:
Manually download the teradata jars from teradata homepage => add the Maven repo
C:\workstation\PADP\Project\Python_cognitiveClaim\Teradata => download  teradata driver
========================================================


Error:
===============================
Lost task 0.1 in stage 0.0 (TID 1, dwbdtest1r5w6.wellpoint.com, executor 1): java.sql.SQLException: [Teradata Database] [TeraJDBC 16.10.00.07] [Error 2646] [SQLState HY000] No more spool space in AF35352.
-------------------------------------
Issue:
-------------------
Query consume too much resource
------------------------
Fix: add the following to limit the output:
--------------
line 93:  and R.CLNCL_SOR_CD like '11%'
	- CogxUMHbaseLoad.scala
	- This will limit the output as 10 rows
--------------------------------
add the following con
=======================================

To make JDBC login works:
===============================================
Step 1 - Logic the Teradata through DWTEST3_LDAP 
	- login using Teradata SQL assistence 
Step 2 - issue the following command
	modify user AF35352 as password = "******" 
	- This works
Step 3 - Wait for the next update of the username and password, then use the following connection string
	jdbc:teradata://DWTEST3COP1.WELLPOINT.COM/database=T89_ETL_VIEWS_ENT
	- Test use DBVisualizer or other 3rd party JDBC connector

Notes and support:
==============================================================
unlock teradata
https://myprofile.antheminc.com/PASS/psf.exe 


Teradata admin team
dl-IM-Teradata-Offshore-App-DBA <dl-im-teradata-application-team@anthem.com> 
Teradata Dba-ANTHEM <Teradata.Dba@anthem.com>
=================================================================



Trouble shooting link:
Hadoop History
https://dwbdtest1r1m.wellpoint.com:8090/cluster/app/application_1557843199409_0132
Hue Job browser
https://dwbdtest1r1e.wellpoint.com:8890/hue/jobbrowser


Verify HBase:
scan "dv_hb_ccpcogx_gbd_r000_in:um_auth"
scan "ts_hb_ccpcogx_gbd_r000_in:um_auth"
scan 'dv_hb_ccpcogx_gbd_r000_in:um_auth' , {COLUMNS => 'um:jsonData' , FILTER => "ValueFilter (=, 'substring:\"rfrnc_nbr\"\:\"82876996\"')"}
scan 'ts_hb_ccpcogx_gbd_r000_in:um_auth' 
scan 'dv_hb_ccpcogx_gbd_r000_in:um_auth' , FILTER=>"SingleColumnValueFilter ('','', =, 'regexstring:.*123186519*'  )"



scan 'dv_hb_ccpcogx_gbd_r000_in:um_auth' , { FILTER => " (TimestampsFilter ( 1556848066000, 1557848066000))"}


==================================
Discover cluster edgenode
dwbddisc1r1e.wellpoint.com 
===================================

yaml Work round solution:
1 - Replace the TableCOnfig => TableConfigJava
2 - Manually load the file 

==========================================
--conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" \
--conf "spark.yarn.security.tokens.hbase.enabled=true" \

spark2-submit --master local[*]   \
--files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml \
--name "COGX HBase ETL: cogx_um_load - by AF35352" \
--principal AF35352 \
--keytab /home/af35352/keytabstore/AF35352.keytab  \
--driver-class-path /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-sprint-03-3.0.jar \
--jars /data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-sprint-03-3.0.jar \
--class "com.anthem.cogx.pi.etl.DriverWithoutYaml" \
/data/01/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/etlHbase/cogxHDFSHBaseETL-sprint-03-3.0.jar cogx_um_load
===========================================
