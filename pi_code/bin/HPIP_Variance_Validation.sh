#!/bin/sh
#=================================================================================================================================================
# Title            : Variance_email_check
# ProjectName      : PADP - PI Data (HPIP)
# Filename         : HPIP_Variance_email_check.sh
# Description      : Shell wrapper Script for checking variance and sending email
# Developer        : Anthem
# Created on       : DEC 2017
# Location         : ATLANTA
# Date			Auth			Ver#		Modified By(Name)				Change and Reason for Change
# ----------	----			----		-----------------				-----------------------------
# 2018/01/02	Deloitte		1			Raj Keshri
#====================================================================================================================================================
##  INPUT ARGUMENTS                                                                                     
# 1. EDGE node config file along with path
# 2. Programs for which variance has to be checked
#====================================================================================================================================================                                                                                                                                                             
##  PROGRAM STEPS                                                                                     
# 1. Get all the programs for which variance has to be checked
# 2. Collect from the variance tables the records for which variance has exceeded
# 3. Send an email with #Details                                                                                                                                                               
#=====================================================================================================================================================
ENV=$1
EDGE_CONFIG_PATH=$2
PROGRAM_ARRAY=(${@:3})

EDGE_CONFIG=${EDGE_CONFIG_PATH}application_script_${ENV}.properties

flag=0
counter=0
successarray=()
failurearray=()

source $EDGE_CONFIG

now=$(date +"%Y%m%d%H%M%S")
LOG_FILE=${LOG_FILE_PATH}/VARIANCE_VALIDATION_${now}.log

echo "LOG_FILE : "$LOG_FILE
echo "Variance check email service started at $now" > $LOG_FILE

#====================================================================================================================================================
#Checking if temporary directory exists on edge node. Clean if exists or Creating directory if does not exist.
##########################################
if [ -d $TEMP_PATH ]
then
	echo "Directory exists on edge node" >> $LOG_FILE
	if [ "$(ls -A $TEMP_PATH)" ]
	then
		echo "Cleaning the Directory at $now" >> $LOG_FILE
		rm $TEMP_PATH/*
	fi
else
	echo "Directory does not exist on edge node" >> $LOG_FILE
	echo "Creating directory on edge node at $now" >> $LOG_FILE
	mkdir $TEMP_PATH
fi

#====================================================================================================================================================
#Execution of the HQL starts to check the variance and capture it in temporary file if found any.
##########################################

for i in "${PROGRAM_ARRAY[@]}"
do
	filename="${TEMP_PATH}/query_output_${i}.csv"
	varianceresult="${TEMP_PATH}/variance.csv"
	varianceresult_html="${TEMP_PATH}/variancehtml.html"
	
	eval SUBJECT=$(tr '[a-z]' '[A-Z]' <<< $i)
	eval LIMIT=$SUBJECT"_ROW_LIMIT"
	LIMITVALUE=$(eval echo \$$LIMIT)
		
	beeline -u $BEELINE_SERVER -hiveconf db=$HIVE_SCHEMA -hiveconf subarea=$SUBJECT -hiveconf limit=$LIMITVALUE --outputformat=csv2 -e "set hive.exec.parallel=true; set mapred.job.reuse.jvm.num.tasks=-1; select tablename, operationdescription,operationvalue, percentagevariance, threshold, isthresholdcrossed, subjectarea, last_updt_dtm from "'${hiveconf:db}'".hpip_variance where upper(subjectarea)='"'${hiveconf:subarea}'"' order by last_updt_dtm desc limit "'${hiveconf:limit}'";">${filename}
	
	if [ $? -ne '0' ]; then
		echo "ERROR: Hive query execution failed for Variance check for subjectarea ${SUBJECT} at ${now}" >> $LOG_FILE
		exit 1
	else
		echo "Hive query execution was successful for Variance check for subjectarea ${SUBJECT} at ${now}" >> $LOG_FILE
	fi	
		
	{
	read
	while read line
	do 
		isthresholdcrossed=`echo $line | cut -d, -f6`
					
		if ( "${isthresholdcrossed}" == 'true') 
		then
		   flag=1
		   counter=$((counter+1))
		   tablename=`echo $line | cut -d, -f1`
		   operationdescription=`echo $line | cut -d, -f2`
		   operationvalue=`echo $line | cut -d, -f3`
		   percentagevariance=`echo $line | cut -d, -f4`
		   threshold=`echo $line | cut -d, -f5`
		   isthresholdcrossed=`echo $line | cut -d, -f6`
		   subjectarea=`echo $line | cut -d, -f7`
		   last_updt_dtm=`echo $line | cut -d, -f8`
		   eachrowval="$tablename,$operationdescription,$operationvalue,$percentagevariance,$threshold,$isthresholdcrossed,$subjectarea,$last_updt_dtm"
		   echo -e "$eachrowval"  >>${varianceresult}
		fi
	done
	}<$filename
	
	if [ $? -ne '0' ]; then
		echo "ERROR: Isthresholdcrossed value check for subjectarea ${SUBJECT} failed at ${now}" >> $LOG_FILE
		exit 1
	else
		echo "Isthresholdcrossed value check for subjectarea ${SUBJECT} completed at ${now}" >> $LOG_FILE
	fi
	
	if [ "$counter" == 0 ]
	then
		successarray+=($SUBJECT)
		counter=0
	else
		failurearray+=($SUBJECT)
		counter=0
	fi
	
	echo "Deleting the temp query output of subjectarea ${SUBJECT} at ${now}" >> $LOG_FILE
	rm ${filename}
done

successcount=${#successarray[@]}
successmsg="Variance check for "${successarray[@]}" successful."
failuremsg=""${failurearray[@]}"."

if [ "$flag" == 0 ]
then
	echo "Variance Check completed with No threshold violations at ${now}" >> $LOG_FILE
	echo "${successmsg} - No threshold violations." | mail -s "PI Data - Variance check Report" $RECIPIENT_LIST
	echo "PI Data - Variance check Report mail sent at ${now}" >> $LOG_FILE
else
	echo "Variance Check completed with threshold violations at ${now}" >> $LOG_FILE
	awk -v successcount="$successcount" -v successmsg="$successmsg" -v failuremsg="$failuremsg" 'BEGIN{
	FS=","
	print "Mime-Version: 1.0"
	print "Content-Type: text/html"
	print "Subject: PI Data - Variance check Report"
	if (successcount == 0 )
	{
	print "<HTML>""<H4>Variance check completed - Threshold violation found for subjectarea "
	print failuremsg
	print "</H4></BR>"
	}
	else
	{
	print "<HTML>"
	print "<H4>"
	print successmsg
	print "</H4>"
	print "<H4>Variance check completed - Threshold violation found for subjectarea "
	print failuremsg
	print "</H4></BR>"
	}
	fi
	print "<TABLE border="1"><TH>Tablename</TH><TH>Operationdescription</TH><TH>Operationvalue</TH><TH>Percentagevariance</TH><TH>Threshold</TH><TH>Isthresholdcrossed</TH><TH>Subjectarea</TH><TH>Last_updt_dtm</TH>" 
	}
	{
	printf "<TR>"
	for(i=1;i<=NF;i++)
	printf "<TD>%s</TD>", $i
	print "</TR>"
	 }
	END{
	print "</TABLE></BODY></HTML>"
	 }
	' ${varianceresult} > ${varianceresult_html}
	
	cat ${varianceresult_html} | /usr/lib/sendmail -t $RECIPIENT_LIST
	echo "PI Data - Variance check Report mail sent at ${now}" >> $LOG_FILE
fi

if [ $? -ne '0' ]; then
	echo "ERROR: Mail sending failed at ${now}" >> $LOG_FILE
	exit 1
fi

if [ -f $varianceresult ]
then
	rm ${varianceresult}
fi

if [ $? -ne '0' ]; then
	echo "ERROR: Failed to delete temporary files at ${now}" >> $LOG_FILE
	exit 1
else
	echo "Successfully deleted temporary files at ${now}" >> $LOG_FILE
fi

if [ -f $varianceresult_html ]
then
	rm ${varianceresult_html}
fi

if [ $? -ne '0' ]; then
	echo "ERROR: Failed to delete temporary files at ${now}" >> $LOG_FILE
	exit 1
else
	echo "Successfully deleted temporary html files at ${now}" >> $LOG_FILE
fi

echo "Script ran successfully, completed at ${now}" >> $LOG_FILE
