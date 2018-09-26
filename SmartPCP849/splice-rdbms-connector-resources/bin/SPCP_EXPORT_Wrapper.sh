#!/bin/sh
#=================================================================================================================================================
# Title            : SPCP_EXPORT_WRAPPER
# ProjectName      : 849 SMARTPCP select 
# Filename         : SPCP_EXPORT_Wrapper.sh
# Description      : Shell Script for spark export 
# Developer        : Anthem
# Created on       : JULY 2017
# Location         : INDIA
# Date           Auth           Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2018/06/30    Deloitte         1     Initial Version
#====================================================================================================================================================
##  INPUT ARGUMENTS                                                                                     
# 1. Subject Area of the specific application as required
# 2. Environment (specifically gbd or no_gbd for SIT)
# 3. Edge node path
# 4. Config path 
#
#====================================================================================================================================================                                                                                                                                                             
##  PROGRAM STEPS                                                                     
# 1. Get the application properties to the scope of the program.
# 2. Submit the spark job.                
# 3. Get the application id and application name from log file.
# 4. Copy the yarn application logs to a new log file created with filename having the application name and application id.                                                                                                                                                               
#==================================================================================================================================================== 
## SAMPLE ARGUMENTS
#subject_area='spcp-etl-member'
#Environment='dev'
#Edge node path=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/control
#table_nm/view_nm=MBRINF
#Config path=hdfs:///dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/

#=====================================================================================================================================================

now=$(date +"%Y%m%d%H%M%S")

ENV=$1
EDGE_PATH=$2
TABLE_NM=$3
CONFIG_NAME="src_script_application_"$1".properties"
SUBJECT_AREA="spcp-etl-export_"$3""
#Fetch properties from Config file -- redirect that to property file in edge node
#source that property files and get the needed params as below
echo $ENV
echo $EDGE_PATH
echo $CONFIG_NAME
echo $SUBJECT_AREA
echo $QUEUE_NAME

source $EDGE_PATH/$CONFIG_NAME
echo $EDGE_PATH/$CONFIG_NAME	
echo $LOG_FILE_PATH
#Creating log file 
log_loc=$LOG_FILE_PATH
echo "log loc== $log_loc"
SPCP_LOG_FILE=${log_loc}"/script_"$SUBJECT_AREA"_"$USER.log

echo "SPCP EXPORT wrapper triggered at $now" >>$SPCP_LOG_FILE
if [ $# -eq 3 ]
    then
        echo "Argument check completed"  >>$SPCP_LOG_FILE
    else
		echo "Error in number of arguments passed, Script needs 3 arguments for execution"  >>$SPCP_LOG_FILE
		exit 1
fi


SummaryLogFileNm=$SUBJECT_AREA"_"$now"_"$USER.log
SLogFullFileName=$LOG_FILE_PATH/$SummaryLogFileNm
exec 1> $SLogFullFileName 2>&1

#====================================================================================================================================================
# Run spark submit command
export JAVA_HOME=/usr/java/latest
spark2-submit --class com.anthem.hca.splice.export.fullrefresh.SpliceToRDBMSExportDriver --master yarn --deploy-mode cluster \
--queue $QUEUE_NAME \
--packages com.typesafe:config:1.2.1  \
--conf spark.yarn.maxAppAttempts=1 \
--driver-memory 6G  --driver-cores 4 --conf spark.yarn.driver.memoryOverhead=2048 --conf spark.driver.maxResultSize=3G \
--conf spark.dynamicAllocation.enabled=true --conf spark.shuffle.service.enabled=true --conf spark.dynamicAllocation.maxExecutors=100 \
--executor-memory 6G --executor-cores 4  --conf spark.yarn.executor.memoryOverhead=2048 \
--conf spark.network.timeout=600 --conf spark.port.maxRetries=40 \
--conf 'spark.driver.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dspark.yarn.principal=${USER_NAME}@DEVAD.WELLPOINT.COM -Dspark.yarn.keytab=$KEYTAB_PATH/${USER_NAME}.keytab -Dlog4j.configuration=log4j.xml  -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12' \
--conf 'spark.executor.extraJavaOptions=-Djava.security.krb5.conf=/etc/krb5.conf -Dlog4j.configuration=log4j.xml -XX:+UseCompressedOops -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=12'  \
--conf 'spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--conf 'spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*' \
--keytab $KEYTAB_PATH/${USER_NAME}.keytab --principal ${USER_NAME}@DEVAD.WELLPOINT.COM \
--files $CONFIG_PATH/log4j.xml,/etc/krb5.conf \
--jars /opt/cloudera/parcels/SPLICEMACHINE/lib/$SPLICE_DB_CLIENT_JAR,$JAR_FILE_PATH/$SPLICE_CLOUDERA_JAR,/var/lib/sqoop/jtds-patched-1.3.1.jar \
--name $SUBJECT_AREA  $JAR_FILE_PATH/$JAR_NAME $CONFIG_PATH $ENV $TABLE_NM

#=====================================================================================================================================================
#Get spark application URL
application_url=`grep tracking  $SLogFullFileName|head -1` 
echo "Application URL is $application_url" >>$SPCP_LOG_FILE

#Extract application_id from URL
application_id=$(echo $application_url | sed 's:/*$::') 

#Get application name
application_name=`echo $application_id| rev | cut -d'/' -f 1 | rev` 
echo "Application Name is : $application_name" >>$SPCP_LOG_FILE

#Get application status details and save in temp file
yarn application --status $application_name >$LOG_FILE_PATH/temp_app_details_$SUBJECT_AREA"_"$now"_"$USER.txt 

#Get the application final status
app_status=`grep Final-State $LOG_FILE_PATH/temp_app_details_$SUBJECT_AREA"_"$now"_"$USER.txt`
final_app_status=`echo $app_status|rev | cut -d':' -f 1 | rev|tail -1`

status="SUCCEEDED"

echo $final_app_status  >>$SPCP_LOG_FILE
#Compare application status
if [ $final_app_status  ==  $status ]
then 
echo "Spark Job for "$SUBJECT_AREA" executed successfully" >>$SPCP_LOG_FILE
else
echo "Spark Job Failed.Please check the log" >>$SPCP_LOG_FILE
exit 1
fi
#path to log file
yarn logs -applicationId ${application_name} >$LOG_FILE_PATH/$SUBJECT_AREA"_yarn_log_"${application_name}.log

echo "Yarn Log file : "$LOG_FILE_PATH"/"$SUBJECT_AREA"_yarn_log_"${application_name}".log"  >>$SPCP_LOG_FILE
#Remove temp files
rm -f $LOG_FILE_PATH/temp_app_details_$SUBJECT_AREA"_"$now"_"$USER.txt

#====================================================================================================================================================
##  End of Script      
#====================================================================================================================================================
