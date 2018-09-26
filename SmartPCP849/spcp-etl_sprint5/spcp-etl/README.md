SPCP ETL - (Smart Primary Care Provider/Physician - )

--queue splice_yarn \

spark2-submit --class com.anthem.hca.spcp.pcpgeocode.PCPGeoCodeDriver --master yarn --deploy-mode cluster \
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
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1825a.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1825a.jar \
--name spcp-etl-pcpgeocode  spcp-etl-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/ dev spcp

=============================================================================================================
MemberCodeDriver
==============================================================================================================
spark2-submit --class com.anthem.hca.spcp.member.MemberCodeDriver --master yarn --deploy-mode cluster \
--queue splice_yarn \
--packages com.typesafe:config:1.2.1  \
--conf spark.yarn.maxAppAttempts=1 \
--driver-memory 20G  --driver-cores 5 --conf spark.yarn.driver.memoryOverhead=2048 --conf spark.driver.maxResultSize=3g \
--conf spark.dynamicAllocation.enabled=false \
--num-executors 30 --executor-memory 30G --executor-cores 4  --conf spark.yarn.executor.memoryOverhead=2048 \
--conf spark.network.timeout=600 --conf spark.port.maxRetries=40 --conf spark.sql.broadcastTimeout=4800 \
--conf 'spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j.xml  -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12' \
--conf 'spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j.xml -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12'  \
--conf 'spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--conf 'spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--keytab /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab --principal srcpdpspcpbthdv@DEVAD.WELLPOINT.COM \
--files /etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/krb5.conf \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1822.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1822.jar \
--name spcp-etl-member  spcp-etl-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/ dev spcp



spark2-submit --class com.anthem.hca.spcp.member.MemberCodeDriver --master yarn --deploy-mode cluster \
--queue splice_yarn \
--packages com.typesafe:config:1.2.1  \
--conf spark.yarn.maxAppAttempts=1 \
--driver-memory 20G  --driver-cores 5 --conf spark.yarn.driver.memoryOverhead=2048 --conf spark.driver.maxResultSize=3g \
--conf spark.dynamicAllocation.enabled=false \
--num-executors 30 --executor-memory 30G --executor-cores 4  --conf spark.yarn.executor.memoryOverhead=2048 \
--conf spark.network.timeout=600 --conf spark.port.maxRetries=40  \
--conf 'spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=srcpdpspcpbthdv@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab -Dlog4j.configuration=log4j.xml  -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12' \
--conf 'spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j.xml -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12'  \
--conf 'spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--conf 'spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--keytab /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/srcpdpspcpbthdv.keytab --principal srcpdpspcpbthdv@DEVAD.WELLPOINT.COM \
--files /etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/log4j.xml,/etc/krb5.conf \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1822.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1822.jar \
--name spcp-etl-member  spcp-etl-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/ dev spcp


--conf spark.sql.broadcastTimeout=4800 
	- not broadcast large table
	- IF join two dataframes = not use broadcast
	- increase broadcast time = delay the problem 
		- Need to address the real problem 
	- 4800 seconds => this not miliseconds
	- sortmerge might be the solution = consider different join 


- Executor = JVM = no more that 4 cores 
		- cores = Parallel task inside the single JVM
		- the more core = more mem pressure
		
- more executors -> less cores 
	- start with 2 cores 
	- make sure you have enough task executors by increasing executors
	- unless using lots of shuffling = using shuffling a lot => need more cores
	
- never create more executor-memory than necessary 
		- 15% - 20% of the executor-memory = executor-memory overhead
		- 15% - 20% of the driver-memory = driver-memory overhead
		- node not have enough memory to cover the executor need = waste Mem
	
- Shuffle partitions = increase shuffle partitions (Default 200 -> 400 -> 2000) 
		- each processing will have less pressure
		- df.repartition()
	--conf spark.sql.shuffle.partion
		
- decrease executor core 2
	- increase executors 

- not cache RDD unless use it more than once 
	

--conf spark.ui.port
	
	
	
	
		
	
	

======================================================================================================================
=======================================================================================================================
spark2-submit --class com.anthem.hca.spcp.export.ExportDriver --master yarn --deploy-mode client \
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
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1824.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1824.jar \
--name spcp-etl-export  spcp-etl-1.0.0.jar /dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/ dev spcp