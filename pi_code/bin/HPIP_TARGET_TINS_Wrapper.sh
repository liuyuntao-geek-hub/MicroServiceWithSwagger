#=================================================================================================================================================
#!/bin/sh
# Title            : Targeted_Tins_Wrapper
# ProjectName      : PADP - PI Data (HPIP)
# Filename         : HPIP_TARGET_TINS_Wrapper.sh
# Description      : Shell wrapper Script for Targeted TINS file movement and archival
# Developer        : Anthem
# Created on       : DEC 2017
# Location         : ATLANTA
# Date           Auth           Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2017/12/18     Deloitte         1     Initial Version
#====================================================================================================================================================
##  INPUT ARGUMENTS                                                                                     
# 1. Config file hdfs path                                                                                                                                                           
#2.Environment                                                                                                                                                  # 3.Edge Node app path 
# 4.Edge node property file name
#====================================================================================================================================================                                                                                                                                                             
##  PROGRAM STEPS                                                                                     
# 1. Moves files from edge node to HDFS                                                                                                                                          
# 2. Run the Targeted TINS program                                                                                                                                               
# 3. Archive files                                                                                                                                                               
#==================================================================================================================================================== 
## SAMPLE ARGUMENTS
#Config file path=hdfs:///ts/hdfsapp/ve2/pdp/hpip/phi/no_gbd/r000/control/
#Environment=sit
#Edge node app path = /ts/app/ve2/pdp/hpip/phi/no_gbd/r000/control
#Query Property file=hpip__targeted_tins_wrapper.properties
#Variables for argument parameters passed

now=$(date +"%Y%m%d%H%M%S")
echo $now

echo "Targeted Tins wrapper triggered at $now" 

ENV=$1
EDGE_PATH=$2
CONFIG_NAME="application_script_"$1".properties"
SUBJECT_AREA="HPIP_TARGET_TINS"

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
#Checking if directories exists in hadoop and edge node (Creating if does not exist)
##########################################

if [ -d $EDGE_NODE_PATH ]
then
	echo "Directory exists in edge node"  >>$HPIP_LOG_FILE
else
	echo "Directory does not exist in edge node" >>$HPIP_LOG_FILE
	echo "Creating directory in edge node" >>$HPIP_LOG_FILE
	mkdir $EDGE_NODE_PATH
fi
echo "CHECKING IN HDFS" >>$HPIP_LOG_FILE
if $(hadoop fs -test -d $HDFS_PATH)
then
	echo "Directory exists in hadoop" >>$HPIP_LOG_FILE
	hadoop fs -rm -r -skipTrash $HDFS_PATH
			if [ $? -ne '0' ]; then
				echo "Error in cleaning up files in the hdfs path" >>$HPIP_LOG_FILE
			else
				echo "Files cleaned up from the hdfs path" >>$HPIP_LOG_FILE
			fi
	hadoop fs -mkdir $HDFS_PATH
else
	echo "Directory does not exist in hadoop" >>$HPIP_LOG_FILE
	echo "Creating directory in hadoop" >>$HPIP_LOG_FILE
	hadoop fs -mkdir $HDFS_PATH
fi

#====================================================================================================================================================
# Move file from Edge node to HDFS
#hadoop fs -copyFromLocal $EDGE_NODE_PATH/* $HDFS_PATH/
###########################################
count="$(ls $EDGE_NODE_PATH|wc -l)"
if [ $count -ne 0 ]
then
	ls $EDGE_NODE_PATH > $EDGE_PATH/$FILELIST_TEXT
	cat $EDGE_PATH/$FILELIST_TEXT |while read filename
	do
		filename_ws="${filename// /%20}"
		hadoop fs -copyFromLocal $EDGE_NODE_PATH"/"$filename_ws $HDFS_PATH/
	done
	if [ $? -ne '0' ]; then
		echo "Error copying files from Edge node to HDFS" >>$HPIP_LOG_FILE
		exit 1
	else
		echo "Files copied successfully to HDFS" >>$HPIP_LOG_FILE
	fi
else
	echo "No files found in edge node " >>$HPIP_LOG_FILE
	exit 1
fi
#====================================================================================================================================================
# Run spark submit command

spark-submit --master yarn --deploy-mode cluster --name $SUBJECT_AREA --num-executors 20 --executor-memory 20G --executor-cores 4 --driver-memory 8G --files $CONFIG_PATH/log4j.xml --driver-java-options "-Dlog4j.configuration=log4j.xml" --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.xml" --conf spark.network.timeout=600 --conf spark.driver.maxResultSize=2g --class com.anthem.hpip.targettin.TargetTinDriver $JAR_FILE_PATH/$JAR_NAME $CONFIG_PATH $ENV $PROP_FILE

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
echo "Yarn Log file : "$LOG_FILE_PATH"/"$SUBJECT_AREA"_yarn_log_"${application_name}".log" >>$HPIP_LOG_FILE

#Get application status details and save in temp file
yarn application --status $application_name >$LOG_FILE_PATH/temp_app_details_$USER.txt

#Get the application final status
app_status=`grep Final-State $LOG_FILE_PATH/temp_app_details_$USER.txt`
final_app_status=`echo $app_status|rev | cut -d':' -f 1 | rev|tail -1`

#echo $final_app_status
status="SUCCEEDED"

echo $final_app_status
echo $status
#Compare application status
if [ $final_app_status  ==  $status ]
then 
echo "Spark Job for "$SUBJECT_AREA" executed successfully" >>$HPIP_LOG_FILE
else
echo "Spark Job Failed.Please check the log" >>$HPIP_LOG_FILE
exit 1
fi


#====================================================================================================================================================
# Archival and cleanup
echo "Archiving the files in edge node" >>$HPIP_LOG_FILE

mkdir $ARCHIVAL_PATH/"tinfiles_"$MONTHYEAR
cp $EDGE_NODE_PATH/* $ARCHIVAL_PATH/"tinfiles_"$MONTHYEAR
if [ $? -ne '0' ]; then
	echo "Error in copying files in the archival path" >>$HPIP_LOG_FILE
	exit 1
else
	echo "Files copied in archive folder" >>$HPIP_LOG_FILE
fi

echo "Cleaning up the files in HDFS" >>$HPIP_LOG_FILE

hadoop fs -rm -skipTrash $HDFS_PATH/*
if [ $? -ne '0' ]; then
	echo "Error in cleaning up files in the hdfs path" >>$HPIP_LOG_FILE
	exit 1
else
	echo "Files cleaned up from the hdfs path" >>$HPIP_LOG_FILE
fi

echo "Cleaning up the files in edge node path" >>$HPIP_LOG_FILE
rm $EDGE_NODE_PATH/*
if [ $? -ne '0' ]; then
	echo "Error in cleaning up files in the edge node path" >>$HPIP_LOG_FILE
	exit 1
else
	echo "Files cleaned up from the edge node path; Targeted TINS execution successful" >>$HPIP_LOG_FILE
fi

echo "Cleaning up the temperory files created" >>$HPIP_LOG_FILE

rm $EDGE_PATH/$FILELIST_TEXT
if [ $? -ne '0' ]; then
	echo "Error in cleaning up files in the edge node path"  >>$HPIP_LOG_FILE
	exit 1
else
	echo "Files cleaned up from the edge node path; Targeted TINS execution successful" >>$HPIP_LOG_FILE
fi

#====================================================================================================================================================
##  End of Script      
#====================================================================================================================================================