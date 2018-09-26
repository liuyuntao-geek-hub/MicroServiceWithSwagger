#!/bin/sh
source ~/.bash_profile

# Title            : PROVIDER_VBP_WRAPPER
# ProjectName      : 849 SMARTPCP select 
# Filename         : PROVIDER_VBP_Wrapper.sh
# Description      : Shell Script for spark member ETL
# Developer        : Anthem
# Created on       : JUNE 2017
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
#subject_area='spcp-etl-vbp'
#Environment='dev'
#Edge node path=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/control
#Config path=hdfs:/dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/control/

#=====================================================================================================================================================

now=$(date +"%Y%m%d%H%M%S")

ENV=$1
EDGE_PATH=$2
CONFIG_NAME="application_script_"$1".properties"
SUBJECT_AREA="spcp-etl-vbp"

QUERY_PROPERTY=`echo $SUBJECT_AREA | cut -d "-" -f 1`
#Fetch properties from Config file -- redirect that to property file in edge node
#source that property files and get the needed params as below
echo $ENV
echo $EDGE_PATH
echo $CONFIG_NAME
echo $QUERY_PROPERTY
echo $SUBJECT_AREA


source $EDGE_PATH/$CONFIG_NAME
echo $EDGE_PATH/$CONFIG_NAME
echo $LOG_FILE_PATH
#Creating log file
log_loc=$LOG_FILE_PATH

echo "log loc== $log_loc"
SPCP_LOG_FILE=${log_loc}"/script_"$SUBJECT_AREA"_"$USER.log

echo "vbp wrapper triggered at $now" >>$SPCP_LOG_FILE
if [ $# -eq 2 ]
    then
        echo "Argument check completed"  >>$SPCP_LOG_FILE
    else
                echo "Error in number of arguments passed, Script needs 2 arguments for execution"  >>$SPCP_LOG_FILE
                exit 1
fi


SummaryLogFileNm=$SUBJECT_AREA"_"$now"_"$USER.log
SLogFullFileName=$LOG_FILE_PATH/$SummaryLogFileNm
exec 1> $SLogFullFileName 2>&1

#====================================================================================================================================================
# Run spark submit command

export JAVA_HOME=/usr/java/latest

spark2-submit --class  com.anthem.hca.spcp.vbp.VbpDriver --master yarn --deploy-mode cluster \
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
--files /etc/spark2/conf.cloudera.spark2_on_yarn/yarn-conf/hive-site.xml,$CONFIG_PATH/log4j.xml,/etc/krb5.conf \
--jars /opt/cloudera/parcels/CDH-5.12.2-1.cdh5.12.2.p0.4/jars/hive-contrib-1.1.0-cdh5.12.2.jar,/opt/cloudera/parcels/SPLICEMACHINE/lib/db-client-2.5.0.1826b.jar,/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/bin/splicemachine-cdh5.12.2-2.2.0.cloudera2_2.11-2.5.0.1826b.jar \
--name $SUBJECT_AREA  $JAR_FILE_PATH/$JAR_NAME $CONFIG_PATH $ENV $QUERY_PROPERTY
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



-- INSERT --                 
