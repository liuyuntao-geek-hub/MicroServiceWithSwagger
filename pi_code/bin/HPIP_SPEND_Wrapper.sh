#!/bin/bash
#=================================================================================================================================================
# Title            : HPIP_SPEND_WRAPPER
# ProjectName      : PADP - PI Data (HPIP)
# Filename         : hpip_copy_logs.sh
# Description      : Shell Script for fetching the application name and id
# Developer        : Anthem
# Created on       : DEC 2017
# Location         : ATLANTA
# Date           Auth           Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2017/12/20     Deloitte         1     Initial Version
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
# 1. Fetch the values from hive audit table for the application id and application name
# 2. Get the application id and appplication name from log file.
# 3. Copy the yarn application logs to a new log file created with filename having the application name and application id.                                                                                                                                                               
#==================================================================================================================================================== 
## SAMPLE ARGUMENTS
#subject_area='HPIP_SPEND'
#=====================================================================================================================================================

now=$(date +"%Y%m%d%H%M%S")
echo $now

echo "SPEND wrapper triggered at $now" 

ENV=$1
EDGE_PATH=$2
CONFIG_NAME="application_script_"$1".properties"
SUBJECT_AREA="HPIP_SPEND"

#Fetch properties from Config file -- redirect that to property file in edge node
#source that property files and get the needed params as below

source $EDGE_PATH/$CONFIG_NAME

#Creating log file 
HPIP_LOG_FILE=$LOG_FILE_PATH/"script_"$SUBJECT_AREA"_"$now"_"$USER.log

if [ $# -eq 2 ]
    then
        echo "Argument check completed"  >>$HPIP_LOG_FILE
    else
		echo "Error in number of arguments passed, Script needs 2 arguments for execution"  >>$HPIP_LOG_FILE
		exit 1
fi

SummaryLogFileNm=$SUBJECT_AREA"_"$now"_"$USER.log
SLogFullFileName=$LOG_FILE_PATH/$SummaryLogFileNm
exec 1> $SLogFullFileName 2>&1

#====================================================================================================================================================
# Run spark submit command

spark-submit --master yarn --deploy-mode cluster --name $SUBJECT_AREA --num-executors 20 --executor-memory 20G --executor-cores 4 --driver-memory 8G --files $CONFIG_PATH/log4j.xml --driver-java-options "-Dlog4j.configuration=log4j.xml" --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" --conf spark.network.timeout=600 --conf spark.driver.maxResultSize=2g --class com.anthem.hpip.spend.SpendDriver $JAR_FILE_PATH/$JAR_NAME $CONFIG_PATH $ENV $PROP_FILE

#======================================================================================================================================================
#=====================================================================================================================================================
#Get spark application URL
 application_url=`grep tracking  $LOG_FILE_PATH/$SummaryLogFileNm|head -1`

 #Extract application_id from URL
application_id=$(echo $application_url | sed 's:/*$::')

#Get application name
application_name=`echo $application_id|rev | cut -d'/' -f 1 | rev`

#Remove temp files
rm $LOG_FILE_PATH/temp_app_details_$USER.txt

#path to log file
yarn logs -applicationId ${application_name} >$LOG_FILE_PATH/$SUBJECT_AREA"_yarn_log_"${application_name}.log
echo "Yarn Log file : "$LOG_FILE_PATH"/"$SUBJECT_AREA"_yarn_log_"${application_name}".log"  >>$HPIP_LOG_FILE

#Get application status details and save in temp file
yarn application --status $application_name >$LOG_FILE_PATH/temp_app_details_$USER.txt

#Get the application final status
app_status=`grep Final-State $LOG_FILE_PATH/temp_app_details_$USER.txt`
final_app_status=`echo $app_status|rev | cut -d':' -f 1 | rev|tail -1`

#echo $final_app_status
status="SUCCEEDED"

echo $final_app_status >>$HPIP_LOG_FILE
echo $status >>$HPIP_LOG_FILE
#Compare application status
if [ $final_app_status  ==  $status ]
then 
echo "Spark Job for "$SUBJECT_AREA" executed successfully" >>$HPIP_LOG_FILE
else
echo "Spark Job Failed.Please check the log" >>$HPIP_LOG_FILE
exit 1
fi



#====================================================================================================================================================
##  End of Script      
#====================================================================================================================================================
