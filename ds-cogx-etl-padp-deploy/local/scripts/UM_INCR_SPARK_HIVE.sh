#====================================================================================================
#!/bin/sh
# Title            : UM_HIST_SPARK
# ProjectName      : COGX
# Filename         : UM_HIST_SPARK.sh
# Developer        : Anthem
# Created on       : JUN 2019
# Location         : ATLANTA                                                                                
#=================================================================================================================

#Load the bash profile/ .profile/
source $HOME/.bash_profile
source $HOME/.profile

#Defaulting the profile variables for non-service ids
#Defaulting the profile variables for non-service ids
if [ "$(whoami)" != "srcccpcogxbthpr" ]; then
        CONTROL_DIR="/pr/app/ve2/ccp/cogx/phi/no_gbd/r000/bin/control"
        HDFS_DIR="/pr/hdfsapp/ve2/ccp/cogx/phi/no_gbd/r000/bin/"
        BIN_DIR="/pr/app/ve2/ccp/cogx/phi/no_gbd/r000/bin/"
        LOG_DIR="/pr/app/ve2/ccp/cogx/phi/no_gbd/r000/bin/logs"
fi

#Load generic functions/Variables
if [ -f $CONTROL_DIR/application_shell.properties ];
then
        source $CONTROL_DIR/application_shell.properties
else
        echo "ERROR - application_shell.properties not available in $CONTROL_DIR"
        exit -1
fi


#VARIABLES:
DRIVER_CLASS="CogxHiveUMDriver"
SUBJECT_AREA="$DRIVER_CLASS"
status="SUCCEEDED"

#Creating log file
LOG_FILE=$LOG_DIR/"script_"$SUBJECT_AREA"_"$(v_DATETIME)"_"$USER.log
YARN_LOG_SMRY_FILE=$LOG_DIR/$SUBJECT_AREA"_"$(v_DATETIME)"_"$USER.log

exec 1> $LOG_FILE 2>&1

echo "$(v_TIMESTAMP):INFO:NDO ETL AUDIT -$SUBJECT_AREA Script logs in  $LOG_FILE"
echo "$(v_TIMESTAMP):INFO:NDO ETL AUDIT wrapper triggered">>$LOG_FILE
echo "$(v_TIMESTAMP):INFO:NDO  \
        SUBJECT_AREA-$SUBJECT_AREA \
        DRIVER_CLASS-$DRIVER_CLASS \
        ">>$LOG_FILE

#=========================================================================================================

# Run spark submit command
export JAVA_HOME=/usr/java/latest
exec
spark2-submit --master yarn --queue ndo_coca_yarn --deploy-mode cluster --executor-memory 40G --executor-cores 2 --driver-cores 2 --driver-memory 30G --name "Cogx_ETL_UM" --files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml --conf spark.sql.codegen.wholeStage=true --conf spark.yarn.maxAppAttempts=1 --conf spark.driver.memoryOverhead=5120 --conf spark.sql.parquet.cacheMetadata=false --conf spark.yarn.executor.memoryOverhead=16384 --conf spark.network.timeout=420000 --conf "spark.driver.maxResultSize=15g" --conf "spark.default.parallelism=1000" --conf spark.kryoserializer.buffer.max=1024m --conf spark.rpc.message.maxSize=1024 --conf spark.sql.broadcastTimeout=5800 --conf spark.executor.heartbeatInterval=30s --conf spark.dynamicAllocation.executorIdleTimeout=90 --conf spark.dynamicAllocation.initialExecutors=0 --conf spark.dynamicAllocation.maxExecutors=100 --conf spark.dynamicAllocation.minExecutors=30 --conf spark.sql.autoBroadcastJoinThreshold=-1 --conf spark.sql.cbo.enabled=true --conf "spark.yarn.security.tokens.hbase.enabled=true" --conf "spark.sql.shuffle.partitions=4200" --files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml,/opt/cloudera/parcels/CDH/lib/hbase/conf/hbase-site.xml --jars /opt/cloudera/parcels/CDH/jars/hive-contrib-1.1.0-cdh5.12.2.jar --jars /usr/lib/tdch/1.5/lib/terajdbc4.jar,/usr/lib/tdch/1.5/lib/tdgssconfig.jar --class com.anthem.cogx.etl.HiveUM.${DRIVER_CLASS} hdfs://${HDFS_DIR}/jar/$COGX_ETL_JAR  hdfs://${HDFS_DIR}/ $v_ENV cogxHiveUMIncremental >$YARN_LOG_SMRY_FILE 2>&1

#=================================================================================================================

#Get spark application URL

application_url=`grep tracking  $YARN_LOG_SMRY_FILE|head -1`
application_id=$(echo $application_url | sed 's:/*$::')
application_name=`echo $application_id| rev | cut -d'/' -f 1 | rev`

echo "$(v_TIMESTAMP):INFO: Application URL :$application_url" >>$LOG_FILE
echo "$(v_TIMESTAMP):INFO: Application Name : $application_name" >>$LOG_FILE

YARN_LOG_FILE=$LOG_DIR/$SUBJECT_AREA"_yarn_log_"${application_name}.log
TMP_YARN_LOG_FILE=$LOG_DIR/temp_app_details_$SUBJECT_AREA"_"$now"_"$USER.txt

#path to log file
yarn logs -applicationId ${application_name} >echo "$(v_TIMESTAMP):INFO:Yarn Log file : $YARN_LOG_FILE"  >>$LOG_FILE

#Get application status details and save in temp file
yarn application --status $application_name >$TMP_YARN_LOG_FILE

#Get the application final status
app_status=`grep Final-State $TMP_YARN_LOG_FILE`
yarn logs -applicationId ${application_name} >$YARN_LOG_FILE
final_app_status=`echo $app_status|rev | cut -d':' -f 1 | rev|tail -1`


#Compare application status
if [ $final_app_status  ==  $status ]
then
echo "$(v_TIMESTAMP):INFO: Spark Job for "$SUBJECT_AREA" COMPLETED." >>$LOG_FILE
rm $TMP_YARN_LOG_FILE
exit $v_SUCCESS
else
echo "$(v_TIMESTAMP):INFO: Spark Job Failed for "$SUBJECT_AREA"" >>$LOG_FILE
exit $v_ERROR
fi


