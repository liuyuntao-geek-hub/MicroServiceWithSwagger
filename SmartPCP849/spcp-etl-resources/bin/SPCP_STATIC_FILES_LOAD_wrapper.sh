#!/bin/sh
source ~/.bash_profile
#=================================================================================================================================================
# Title            : STATIS_FILEA_LOAD_WRAPPER
# ProjectName      : 849 SMARTPCP select
# Filename         : STATIS_FILEA_LOAD_WRAPPER.sh
# Description      : Shell Script for Loading static files into splice machine
# Developer        : Anthem
# Created on       : JUNE 2017
# Location         : INDIA
# Date           Auth           Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2018/06/30    Deloitte         1     Initial Version
#====================================================================================================================================================
##  INPUT ARGUMENTS
# 1. Flat File Location

#====================================================================================================================================================
##  PROGRAM STEPS
# 
# 1. Executing the stored procedure from the shell script.
# 2. Get the application id and application name from log file.
# 
#====================================================================================================================================================
## SAMPLE ARGUMENTS
#FLAT_File_Location='cd /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/control' 

#=====================================================================================================================================================

now=$(date +"%Y%m%d%H%M%S")
 
tablename=$1
sqlfilename=$2

echo $FLAT_File_Location
echo $tablename
echo $sqlfilename


#====================================================================================================================================================

sqlshell.sh -h dwbdtest1r1w5.wellpoint.com -u $username -s $password  -f /dv/app/ve2/pdp/spcp/phi/no_gbd/r000/control/$sqlfilename>staticfileslogs.txt

exit



#====================================================================================================================================================
##  End of Script
#====================================================================================================================================================














