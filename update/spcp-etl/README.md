SPCP ETL - (Smart Primary Care Provider/Physician - )

=============================================================================================================
MemberInfo
==============================================================================================================
spark2-submit --class com.anthem.hca.spcp.member.MemberInfoDriver --master yarn --deploy-mode cluster \
--queue splice_yarn \
--packages com.typesafe:config:1.2.1  \
--conf spark.yarn.maxAppAttempts=1 \
--driver-memory 20G  --driver-cores 5 --conf spark.yarn.driver.memoryOverhead=2048 --conf spark.driver.maxResultSize=3g \
--conf spark.dynamicAllocation.enabled=false \
--num-executors 50 --executor-memory 20G --executor-cores 4  --conf spark.yarn.executor.memoryOverhead=2048 \
--conf spark.network.timeout=600 --conf spark.port.maxRetries=40 --conf spark.sql.broadcastTimeout=4800 \
--conf 'spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j.xml  -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12' \
--conf 'spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j.xml -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12'  \
--conf 'spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--conf 'spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--keytab /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab --principal srcpdpspcpbthdv@DEVAD.WELLPOINT.COM \
--files /etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/krb5.conf \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1828.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1828.jar \
--name spcp-etl-member  spcp-etl-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/ dev spcp


Stats:-
Inbound volume of data: (only major tables after filter if pushdown query is used)  
	-Member has ~160 million  ((actual data is ~300 million)
	-MPE has ~300 million (actual data is ~1.7 billion)
	-Affinity Attibution ~100 Mill
DAG : https://dwbdtest1r2m3.wellpoint.com:18489/history/application_1530144680210_1632/1/SQL/execution/?id=1

Duration: 34 min
Application URL:https://dwbdtest1r1e.wellpoint.com:8890/jobbrowser/jobs/application_1530144680210_1632
VCore seconds	404271
Memory seconds	2265735325



=============================================================================================================
ProviderInfo  
==============================================================================================================
spark2-submit --class com.anthem.hca.spcp.provider.providerinfo.ProviderDriver --master yarn --deploy-mode cluster \
--queue splice_yarn \
--packages com.typesafe:config:1.2.1  \
--conf spark.yarn.maxAppAttempts=1 \
--driver-memory 20G  --driver-cores 5 --conf spark.yarn.driver.memoryOverhead=2048 --conf spark.driver.maxResultSize=3g \
--conf spark.dynamicAllocation.enabled=false \
--num-executors 80 --executor-memory 30G --executor-cores 4  --conf spark.yarn.executor.memoryOverhead=2048 \
--conf spark.network.timeout=600 --conf spark.port.maxRetries=40 --conf spark.sql.broadcastTimeout=4800 \
--conf 'spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j.xml  -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12' \
--conf 'spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j.xml -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12'  \
--conf 'spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--conf 'spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--keytab /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab --principal srcpdpspcpbthdv@DEVAD.WELLPOINT.COM \
--files /etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/krb5.conf \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1828.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1828.jar \
--name spcp-etl-provider  spcp-etl-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/ dev spcp

Stats:-
Inbound volume of data: (only major tables) 
	- https://dwbdtest1r2m3.wellpoint.com:18489/history/application_1530144680210_2315/SQL/execution/?id=1

Duration:55 minutes
Application URL:https://dwbdtest1r1e.wellpoint.com:8890/jobbrowser/jobs/application_1530144680210_2315
DAG location: https://dwbdtest1r2m3.wellpoint.com:18489/history/application_1530144680210_2315/SQL/execution/?id=1
VCore seconds	48488
Memory seconds	377130852

======================================================================================================================
Splice to SQL Server Export Utility   - This is taking only ~ 2 min to export data typically  
=======================================================================================================================
spark2-submit --class com.anthem.hca.spcp.export.Splice2RDBMSExportDriver --master yarn --deploy-mode client \
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
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1828.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1828.jar \
--name spcp-etl-export  spcp-etl-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/ dev spcp


===============================================================================================================================
PCPGeocode
================================================================================================================================
spark2-submit --class com.anthem.hca.spcp.provider.pcpgeocode.PCPGeoCodeDriver --master yarn --deploy-mode cluster \
--queue splice_yarn \
--packages com.typesafe:config:1.2.1  \
--conf spark.yarn.maxAppAttempts=1 \
--driver-memory 20G  --driver-cores 5 --conf spark.yarn.driver.memoryOverhead=2048 --conf spark.driver.maxResultSize=3g \
--conf spark.dynamicAllocation.enabled=false \
--num-executors 40 --executor-memory 10G --executor-cores 4  --conf spark.yarn.executor.memoryOverhead=2048 \
--conf spark.network.timeout=600 --conf spark.port.maxRetries=40 --conf spark.sql.broadcastTimeout=4800 \
--conf 'spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j.xml  -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12' \
--conf 'spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j.xml -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12'  \
--conf 'spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--conf 'spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--keytab /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab --principal srcpdpspcpbthdv@DEVAD.WELLPOINT.COM \
--files /etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/krb5.conf \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1828.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1828.jar \
--name spcp-etl-pcpgeocode  spcp-etl-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/ dev spcp


=============================
password check


spark2-submit --master yarn \
--class com.anthem.hca.spcp.helper.CredentialsVerifyDriver \
--keytab /ts/app/ve2/pdp/spcp/phi/no_gbd/r000/control/srcpdpspcpbthts.keytab --principal srcpdpspcpbthts@DEVAD.WELLPOINT.COM \
spcp-etl-1.0.0.jar jceks://hdfs//ts/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin//sit849splicemachine.jceks sit849pass <password>