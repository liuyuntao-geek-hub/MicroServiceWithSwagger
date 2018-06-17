*** This is the package to test local environment and cluster environment ***
*** Only basic test included ***

Section 1 - Run Template on Local
Step 0 - Change the code in com.anthem.hpip.config.Spark2Config 
	- pick up the local run section 
	- Check the comments in the code com.anthem.hpip.config.Spark2Config 
	
Step 1 - Run com.anthem.hpip.Template.TemplateDriver within eClipse with the following Parameter

	C:\java\git\repos\coe_2.0\conf local TemplateLocal

Step 2 - Before to deploy to cluster: 
	*** reverse back the code in com.anthem.hpip.config.Spark2Config
	*** Forget to do this will cause mode conflict issue and fail on --deploymode cluster without explict Error


===============================================================
Step 1 - copy file to server:
FTP: 
From:
C:\java\git\repos\coe_2.0\target\CenterOfExcellence-2.0.0.jar
To:
/data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar

---------------------------------------------------------------
Step 2 - FTP CenterOfExcellence-2.0.0.jar
From:
    ../target/CenterOfExcellence-2.0.0.jar
    C:\java\maven\localrepoNew\com\splicemachine\db-client\2.5.0.1806S\db-client-2.5.0.1806S.jar
    C:\java\maven\localrepoNew\com\splicemachine\db-client\2.5.0.1819\db-client-2.5.0.1819.jar
    C:\java\maven\localrepoNew\com\splicemachine\hbase_sql-cdh5.12.2\2.5.0.1819\hbase_sql-cdh5.12.2-2.5.0.1819.jar
    C:\java\maven\localrepoNew\com\splicemachine\splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11\2.5.0.1819\splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar
    C:\java\maven\localrepoNew\com\splicemachine\hbase_storage-cdh5.12.2\2.5.0.1819\hbase_storage-cdh5.12.2-2.5.0.1819.jar
    
    
To:
    /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar
    /data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/db-client-2.5.0.1806S.jar
    /data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar
    /data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/hbase_sql-cdh5.12.2-2.5.0.1819.jar
    /data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar
    /data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/hbase_storage-cdh5.12.2-2.5.0.1819.jar
    

Push to HDFS
cd /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/hbase_sql-cdh5.12.2-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/hbase_storage-cdh5.12.2-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/





Step 3 - Run Spark-submit => client mode + no key tab + JDBC
(
	
)
export JAVA_HOME=/usr/java/latest/

**** spark2-shell --conf spark.ui.port=5052
--------- This works with or without JCEKS encryption ---
--------- This works on client mode - Cluster mode has issues exitCode 0 ------------
spark2-submit \
--master yarn \
--deploy-mode client \
--name SPCP_TEST_JDBC_JCEKS_ClientMode \
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
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class  com.anthem.hpip.Template.SpliceMachine.FirstSpliceJDBCSpark \
hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar 


hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/hbase_sql-cdh5.12.2-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/hbase_storage-cdh5.12.2-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/


--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/home/srcpdpspfibthdv/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,/home/srcpdpspfibthdv/db-client-2.5.0.1819.jar \
--jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/data/01/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/splicemachine-cdh5.8.3-2.2.0.cloudera2_2.11-2.5.0.1806S.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/db-client-2.5.0.1806S.jar  \


Step 4 - Generate Keytab

cd 
cd keyHome
chmod 755 keyHome/
ktutil
ktutil: addent -password -p srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -k 1 -e aes256-cts
ktutil: addent -password -p af35352@DEVAD.WELLPOINT.COM -k 1 -e aes256-cts
Password for srcpdpspcpbthdv@DEVAD.WELLPOINT.COM:********
ktutil: wkt srcpdpspcpbthdv.keytab
ktutil: wkt af35352.keytab
ktutil: exit

cd 
cd keyHome
chmod 755 keyHome/
ktutil
ktutil: addent -password -p srcpdpspcpbthts@DEVAD.WELLPOINT.COM -k 1 -e aes256-cts
Password for srcpdpspcpbthts@DEVAD.WELLPOINT.COM:********
ktutil: wkt srcpdpspcpbthts.keytab

ktutil: exit



Step 5 - spark submit with JDBC with Keytab
(IF there is now JCEKS encryption : Keytab owner is different from JDBC user = still working )
(This only work if we are not using JCEKS to do encrytpion.)
(Encryption with JCEKS: 
	- If there is Keytab = Require the keytab owner is same as splice login 
	- If there is no keytab = works even if keytab owner is different from splice login
)


----------- The following give error message: org.apache.hadoop.security.KerberosAuthException ---------------------------
 Login failure for user: af35352@DEVAD.WELLPOINT.COM from keytab /home/af35352/keyHome/af35352.keytab javax.security.auth.login.LoginException: Pre-authentication information was invalid (24)
----------- Using af35352 => kerberos not having authorization to use keytab ------------------
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
--conf spark.dynamicAllocation.initialExecutors=10 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=10 \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,/etc/spark/conf/log4j.properties,/etc/krb5.conf \
--keytab "/home/af35352/keyHome/af35352.keytab"  \
--principal "af35352@DEVAD.WELLPOINT.COM" \
--conf "spark.io.compression.codec=org.apache.spark.io.SnappyCompressionCodec" \
--conf "spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=af35352@DEVAD.af35352.COM -Dspark.yarn.keytab=/home/af35352/keyHome/af35352.keytab -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--conf "spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class  com.anthem.hpip.Template.SpliceMachine.FirstSpliceJDBCSpark \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar 


------------------------ Using Keytab + JCEKS = same login (Dev ID) ---------------------
spark2-submit \
--master yarn \
--deploy-mode client \
--name spcp_keytab_JCEKS_JDBC_Client \
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
--conf spark.dynamicAllocation.initialExecutors=10 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=10 \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,/etc/spark/conf/log4j.properties,/etc/krb5.conf \
--keytab "/home/af35352/keyHome/srcpdpspcpbthdv.keytab"  \
--principal "srcpdpspcpbthdv@DEVAD.WELLPOINT.COM" \
--conf "spark.io.compression.codec=org.apache.spark.io.SnappyCompressionCodec" \
--conf "spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.af35352.COM -Dspark.yarn.keytab=/home/af35352/keyHome/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--conf "spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class  com.anthem.hpip.Template.SpliceMachine.FirstSpliceJDBCSpark \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar 




Step 6 - Spark Submit - SparkSpliceAdapter + Keytab + local jars + no encryption using JCEKS (THIS job is disabled - created only for testing once)

spark2-submit \
--master yarn \
--deploy-mode client \
--name SPCP_SparkAdapter_ClientMode_Keytab_JCEKS_Disabled \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 10 \
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
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,/etc/spark/conf/log4j.properties,/etc/krb5.conf \
--conf "spark.io.compression.codec=org.apache.spark.io.SnappyCompressionCodec" \
--conf "spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/home/af35352/keyHome/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--conf "spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*" \
--conf "spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*"   \
--keytab "/home/af35352/keyHome/srcpdpspcpbthdv.keytab"  \
--principal "srcpdpspcpbthdv@DEVAD.WELLPOINT.COM" \
--class  com.anthem.hpip.Template.SpliceMachine.FirstSpliceSparkNoEncrypt_disabled \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar 


Step 7 - add jceks store credentials + Keytab + SparkAdapter + Select
	hadoop credential create dev849pass -provider jceks://hdfs//dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin//dev849.jceks
	hadoop credential create dev849pass -provider jceks://hdfs//dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin//dev849.jceks
	hadoop credential create af35352pass -provider jceks://hdfs//dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin//af35352.jceks

in Code:
------------------
    val dbUrl = "jdbc:splice://dwbdtest1r5w1.wellpoint.com:1527/splicedb;user=srcpdpspcpbthdv;password="
    val splicemachineContext = new SplicemachineContext(dbUrl.concat(getCredentialSecret("jceks://hdfs//dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin//dev849.jceks","dev849pass")))
   ///// ******* the jceks file need to be inside spcp hdfs folder (//hdfs//dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin), otherwise it give permission issue
   //// even though, the job can read jars from other hdfs folders

    def getCredentialSecret (aCredentialStore: String, aCredentialAlias: String): String ={
      val config = new org.apache.hadoop.conf.Configuration()
      config.set(org.apache.hadoop.security.alias.CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH, aCredentialStore)
      String.valueOf(config.getPassword(aCredentialAlias))

    }
--------------------

Step 8 - Run jceks + keytab + Sparkadapter + select + use JCEKS (*** In performance test ***)
(
	Keytab
	JCEKS
	Spark Adapter
	Select Only
) 
spark2-submit \
--master yarn \
--deploy-mode client \
--name SPCP_SparkAdapter_ClientMode_Keytab_JCEKS \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 10 \
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
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,/dv/app/ve2/pdp/spfi/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,/etc/spark/conf/log4j.properties,/etc/krb5.conf \
--conf "spark.io.compression.codec=org.apache.spark.io.SnappyCompressionCodec" \
--conf "spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/home/af35352/keyHome/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--conf "spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*" \
--conf "spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*"   \
--keytab "/home/af35352/keyHome/srcpdpspcpbthdv.keytab"  \
--principal "srcpdpspcpbthdv@DEVAD.WELLPOINT.COM" \
--class  com.anthem.hpip.Template.SpliceMachine.FirstSpliceSpark \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar 


=================================================================

===========================================================
Section 2 - Run Template on Cluster 
	- Client mode first 
	- cluster mode second

================================================================

Step 1 - Prepare files to SPCP

hadoop fs -put -f /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/hbase_sql-cdh5.12.2-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/hbase_storage-cdh5.12.2-2.5.0.1819.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/
hadoop fs -put -f /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control



JDBC cluster mode
------------------------------------
Pending to be resolved
Application application_1527038069742_2865 failed 1 times due to AM Container for appattempt_1527038069742_2865_000001 exited with exitCode: 0 
--------------------------------------
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name SPCP_TEST_JDBC_JCEKS_ClusterMode \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 10 \
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
--jars hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar \
--class  com.anthem.hpip.Template.SpliceMachine.FirstSpliceJDBCSpark \
hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar 


------------- JDBC + KeyTab + JCEKS + Cluster mode ---
-------- Still give exitcode 0 issue -----------------
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name spcp_keytab_JCEKS_JDBC_Cluster \
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
--conf spark.dynamicAllocation.initialExecutors=10 \
--conf spark.dynamicAllocation.maxExecutors=100 \
--conf spark.dynamicAllocation.minExecutors=10 \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,/etc/spark/conf/log4j.properties,/etc/krb5.conf \
--keytab "/home/af35352/keyHome/srcpdpspcpbthdv.keytab"  \
--principal "srcpdpspcpbthdv@DEVAD.WELLPOINT.COM" \
--conf "spark.io.compression.codec=org.apache.spark.io.SnappyCompressionCodec" \
--conf "spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.af35352.COM -Dspark.yarn.keytab=/home/af35352/keyHome/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--conf "spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class  com.anthem.hpip.Template.SpliceMachine.FirstSpliceJDBCSpark \
hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar 



hadoop fs -put -f /home/af35352/keyHome/srcpdpspcpbthdv.keytab /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/



----------------------- Keytab + Adapter + JCEKS ------------------
------------------ Issues: no log ------------------
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name SPCP_Clustermode_Keytab_JCEKS_Adapter_Sample \
--driver-cores 5 \
--driver-memory 8G \
--num-executors 10 \
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
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1819.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/db-client-2.5.0.1819.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,/etc/spark/conf/log4j.properties,/etc/krb5.conf \
--conf "spark.io.compression.codec=org.apache.spark.io.SnappyCompressionCodec" \
--conf "spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/home/af35352/keyHome/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--conf "spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j-spark.properties -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12" \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*" \
--conf "spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*"   \
--keytab "/home/af35352/keyHome/srcpdpspcpbthdv.keytab"  \
--principal "srcpdpspcpbthdv@DEVAD.WELLPOINT.COM" \
--class  com.anthem.hpip.Template.SpliceMachine.FirstSpliceSpark \
hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar 








