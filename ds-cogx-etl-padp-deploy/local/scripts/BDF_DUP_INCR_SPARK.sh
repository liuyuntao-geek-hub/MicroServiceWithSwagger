#====================================================================================================
#!/bin/sh
# Title            : BDF_DUP_INCR_SPARK
# ProjectName      : COGX
# Filename         : BDF_DUP_INCR_SPARK.sh
# Developer        : Deloitte
# Created on       : JUN 2019
# Location         : ATLANTA                                                                                
#=================================================================================================================

#Load the bash profile/ .profile/
source $HOME/.bash_profile
source $HOME/.profile

#Defaulting the profile variables for non-service ids
if [ "$(whoami)" != "srcccpcogxbthpr" ]; then
        CONTROL_DIR="/pr/app/ve2/ccp/cogx/phi/gbd/r000/control"
		HDFS_DIR="/pr/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/"
		HDFS_CONTROL_DIR="/pr/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/control/"
		BIN_DIR="/pr/app/ve2/ccp/cogx/phi/gbd/r000/bin/s"
		LOG_DIR="/pr/app/ve2/ccp/cogx/phi/gbd/r000/logs"
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
DRIVER_CLASS="CogxHiveBDFDriver"
SUBJECT_AREA="$DRIVER_CLASS"
status="SUCCEEDED"

#Creating log file
LOG_FILE=$LOG_DIR/"script_"$SUBJECT_AREA"_"$(v_DATETIME)"_"$USER.log
YARN_LOG_SMRY_FILE=$LOG_DIR/$SUBJECT_AREA"_"$(v_DATETIME)"_"$USER.log
echo "YARN_LOG_SMRY_FILE - $YARN_LOG_SMRY_FILE" 

echo "$(v_TIMESTAMP):INFO:COGX ETL AUDIT -$SUBJECT_AREA Script logs in  $LOG_FILE"
echo "$(v_TIMESTAMP):INFO:COGX ETL AUDIT wrapper triggered">>$LOG_FILE
echo "$(v_TIMESTAMP):INFO:COGX  \
        SUBJECT_AREA-$SUBJECT_AREA \
        DRIVER_CLASS-$DRIVER_CLASS \
        ">>$LOG_FILE	

#=========================================================================================================

# Run spark submit command
export JAVA_HOME=/usr/java/latest
exec
spark2-submit --master yarn --deploy-mode cluster --queue ndo_coca_yarn --name "Cogx_ETL_BDF" --driver-cores 8 --driver-memory 40G --num-executors 50 --executor-memory 40G --executor-cores 8 --conf spark.yarn.maxAppAttempts=1 --conf spark.yarn.driver.memoryOverhead=2048 --conf spark.yarn.executor.memoryOverhead=16384 --conf spark.network.timeout=420000 --conf spark.driver.maxResultSize=0 --conf spark.kryoserializer.buffer.max=1024m --conf spark.rpc.message.maxSize=1024 --conf spark.sql.broadcastTimeout=4800 --conf spark.executor.heartbeatInterval=30s --conf spark.dynamicAllocation.executorIdleTimeout=180 --conf spark.dynamicAllocation.initialExecutors=30 --conf spark.dynamicAllocation.maxExecutors=100 --conf spark.dynamicAllocation.minExecutors=30 --conf spark.sql.shuffle.partitions=4200 --conf spark.dynamicAllocation.enabled=true --files /etc/alternatives/spark2-conf/yarn-conf/hive-site.xml,/opt/cloudera/parcels/CDH/lib/hbase/conf/hbase-site.xml --keytab /etc/keytabs/srcccpcogxbthpr.keytab --conf "spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/hbase/lib/" --conf "spark.yarn.security.tokens.hbase.enabled=true" --class com.anthem.cogx.etl.HiveBDF.${DRIVER_CLASS} hdfs://${HDFS_DIR}/$COGX_ETL_JAR  hdfs://${HDFS_CONTROL_DIR}/ $v_ENV cogxHiveBDFIncremental >$YARN_LOG_SMRY_FILE 2>&1

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
if [ $final_app_status  ==  $status ];
then
echo "$(v_TIMESTAMP):INFO: Spark Job for "$SUBJECT_AREA" COMPLETED." >>$LOG_FILE
rm $TMP_YARN_LOG_FILE
exit $v_SUCCESS
else
echo "$(v_TIMESTAMP):INFO: Spark Job Failed for "$SUBJECT_AREA"" >>$LOG_FILE
exit $v_ERROR
fi		