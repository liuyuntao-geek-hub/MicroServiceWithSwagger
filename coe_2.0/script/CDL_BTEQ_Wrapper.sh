#!/bin/bash
#====================================================================================================================================================
# Title            : CDL BTEQ Wrapper Script to Load Stage Tables
# ProjectName      : CDL 
# Filename         : CDL_BTEQ_WRAPPER.sh
# Description      : Script to load CDL stage tables on Teradata
# Developer        : Anthem
# Created on       : JUNE 2017
# Location         : ATLANTA
# Logic            : 
# Parameters       : Parameter file name
#Execution          :sh CDL_BTEQ_Wrapper.sh "parameter file name"
# Return codes     : 
# Date                         Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2017/06/13                      1     Initial Version

#  ***************************************************************************************************************************

######### This script does the following 
######### 1. Create a record in the teradata CDL_LOAD_LOG
######### 2. Execute the BTEQ DML to insert data into stage table - Only if needed
######### 3. Excecute script to get the teradata source table count for Reconciliation
echo "In Bteq script"
echo $2
echo $0
BIN_DIR=`dirname $0`
echo "BIN DIR name"
echo $BIN_DIR 
echo source $BIN_DIR/cdl-bl-db-env.conf $2
 source $BIN_DIR/cdl-bl-db-env.conf $2
echo "DB Name"
echo $DB
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
LogPath=/home/$USER
RunDate=`date +%Y:%m:%d:%H:%M:%S`
SummaryLogFileNm=CDL_BTEQ_RUN_`date +%Y%m%d%H%M%S`_${TBL_NM}_$USER.log
SLogFullFileName=$CDL_LOGS/${SummaryLogFileNm}
exec 1> $SLogFullFileName 2>&1



#Source bash_profile to get credentials for Teradata BTEQ Script
#source ./.bash_profile
echo "Logon server name"
echo $LOGON


################################### STEP -1 : Inserting a record into TERADATA Audit Log table  - CDL_LOAD_LOG

echo "STEP -1 : Inserting a record into TERADATA Audit Log table  - CDL_LOAD_LOG"

sh $CDL_BIN/BTEQ/BTEQ_CDL_LOAD_LOG.sh $TBL_NM $SUBJ_AREA_NM $PROCESS_NM $TBL_NM CURRENT_TIMESTAMP $LOAD_END_DTM $PUB_IND

#Check Script successfully executed or not
if [ $? -eq 0 ]; then
    echo "Succefully Inserted record in Teradata CDL_LOAD_LOG table "
else
    echo "Teradata load log entry failed"
    exit 1
fi

#Echo Variables
BTEQ_LOAD_START_DT=$BTEQ_LOAD_START_DATE
BTEQ_LOAD_END_DT=$BTEQ_LOAD_END_DATE

echo "Start End  date for BTEQ"
echo $BTEQ_LOAD_START_DATE
echo  $BTEQ_LOAD_END_DATE


################################### STEP -2 : Insert Data Into Stage table On Teradata ( If Needed )
echo "STEP -2 : Insert Data Into Stage table On Teradata ( If Needed )"

#Echo Variables
STAGE_TABLE_NEEDED=$STAGE_TABLE_NEEDED
BTEQ_LOAD_START_DT=$BTEQ_LOAD_START_DATE
BTEQ_LOAD_END_DT=$BTEQ_LOAD_END_DATE
echo "Start End  date for BTEQ"
echo $STAGE_TABLE_NEEDED
echo $BTEQ_LOAD_START_DATE
echo $BTEQ_LOAD_END_DATE
echo $TBL_NM
echo $DB
echo $SOURCE_CON

#Check whether BTEQ Script execution to INSERT DATA INTO STAGE TABLE is required or not

if [ "$STAGE_TABLE_NEEDED" == "YES" ] ; then

echo "Run BTEQ to load data into Teradata stage table in CDL_ETL_TEMP"

sh $CDL_BIN/BTEQ/BTEQ_${TBL_NM}_LOAD.sh $TBL_NM $TDCH_DB $BTEQ_LOAD_START_DT $BTEQ_LOAD_END_DT "${SOURCE_CON}" $BTEQ_DATABASE_NM

#Check Script successfully executed or not
if [ $? -eq 0 ]; then
    echo "Succefully Executed BTEQ LOAD Script"
else
    echo "BTEQ LOAD Script failed"
    exit 1
fi

else
echo "BTEQ Execution is not required"

fi

#### Deleting the existing recon file ####
if [ -f $CDL_LOGS/Reconciliation/Teradata_count_recon_$TBL_NM.txt ]; then

    rm $CDL_LOGS/Reconciliation/Teradata_count_recon_$TBL_NM.txt
    echo "$?"
    if [ $? -gt 0 ]; then 
    echo "Deleting existing recon file failed."
    exit 1
    fi

fi

################################### STEP -3 : Get The Teradata Source Table Count
echo "STEP -3 : Teradata Source Table Count"

sh $CDL_BIN/BTEQ/BTEQ_TERADATA_RECON_COUNT.sh $TBL_NM $TDCH_DB "${SOURCE_CON}"

#Check Script successfully executed or not
if [ $? -eq 0 ]; then
    echo "Succefully Executed RECON Script"
else
    echo "RECON Script failed"
    exit 1
fi

