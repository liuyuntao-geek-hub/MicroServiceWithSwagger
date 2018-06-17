#!/bin/bash
#====================================================================================================================================================
# Title            : CDL_AUDIT_WRAPPER
# ProjectName      : CDL BaseLine Loads
# Filename         : CDL_AUDIT_WRAPPER.sh
# Description      : Script to create entry in CDH_LOAD_LOG , CDH_AUDIT_LOG
# Developer        : Anthem
# Created on       : AUG 2017
# Location         : ATLANTA
# Logic            : 
# Parameters       : Parameter file name
#Execution          :sh CDL_AUDIT_Wrapper.sh "parameter file name"
# Return codes     : 
# Date                         Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2017/06/13                      1     Initial Version

#  ***************************************************************************************************************************bash

######### Below script performs the following
######### 1. Compare Avro table count with Parquet table count ( W - w/o partitions)
######### 2. If the counts do not match, then exit the proces.
######### 3. If the counts match, then insert the data into CDH_LOAD_LOG , CDH_AUDIT_LOG and Update TERADATA..CDL_LOAD_LOG
######### 4. Delete the AVRO file and Delete the Teradata Stage table ( if exists )

################################### STEP -1 : Capture the record counts between Avro table and parquet table in a Control Table
echo "STEP -1 : Capture the record counts between Avro table and parquet table in a Control Table"

echo $0
BIN_DIR=`dirname $0`
echo $BIN_DIR
source $BIN_DIR/cdl-bl-db-env.conf $2
#Source Hadoop Conf file
source $BIN_DIR/cdl-bl-env.conf

#Source the parameter file
echo $1 
source $1

echo  $CDL_CONF
echo  $CDL_PARAM
echo  $CDL_LOGS

# Moving Source env variables to Hadoop_param.conf

#Job Unix Log File Location

SummaryLogFileNm=CDL_AUDIT_`date +%Y%m%d%H%M%S`_${TBL_NM}_$USER.log
SLogFullFileName=$CDL_LOGS/${SummaryLogFileNm}
exec 1> $SLogFullFileName 2>&1

echo "Parquet Table Load Time"
echo $LoadEndDate
echo $LoadStartDate

#Initiatig variables for CDH_AUDIT_LOG table
delimiter='|'
SUBJ_AREA_NM=$SUBJ_AREA_NM
TBL_NM=$TBL_NM
EXTRACT_COLUMN_NM=$EXTRACT_COLUMN_NM
EXTRACT_START_DT=$JobRunDate
EXTRACT_END_DT=$JobEndTime
AVRO_LOAD_STRT_DTM=$JobRunDate
AVRO_LOAD_END_DTM=$JobEndTime
PQT_LOAD_STRT_DTM=$LoadStartDate
PQT_LOAD_END_DTM=`cat $CDL_LOGS/ParquetRun.txt`
cdl_schema_name=$cdl_schema_name_wh
avro_table_name=$cdl_incoming_table_name
parquet_table_name=$cdl_table_name


#Echo All The Variables
echo $SUBJ_AREA_NM
echo $TBL_NM
echo $EXTRACT_COLUMN_NM
echo $EXTRACT_START_DT
echo $EXTRACT_END_DT
echo $AVRO_LOAD_STRT_DTM
echo $AVRO_LOAD_END_DTM
echo $PQT_LOAD_STRT_DTM
echo $PQT_LOAD_END_DTM
echo $cdl_schema_name_wh




#Create directory if not exist
audit_dir=$CDL_TEMP_AUDIT/Audit
if [[ ! -e $audit_dir ]]; then
    mkdir $audit_dir
elif [[  -d $audit_dir ]]; then
    echo "$tdch_avro_schem already exist"
fi

#Remove temp file before loading data

AvroFile=$audit_dir/avro_count_$TBL_NM.txt
if [ -f "$AvroFile" ]
then
    rm $AvroFile

   echo "Avro Temp File deleted"
fi

ParquetFile=$audit_dir/parquet_count_$TBL_NM_$USER.txt

if [ -f "$ParquetFile" ]
then
    rm $ParquetFile

   echo "Parquet Temp File deleted"
fi


echo "count for tables ${cdl_schema_name_sg}.${avro_table_name} and ${cdl_schema_name_wh}.${parquet_table_name};"

echo "Where condition"
echo ${PARQUET_RECON_CON}



#Get the parquet table count

beeline -u $BEELINE_CMD  --outputformat=tsv2 --showHeader=false -e "SELECT count(*) from  ${cdl_schema_name_wh}.${parquet_table_name} where ${PARQUET_RECON_CON};" >> $audit_dir/parquet_count_$TBL_NM_$USER.txt

#impala-shell -k --ssl -i $IMPALA_CMD -B  -q  "invalidate metadata ${cdl_schema_name_wh}.${parquet_table_name} ;SELECT count(*) from  ${cdl_schema_name_wh}.${parquet_table_name} where ${PARQUET_RECON_CON};" --output_file=$audit_dir/parquet_count_$TBL_NM_$USER.txt

#Check Script successfully executed or not
if [ $? -eq 0 ]; then
    echo "Succefully get count from  Parquet external table "
else

echo "Beeline command to get count from  Parquet external table failed"
exit 1
fi 



#Getting the counts for avro and parquet
echo "Avro Count from Param file"
echo $avroRecon_count
avro_count=$avroRecon_count
parquet_count=`cat $audit_dir/parquet_count_$TBL_NM_$USER.txt`




#Initiatig variables and remove blank spaces from row count
AVRO_ROWS_LOADED_NB=`echo ${avro_count//[[:blank:]]/}`
PQT_ROWS_LOADED_NB=`echo ${parquet_count//[[:blank:]]/}`

echo  "Avro Parquet Tbale Count"
echo $AVRO_ROWS_LOADED_NB
echo $PQT_ROWS_LOADED_NB



#Compare the counts before deleting the temp files and tables
#Compare the counts before deleting the temp files and tables
if [ "$AVRO_ROWS_LOADED_NB" == "$PQT_ROWS_LOADED_NB" ] 

then
echo "Move avro files to inbound location"
echo "Update the record in Teradata CDL_LOAD_LOG and make an entry into Hadoop CDH_LOAD_LOG"

echo RUN_NB: $RUN_NB
#Check the RUN_NB is param file and if RUN_NB=1 then delete the old data avro table data
if [ $RUN_NB -eq 1 ]
then
     hadoop fs -rmr -skipTrash hdfs://nameservice1$CDL_HDFS_INBOUND
	 
   echo "Avro old files deleted"
fi


#Moving avro files to Stage
echo "Moving avro files to Inbound"

echo hadoop fs -mv hdfs://nameservice1$HADOOP_PATH hdfs://nameservice1$CDL_HDFS_INBOUND
hadoop fs -mv hdfs://nameservice1$HADOOP_PATH hdfs://nameservice1$CDL_HDFS_INBOUND

if [ $? -gt 0 ]; then
echo "Moved Avro files to Inbound"
exit 1
fi


delimiter='|'
#CDH_LOAD_LOG_KEY=`expr $load_log_key + 1`
CDH_LOAD_LOG_KEY=$CDH_LOAD_LOG_KEY
SUBJ_AREA_NM=$SUBJ_AREA_NM
PROCESS_NM=$PROCESS_NM
SUB_PROCESS_NM=$TBL_NM
RUN_NB=$RUN_NB
LOAD_STRT_DTM=$JobRunDate
LOAD_END_DTM=$JobEndTime
PUB_IND='Y'


#Inserting into CDH_LOAD_LOG audit table
echo $CDH_LOAD_LOG_KEY$delimiter$SUBJ_AREA_NM$delimiter$PROCESS_NM$delimiter$SUB_PROCESS_NM$delimiter$RUN_NB$delimiter$LOAD_STRT_DTM$delimiter$LOAD_END_DTM$delimiter$PUB_IND  >>$audit_dir/audit_${TBL_NM}.txt

#Inserting into Teradata Audit Log table 

sh $CDL_BIN/BTEQ/BTEQ_CDL_LOAD_LOG_UPDATE.sh $CDL_TBL_NM $CDH_LOAD_LOG_KEY $DB

if [ $? -ne 0 ];then

echo " UPDATE of CDL_LOAD_LOG on TERADTA FAILS FOR ${CDL_TBL_NM}"
fi


#Deleting data from STAGE table in CDL_ETL_TEMP

if [ "$STAGE_TABLE_NEEDED" == "YES" ] ; then

sh $CDL_BIN/BTEQ/BTEQ_CDL_STAGE_TBL_DELETE.sh $TBL_NM $TDCH_DB

if [ $? -ne 0 ];then

echo " DELETE FROM STAGE TABLE FAILED FOR ${TBL_NM}"
fi

fi
echo "Put Dir"
echo hadoop fs -put -f $audit_dir/coca_audit_${TBL_NM}.txt $CDL_HDFS_AUDIT/CDL_AUDIT_LOG
echo hadoop fs -put -f $audit_dir/audit_${TBL_NM}.txt $CDL_HDFS_AUDIT/CDL_LOAD_LOG

hadoop fs -put -f $audit_dir/audit_${TBL_NM}.txt $CDL_HDFS_AUDIT/CDL_LOAD_LOG/

if [ $? -gt 0 ];then
echo "moving hdfs data file for CDL_LOAD_LOG failed"
exit 1
fi
#Removing Avro Temp Files

CDLAvroFiles=hdfs://nameservice1$HADOOP_PATH
	
#hadoop fs -rmr -skipTrash $CDLAvroFiles
echo "Avro staging files removed"

#Removing Data From Teradata Temp Tables

else

echo "Avro Table and Parquet Table Count not matching.Please check the scripts"
exit 1

fi


# Preparing the audit file in local
echo $CDH_LOAD_LOG_KEY$delimiter$SUBJ_AREA_NM$delimiter$TBL_NM$delimiter$EXTRACT_COLUMN_NM$delimiter$EXTRACT_START_DT$delimiter$EXTRACT_END_DT$delimiter$AVRO_LOAD_STRT_DTM$delimiter$AVRO_LOAD_END_DTM$delimiter$AVRO_ROWS_LOADED_NB$delimiter$PQT_LOAD_STRT_DTM$delimiter$PQT_LOAD_END_DTM$delimiter$PQT_ROWS_LOADED_NB >>$audit_dir/cdl_audit_${TBL_NM}.txt


#Adding data to HDFS for CDH_AUDIT_LOG

hadoop fs -put -f $audit_dir/cdl_audit_${TBL_NM}.txt $CDL_HDFS_AUDIT/CDL_AUDIT_LOG/
if [ $? -gt 0 ]; then
echo "moving hdfs data file for CDL_AUDIT_LOG failed"
exit 1
fi
echo "The audit process for $TBL_NM completed successfully "


#Removing Audit variables from param file
 
sed -i '/export JobRunDate=/d' $1
sed -i '/export JobEndTime=/d' $1
sed -i '/export LoadStartDate=/d' $1
sed -i '/export LoadEndDate=/d' $1
sed -i '/CDH_LOAD_LOG_KEY=/d' $1
sed -i '/export avroRecon_count=/d' $1
