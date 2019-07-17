

#====================================================================================================
#!/bin/sh
# Title            : NDO_AUDIT_LOAD
# ProjectName      : NDO
# Filename         : NDO_AUDIT_LOAD.sh
# Developer        : Anthem
# Created on       : FEB 2018
# Location         : ATLANTA
# Date           Auth           Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2018/10/01     Deloitte         1     Initial Version
#====================================================================================================                                                                                         
#=================================================================================================================

#Load the bash profile/ .profile/
source $HOME/.bash_profile
source $HOME/.profile
spark2-submit --master yarn --queue ndo_coca_yarn --deploy-mode cluster --executor-memory 40G --executor-cores 2 --driver-cores 2 --driver-memory 30G --name "COGX HBase ETL: cogx_um_load - by ag21866" --files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml --conf spark.sql.codegen.wholeStage=true --conf spark.yarn.maxAppAttempts=1 --conf spark.driver.memoryOverhead=5120 --conf spark.sql.parquet.cacheMetadata=false --conf spark.yarn.executor.memoryOverhead=16384 --conf spark.network.timeout=420000 --conf "spark.driver.maxResultSize=15g" --conf "spark.default.parallelism=1000" --conf spark.kryoserializer.buffer.max=1024m --conf spark.rpc.message.maxSize=1024 --conf spark.sql.broadcastTimeout=5800 --conf spark.executor.heartbeatInterval=30s --conf spark.dynamicAllocation.executorIdleTimeout=90 --conf spark.dynamicAllocation.initialExecutors=0 --conf spark.dynamicAllocation.maxExecutors=100 --conf spark.dynamicAllocation.minExecutors=30 --conf spark.sql.autoBroadcastJoinThreshold=-1 --conf spark.sql.cbo.enabled=true --conf "spark.yarn.security.tokens.hbase.enabled=true" --conf "spark.sql.shuffle.partitions=4200" --files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml,/opt/cloudera/parcels/CDH/lib/hbase/conf/hbase-site.xml --jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar --jars /usr/lib/tdch/1.5/lib/terajdbc4.jar,/usr/lib/tdch/1.5/lib/tdgssconfig.jar --class com.anthem.cogx.etl.HiveUM.CogxHiveUMDriver hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/no_gbd/r000/control/ag21866Test/ds-cogx-padp-etl-2.0.0.jar hdfs:///dv/hdfsapp/ve2/ccp/cogx/phi/no_gbd/r000/control/ag21866Test dev cogxHiveUMHistory
