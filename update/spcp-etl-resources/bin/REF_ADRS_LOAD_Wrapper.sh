#!/bin/sh
#=================================================================================================================================================
# Title            : SPCP_REF_ADRS_LOAD_WRAPPER
# ProjectName      : 849 SMARTPCP select 
# Filename         : SPCP_REF_ADRS_LOAD_Wrapper.sh
# Description      : Shell Script for ref_adrs data load 
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


#=====================================================================================================================================================

source ~/.bash_profile

now=$(date +"%Y%m%d%H%M%S")

ENV=$1
EDGE_PATH=$2

CONFIG_NAME="spcp_etl_script_application_"$1".properties"

#Fetch properties from Config file -- redirect that to property file in edge node
#source that property files and get the needed params as below

echo $EDGE_PATH
echo $CONFIG_NAME
echo $SPLICE_CMD
echo $SPLICE_SQL_PATH

source $EDGE_PATH/$CONFIG_NAME
echo $EDGE_PATH/$CONFIG_NAME	
echo $LOG_FILE_PATH

#Creating log file 
log_loc=$LOG_FILE_PATH
echo "log loc== $log_loc"
SPCP_LOG_FILE=${log_loc}"/script_REF_ADRS_LOAD_"$USER.log

echo "SPCP ref_adrs load wrapper triggered at $now" >>$SPCP_LOG_FILE
if [ $# -eq 2 ]
    then
        echo "Argument check completed"  >>$SPCP_LOG_FILE
    else
		echo "Error in number of arguments passed, Script needs 2 arguments for execution"  >>$SPCP_LOG_FILE
		exit 1
fi




#====================================================================================================================================================
# Run sqlshell command
sqlshell.sh -h $SPLICE_CMD -u $USER_NAME -s $SPCP_SPLICE_PWD  -f $SPLICE_SQL_PATH/ref_adrs_data_load.sql 

#=====================================================================================================================================================

#Compare application status
if [ $? -ne 0 ]
then 
echo "Load job for REF_ADRS failed.Please check the log." >>$SPCP_LOG_FILE
exit 1
else
echo "Load job for REF_ADRS completed successfully." >>$SPCP_LOG_FILE
fi

#====================================================================================================================================================
##  End of Script      
#====================================================================================================================================================
