#====================================================================================================================================================
# Title            : CDL_PARQUET_Wrapper
# ProjectName      : CDL BaseLine Loads
# Filename         : CDL_PARQUET_Wrapper.sh
# Description      : Script moves data from Avro Hive external table into Parquet Hive external table 
# Developer        : Anthem
# Created on       : AUG 2017
# Location         : ATLANTA
# Logic            : 
# Parameters       : Parameter file name
# Return codes     : 
# Date                         Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2017/07/13                      1     Initial Version

#  ***************************************************************************************************************************
######### Below scrtip performs the following
######### 1. Execute the Reconciliation -1 : Compare source record counts to the avro table count. If the count matches , move the data to parquet table.
#########     if the counts do not match, abort the job .
######### 2. Insert data into Parquet table
######### 3. Write LoadstartDate And LoadEndDate in param file

echo "Execution of  parquet  Schema  Creation"
#Source DBSRVR config file
#Taking user input and passing to DBSRVR file
db_conf=$2
echo "DB Conf value"
echo $db_conf
echo $0
BIN_DIR=`dirname $0`
echo $BIN_DIR
echo "Sourcing .env file"
source $BIN_DIR/cdl-bl-db-env.conf $db_conf
#Source Hadoop config file
source $BIN_DIR/cdl-bl-env.conf

#Source the parameter file
echo $1 
source $1

echo  $CDL_CONF
echo  $CDL_PARAM
echo  $CDL_LOGS


#Source bash_profile to get credentials for Teradata BTEQ Script
#source ~/.bash_profile

SummaryLogFileNm=CDL_AVRO_PARQUET_`date +%Y%m%d`_${TBL_NM}_$USER.log
SLogFullFileName=$CDL_LOGS/$SummaryLogFileNm
exec 1> $SLogFullFileName 2>&1


#Creating Parquet Table before inserting data.

 #sh CDL_PARQUET_TABLE_CREATION_WRAPPER.sh

### Variable Declarations
error_count=0
YYYY=`date +%Y`
MM=`date +%m`
RunDate=`date +%Y:%m:%d:%H:%M:%S`


fnLogMsg()
{
     LogTime=`date +%Y%m%d%H%M%S`
     LogMsg="$LogTime:$1:$2"
     echo $LogMsg >> $SLogFullFileName
     echo $LogMsg
}

fnStatusCheck()
{
     CommandRC="$1"
     SuccessMSG="$2"
     FailureMSG="$3"
                
    if [ $CommandRC -eq 0 ]
    then
        fnLogMsg INFO "${SuccessMSG}"
        else
            fnLogMsg INFO "${FailureMSG}"
        exit 1
        error_count =`expr $error_count + 1`
    fi
}



#Source bash_profile to get credentials for Teradata BTEQ Script

#source ./.bash_profile

#echo "Logon server name"

#echo $LOGON

#########Reconciliation -1 : Compare source record counts to the avro table count. If the count matches , move the data to parquet table.
######### if the counts do not match, abort the job .

#Create directory if not exist
dir=$CDL_LOGS/Reconciliation
if [[ ! -e $dir ]]; then
    mkdir $dir
elif [[  -d $dir ]]; then
    echo "$dir already exist"
fi

#Remove Temp Files if exist
AvroReconFile=$CDL_LOGS/Reconciliation/avro_count_recon_${TBL_NM}_$RUN_NB.txt
if [ -f "$AvroReconFile" ]
then
    rm $AvroReconFile

   echo "AvroReconFile Temp File deleted"
fi

#Remove Temp Files if exist
TeradataReconFile=$CDL_LOGS/Reconciliation/Teradata_count_recon_${TBL_NM}_$RUN_NB.txt
if [ -f "$TeradataReconFile" ]
then
    rm $TeradataReconFile

   echo "TeradataReconFile Temp File deleted"
fi


#GET Avro Table Count

#impala-shell -k --ssl -i $IMPALA_CMD -B  -q  "REFRESH ${cdl_schema_name_sg}.${cdl_incoming_table_name};SELECT count(*) from  ${cdl_schema_name_sg}.${cdl_incoming_table_name};" --output_file=$CDL_LOGS/Reconciliation/avro_count_recon_${TBL_NM}_$RUN_NB.txt

beeline -u $BEELINE_CMD  --outputformat=tsv2 --showHeader=false -e "SELECT count(*) from  ${cdl_schema_name_sg}.${cdl_incoming_table_name};" >> $CDL_LOGS/Reconciliation/avro_count_recon_${TBL_NM}_$RUN_NB.txt

if [ $? -gt 0 ]; then
echo "Error in getting count from Avro external table"
exit 1
fi


cat $CDL_LOGS/CDL_BTEQ_RECON_${TBL_NM}_$USER.log|grep 'Checking Count for Reconcilation Process' -A 10|tail -2 >>$CDL_LOGS/Reconciliation/Teradata_count_recon_${TBL_NM}_$RUN_NB.txt



#Getting the counts for avro and teradata
avroRecon_count=`cat $CDL_LOGS/Reconciliation/avro_count_recon_${TBL_NM}_$RUN_NB.txt`
TeradataRecon_count=`cat $CDL_LOGS/Reconciliation/Teradata_count_recon_${TBL_NM}_$RUN_NB.txt`


echo "avro file count =" $avroRecon_count
echo "Teradata source count =" $TeradataRecon_count
######Compare Teradata  count with Avro count. If the counts match , then proceed with next  steps. Else exit the code.

if [ $avroRecon_count -eq $TeradataRecon_count ]
then

echo "Teradata Avro Counts are matching"

else

echo "Terada Avro counts is not matching. Aborting the job."
exit 1

fi

#Export avro table count for audit wrapper
avroRecon_cnt=`echo ${avroRecon_count//[[:blank:]]/}`
echo export avroRecon_count=$avroRecon_cnt >>$1

################################### STEP -2 : Move the data from Avro table into Parquet table
echo "STEP -1 : Move the data from Avro table into Parquet table"

### Execution of script starts here
echo "Values required for Parquet insert"
echo $cdl_incoming_table_name
echo $cdl_table_name
echo $cdl_schema_name_wh
echo $is_partition


#Table name for hive query
        tbl_nm_hive="'$cdl_table_name'"
        echo "hive query table name"
        echo $tbl_nm_hive
		
		############ Get the updated Load Log Key-CDH_LOAD_LOG
		
		

#Deleting Temp File

CDLFile=$CDL_LOGS/cdl_count_$TBL_NM.txt
if [ -f "$CDLFile" ]
then
     rm $CDLFile

   echo "CDLFile Temp File deleted"
fi


# Table name in CDH_LOD_LOG query to get max(CDH_LOAD_LOG) value
teradata_table_name=$TBL_NM
teradata_table_name_for_hive="'$teradata_table_name'"
echo "Hive query table name"
echo $teradata_table_name_for_hive
process_nm_param=$PROCESS_NM
process_nm_hive="'$process_nm_param'"
echo "Process Name"
echo $process_nm_hive
subj_area_param=$SUBJ_AREA_NM
subj_area_hive="'$subj_area_param'"
echo "subj_area_hive Name"
echo $subj_area_hive
#connectimg beeline to get the max $CDH_LOAD_LOG_KEY
echo "Schema name"
echo $cdl_schema_name_sg
echo  beeline -u $BEELINE_CMD --outputformat=tsv2 --showHeader=false -e "set hive.exec.max.dynamic.partitions.pernode=500;set hive.auto.convert.join=false;set hive.exec.dynamic.partition=true; set hive.exec.dynamic.partition.mode=nonstrict; select max(CDH_LOAD_LOG_KEY) FROM $cdl_schema_name_sg.CDH_LOAD_LOG where SUB_PROCESS_NM=$teradata_table_name_for_hive and SUBJ_AREA_NM=$subj_area_hive and PROCESS_NM=$process_nm_hive limit 1;" 



#beeline -u $BEELINE_CMD --outputformat=tsv2 --showHeader=false -e "set hive.exec.max.dynamic.partitions.pernode=500;set hive.auto.convert.join=false;set hive.exec.dynamic.partition=true; set hive.exec.dynamic.partition.mode=nonstrict; select max(CDH_LOAD_LOG_KEY) FROM $cdl_schema_name_sg.CDH_LOAD_LOG where SUB_PROCESS_NM=$teradata_table_name_for_hive and SUBJ_AREA_NM=$subj_area_hive and PROCESS_NM=$process_nm_hive limit 1;" > $CDL_LOGS/cdl_count_$TBL_NM.txt


#Check load_log_key-If NULL then set to 1 otherwise load_log_key+1
#load_log_key=`cat $CDL_LOGS/cdl_count_$TBL_NM.txt`
#echo "beeline result"
#echo $load_log_key
#First entry of table in Audit table
#if [ "$load_log_key" == "NULL" ]; then
#CDH_LOAD_LOG_KEY=1
#echo $CDH_LOAD_LOG_KEY
#  echo "load_log_key is null -initializing to 1"
#else
#CDH_LOAD_LOG_KEY=`expr $load_log_key + 1`
CDH_LOAD_LOG_KEY=`java -jar $CDL_BIN/Unique_number/UniqueNumber.jar`
#fi

#Checking Table Partition 
#LoadStartDate=`date +%Y-%m-%d %H:%M:%S` 
#Exporting For Audit table
LoadStartDate=`date +%Y:%m:%d:%H:%M:%S`
echo "LoadStartDate******************"
echo $LoadStartDate

echo "Writing variables in file"
echo $1
#echo export CDH_LOAD_LOG_KEY=$CDH_LOAD_LOG_KEY >>$1
echo export CDH_LOAD_LOG_KEY=$CDH_LOAD_LOG_KEY >>$1
echo export LoadStartDate=$LoadStartDate >>$1


		

set hive.execution.engine=mapreduce

echo CDH_LOAD_LOG_KEY : $CDH_LOAD_LOG_KEY
echo RUN_NB: $RUN_NB

#Check the RUN_NB is param file and if RUN_NB=1 then delete the old data parquet table data
if [ $RUN_NB -eq 1 ]
then
     hadoop fs -rmr -skipTrash hdfs://nameservice1$PARQUET_LOCATION

   echo "Parquet old files deleted"
   #Initialize Table Count To 0
   PQT_ROWS_LOADED_NB=0
   echo "Count of Parquet Table "
   echo $PQT_ROWS_LOADED_NB
fi

#Create directory if not exist
audit_dir=$CDL_TEMP_AUDIT/Audit
if [[ ! -e $audit_dir ]]; then
    mkdir $audit_dir
elif [[  -d $audit_dir ]]; then
    echo "$audit_dir already exist"
fi

ParquetFile=$audit_dir/parquet_count_${TBL_NM}_$USER.txt

if [ -f "$ParquetFile" ]
then
    rm $ParquetFile

   echo "Parquet Temp File deleted"
fi

#Get the parquet table name
parquet_table_name=$cdl_table_name

#Check parquet count before inserting data to avoid duplication 
if [ $RUN_NB != 1 ]

then

echo "RUN_NB is not 1.Check Parquet Table Count"
#Get the parquet table count
#echo beeline -u $BEELINE_CMD  --outputformat=tsv2 --showHeader=false -e "SELECT count(*) from  ${cdl_schema_name_wh}.${parquet_table_name} where ${PARQUET_RECON_CON};" >> $audit_dir/parquet_count_$TBL_NM_$USER.txt

beeline -u $BEELINE_CMD  --outputformat=tsv2 --showHeader=false -e "SELECT count(*) from  ${cdl_schema_name_wh}.${parquet_table_name} where ${PARQUET_RECON_CON};" >> $audit_dir/parquet_count_${TBL_NM}_$USER.txt

#impala-shell -k --ssl -i $IMPALA_CMD -B  -q  "invalidate metadata ${cdl_schema_name_wh}.${parquet_table_name} ;SELECT count(*) from  ${cdl_schema_name_wh}.${parquet_table_name} where ${PARQUET_RECON_CON};" --output_file=$audit_dir/parquet_count_$TBL_NM_$USER.txt

#Check Script successfully executed or not
if [ $? -eq 0 ]; then
    echo "Succefully get count from  Parquet external table "
else

echo "Beeline command to get count from  Parquet external table failed"
exit 1
fi 



#Getting the counts for avro and parquet
echo "Paquet Count from Param file"
parquet_count=`cat $audit_dir/parquet_count_${TBL_NM}_$USER.txt`

echo $parquet_count

#Parquet table count
PQT_ROWS_LOADED_NB=`echo ${parquet_count//[[:blank:]]/}`

echo $PQT_ROWS_LOADED_NB

#End of RUN_NB if condition
fi


#Compare the counts before deleting the temp files and tables
if [ "$PQT_ROWS_LOADED_NB" == "0" ] 
then

echo Avro To ParquetInsertion Start
echo spark-submit --class org.coca.CDL_DDL_Creation --master yarn --deploy-mode cluster --name $TBL_NM --conf "spark.yarn.maxAppAttempts=1" $CDL_BIN/AvroToParquetInsertion-0.0.1-SNAPSHOT.jar $PARQUET_TEMP_LOCATION/final_parquet_data_$TBL_NM.txt $cdl_schema_name_wh  $cdl_schema_name_sg $cdl_table_name $cdl_incoming_table_name $PARQUET_LOCATION $CDH_LOAD_LOG_KEY $PARTITION_COLUMN



echo insert to parquet from avro started
spark-submit --class org.coca.CDL_DDL_Creation --master yarn --deploy-mode cluster --name $TBL_NM --conf "spark.yarn.maxAppAttempts=1" --num-executors 40 --executor-memory 30G --executor-cores 4 --driver-memory 8G $CDL_BIN/AvroToParquetInsertion-0.0.1-SNAPSHOT.jar $PARQUET_TEMP_LOCATION/final_parquet_data_$TBL_NM.txt $cdl_schema_name_wh  $cdl_schema_name_sg $cdl_table_name $cdl_incoming_table_name $PARQUET_LOCATION $CDH_LOAD_LOG_KEY $PARTITION_COLUMN


#Get spark application URL
 application_url=`grep tracking  $CDL_LOGS/$SummaryLogFileNm|head -1`

 #Extract application_id from URL
application_id=$(echo $application_url | sed 's:/*$::')

#Get application name
application_name=`echo $application_id|rev | cut -d'/' -f 1 | rev`

#Remove temp files
rm $CDL_LOGS/temp_app_details_${TBL_NM}_$USER.txt

#Get application status details and save in temp file
yarn application --status $application_name >$CDL_LOGS/temp_app_details_${TBL_NM}_$USER.txt

#Get the application final status
app_status=`grep Final-State $CDL_LOGS/temp_app_details_${TBL_NM}_$USER.txt`
final_app_status=`echo $app_status|rev | cut -d':' -f 1 | rev|tail -1`

#echo $final_app_status
status="SUCCEEDED"

echo $final_app_status
echo $status
#Compare application status
if [ $final_app_status  ==  $status ]
then 
echo "Parquet data inserted successfully using spark code"
else
echo "Spark Job Failed.Please check the log"
exit 1
fi


#Checking script execution success/failure
insrtRC=$?
fnStatusCheck $insrtRC "echo insert to parquet from avro completed .... $cdl_schema_name_wh is Loaded"  "Error Loading  loading $cdl_table_name"
	
fnLogMsg INFO "CDL load process is completed for table : $cdl_table_names"

	

#Load  start end Timestamp
rm $CDL_LOGS/ParquetRun.txt
LoadEndDate=`date +%Y:%m:%d:%H:%M:%S`
echo $LoadEndDate >>$CDL_LOGS/ParquetRun.txt
echo "Avro Parquet Insert Job Completed"
echo $LoadEndDate


echo "Writing into param file for Audit log"
echo $1
echo export CDH_LOAD_LOG_KEY=$CDH_LOAD_LOG_KEY >>$1
echo export LoadStartDate=$LoadStartDate >>$1
echo export LoadEndDate=$LoadEndDate >>$1

else
	
	echo "ParquetTable:Data Already inserted.Please Check Parquet Table Data"
	
fi



