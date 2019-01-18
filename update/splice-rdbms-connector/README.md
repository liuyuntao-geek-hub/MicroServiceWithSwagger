SPLICE-RDBMS-CONNECTOR

//hios 


=============================================================================================================
Splice to SQL Server Export Utility
==============================================================================================================
spark2-submit --class com.anthem.hca.splice.export.fullrefresh.SpliceToRDBMSExportDriver --master yarn --deploy-mode cluster \
--queue splice_yarn \
--packages com.typesafe:config:1.2.1  \
--conf spark.yarn.maxAppAttempts=1 \
--driver-memory 20G  --driver-cores 5 --conf spark.yarn.driver.memoryOverhead=2048 --conf spark.driver.maxResultSize=3g \
--conf spark.dynamicAllocation.enabled=false \
--num-executors 40 --executor-memory 5G --executor-cores 4  --conf spark.yarn.executor.memoryOverhead=2048 \
--conf spark.network.timeout=600 --conf spark.port.maxRetries=40 --conf spark.sql.broadcastTimeout=4800 \
--conf 'spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j.xml  -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12' \
--conf 'spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j.xml -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12'  \
--conf 'spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--conf 'spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--keytab /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab --principal srcpdpspcpbthdv@DEVAD.WELLPOINT.COM \
--files /etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/krb5.conf \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1826a.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1826a.jar,/var/lib/sqoop/jtds-patched-1.3.1.jar \
--name spcp-etl-export  splice-rdbms-connector-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/  ZZ_MEMBER_DEMO

=============================================================================================================
keytool command for encrytion
=============================================================================================================
keytool -genseckey -keystore /home/af69961/aes-keystore.jck -storetype jceks -storepass sqooptest -keyalg AES -keysize 128 -alias sqooptest -keypass sqooptest