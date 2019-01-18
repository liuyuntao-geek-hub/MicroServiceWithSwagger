#!/bin/sh
#=================================================================================================================================================
# Title            : SPCP_STATIC_LOAD_WRAPPER
# ProjectName      : 849 SMARTPCP select 
# Filename         : SPCP_STATIC_LOAD_Wrapper.sh
# Description      : Shell Script for spark export 
# Developer        : Anthem
# Created on       : SEPTEMBER 2018
# Location         : INDIA
# Date           Auth           Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2018/09/18    Deloitte         1     Initial Version
#====================================================================================================================================================
##  INPUT ARGUMENTS                                                                                     
# 1Mapping Table Name 
#
#==================================================================================================================================================                                                                                                                                                               
#=================================================================================================================================================== 
## SAMPLE ARGUMENTS
#Environment='dev'
#Edge node path=/dv/app/ve2/pdp/spcp/phi/no_gbd/r000/control
#table_nm='wgsp_pcp_Cp_Cd_mapping'


#=====================================================================================================================================================

source ~/.bash_profile

now=$(date +"%Y%m%d%H%M%S") 

ENV=$1
EDGE_PATH=$2
MAPPING_TABLE_NAME=$3
SPLICE_DB=$4
DATA_PATH=$5
CONFIG_NAME="spcp_etl_script_application_"$1".properties"

#Fetch properties from Config file -- redirect that to property file in edge node
#source that property files and get the needed params as below
echo $MAPPING_TABLE_NAME
echo $SPLICE_DB
echo $EDGE_PATH
echo $CONFIG_NAME
echo $SPLICE_CMD
echo $SPLICE_SQL_PATH
echo $DATA_PATH

source $EDGE_PATH/$CONFIG_NAME
echo $EDGE_PATH/$CONFIG_NAME	
echo $LOG_FILE_PATH

#Creating log file 
log_loc=$LOG_FILE_PATH
echo "log loc== $log_loc"
SPCP_LOG_FILE=${log_loc}"/script_STATIC_LOAD_"$MAPPING_TABLE_NAME"_"$USER.log

echo "SPCP static file load wrapper triggered at $now" >>$SPCP_LOG_FILE
if [ $# -eq 5 ]
    then
        echo "Argument check completed"  >>$SPCP_LOG_FILE
    else
		echo "Error in number of arguments passed, Script needs 5 arguments for execution"  >>$SPCP_LOG_FILE
		exit 1
fi




#====================================================================================================================================================
# Create and Run sqlshell command

echo "delete from ${SPLICE_DB}.${MAPPING_TABLE_NAME};

    call SYSCS_UTIL.IMPORT_DATA('${SPLICE_DB}', '${MAPPING_TABLE_NAME}',
    null,
    '${DATA_PATH}',
    '${FILE_DELIMITER}',null,'${TIMESTAMP_FORMAT}','${DATE_FORMAT}',null,25,'/user/$USER/bad_records', true, null);" > $SPLICE_SQL_PATH/SPCP_STATIC_LOAD_${MAPPING_TABLE_NAME}_${now}.sql


sqlshell.sh -h $SPLICE_CMD -u $USER_NAME -s $SPCP_SPLICE_PWD  -f $SPLICE_SQL_PATH/SPCP_STATIC_LOAD_${MAPPING_TABLE_NAME}_${now}.sql 

#=====================================================================================================================================================

#Compare application status
if [ $? -ne 0 ]
then 
echo "Static file load job for "$MAPPING_TABLE_NAME" failed.Please check the log." >>$SPCP_LOG_FILE
exit 1
else
echo "Static file load job for "$MAPPING_TABLE_NAME" completed successfully." >>$SPCP_LOG_FILE
fi

#====================================================================================================================================================
##  End of Script      
#====================================================================================================================================================
