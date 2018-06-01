
-- Continue from template_readme.com
==================================================================
Section 3 - CSV & Hive to Hive
---------------------------------------------------------------
Step 1 - Run ddl 
	-{project path}/ddl/dev/templateRequiredTable.txt
	- These tables are created as internal table
	- No partition specified to simplify the example
	- *** This item need further refactoring
	
Step 2 - The following package cannot run local because it need to access the HIVE DB on cluster

--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver \

Step 3 - Run as client mode:
export JAVA_HOME=/usr/java/latest/
spark2-submit \
--master yarn \
--deploy-mode client \
--name COECSV-HIVE-Test-SPARK22_client \
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
--jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar  \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe

Step 4 - Run as cluster mode

--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver 

export JAVA_HOME=/usr/java/latest/
spark2-submit \
--master yarn \
--deploy-mode cluster \
--name COE-CSV-HIVE-HIVE-SPARK22_cluster \
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
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar \
--files hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/log4j.xml,/etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml \
--driver-java-options "-Dlog4j.configuration=log4j.xml" \
--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" \
--class com.anthem.hpip.JumpStart.prototype.CSVToHive.CSVToHiveDriver \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/bin/CenterOfExcellence-2.0.0.jar \
hdfs:///dv/hdfsapp/ve2/pdp/spfi/phi/no_gbd/r000/control/ dev_coe smartProviderCoe

Step 5 - Verify the job at Hue:

https://dwbdtest1r1e.wellpoint.com:8888/jobbrowser/

Step 6 - Verify the tables at Hue

-- Source Hive table: 
select * from dv_pdphpipph_nogbd_r000_in.clm  where ADJDCTN_DT BETWEEN '2016-04-01' and '2016-04-02' limit 100;
select count(*) from dv_pdphpipph_nogbd_r000_in.clm  where ADJDCTN_DT BETWEEN '2016-04-01' and '2016-04-02';
select  TRIM(CLM_ADJSTMNT_KEY) as CLM_ADJSTMNT_KEY, TRIM(MBRSHP_SOR_CD) as MBRSHP_SOR_CD, TRIM(CLM_ITS_HOST_CD) as CLM_ITS_HOST_CD, TRIM(SRVC_RNDRG_TYPE_CD) as SRVC_RNDRG_TYPE_CD, TRIM(SRC_PROV_NATL_PROV_ID) as SRC_PROV_NATL_PROV_ID, TRIM(SRC_BILLG_TAX_ID) as SRC_BILLG_TAX_ID, TRIM(MBR_KEY) as MBR_KEY, RX_FILLED_DT ,TRIM(CLM_SOR_CD) as CLM_SOR_CD from dv_pdphpipph_nogbd_r000_in.CLM where ADJDCTN_DT BETWEEN '2016-04-01' and '2016-06-30';

-- Source csv file:
/dv/hdfsdata/ve2/pdp/spfi/phi/no_gbd/r000/inbound/testdata_combined.csv

-- Target HIVE table:
select * from dv_pdpspfiph_nogbd_r000_wh.zz_phmp_mtclm limit 100;

-- Target CSV HIVE table:
select * from dv_pdpspfiph_nogbd_r000_wh.zz_phmp_customer limit 10;



