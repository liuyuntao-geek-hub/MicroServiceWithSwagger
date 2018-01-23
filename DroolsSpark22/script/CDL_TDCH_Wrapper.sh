!/usr/bin/bsh
#t TD_DB_SRVR=DWTEST3COP1.WELLPOINT.COM o====================================================================================================================================================
# Title            : CDL_TDCH_WRAPPER
ame      : CDL BaseLine Loads
# Filename         : CDL_TDCH_WRAPPER.sh
# Description      : Script to execute TDCH to extract source data and load into Hadoop in Avro file format.
# Developer        : Anthem
# Created on       : JUNE 2017
# Location         : ATLANTA
# Logic            :
# Parameters       : Parameter file name
# Execution        :sh CDL_TDCH_Wrapper.sh "parameterfile name
# Return codes     :
# Date                         Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2017/06/13                      1     Initial Version

#  ***************************************************************************************************************************

######### This script does the following
######### 1. Execute TDCH  to import data in Avro format
######### 2. Write LoadstartDate And LoadEndDate in param file

#Source DBSRVR config file
#Taking user input and passing to DBSRVR file

echo $0
BIN_DIR=`dirname $0`
echo $BIN_DIR
source $BIN_DIR/cdl-bl-db-env.conf $2
#Source Hadoop Conf file
source $BIN_DIR/cdl-bl-env.conf

echo $TDCHQUERYBAND
echo $TERADATA_TDCH_URL
#Source Hadoop Conf file
#source $BIN_DIR/HADOOP_PARM.conf
#. /dv/app/ve2/cdp/edwd/phi/no_gbd/r000/bin/HADOOP_PARM.conf

#Source the parameter file
echo $1
source $1
#echo $2
#echo " THIS IS THE SECOND ARGUEMENT"

echo  $CDL_CONF
echo  $CDL_PARAM
echo  $CDL_LOGS


#Source bash_profile to get credentials
"CDL_TDCH_Wrapper.sh" 221L, 6037C                   Name      : CDL BaseLine Loads
# Filename         : CDL_TDCH_WRAPPER.sh
# Description      : Script to execute TDCH to extract source data and load into Hadoop in Avro file format.
# Developer        : Anthem
# Created on       : JUNE 2017
# Location         : ATLANTA
# Logic            : 
# Parameters       : Parameter file name
# Execution        :sh CDL_TDCH_Wrapper.sh "parameterfile name
# Return codes     : 
# Date                         Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2017/06/13                      1     Initial Version

#  ***************************************************************************************************************************

######### This script does the following 
######### 1. Execute TDCH  to import data in Avro format
######### 2. Write LoadstartDate And LoadEndDate in param file

#Source DBSRVR config file
#Taking user input and passing to DBSRVR file

echo $0
BIN_DIR=`dirname $0`
echo $BIN_DIR
source $BIN_DIR/cdl-bl-db-env.conf $2
#Source Hadoop Conf file
source $BIN_DIR/cdl-bl-env.conf

echo $TDCHQUERYBAND
echo $TERADATA_TDCH_URL
#Source Hadoop Conf file
#source $BIN_DIR/HADOOP_PARM.conf
#. /dv/app/ve2/cdp/edwd/phi/no_gbd/r000/bin/HADOOP_PARM.conf

#Source the parameter file
echo $1 
source $1
#echo $2
#echo " THIS IS THE SECOND ARGUEMENT"

echo  $CDL_CONF
echo  $CDL_PARAM
echo  $CDL_LOGS


#Source bash_profile to get credentials
#source ~/.bash_profile

#Job Unix Log File Location
JobRunDate=`date +%Y:%m:%d:%H:%M:%S`
SummaryLogFileNm=CDL_TDCH_`date +%Y%m%d%H%M%S`_${TBL_NM}_$USER.log
SLogFullFileName=$CDL_LOGS/${SummaryLogFileNm}
exec 1> $SLogFullFileName 2>&1


error_count=0

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
        error_count=`expr $error_count + 1`
    fi
}




# Variables
teradata_table_name=$TBL_NM
teradata_table_name_for_hive="'$teradata_table_name'"
echo "Hive query table name"
echo $teradata_table_name_for_hive

echo "STEP -1 : Execute the TDCH job to import the data into HDFS in Avro Format"

#Echo variables
echo $TBL_NM
echo $HADOOP_PATH
echo $SOURCE_CON
echo $NUM_MAPPERS
echo $SOURCE_TARGET_FIELD_NAMES
echo $AVRO_SCHEMA_FILE_NAME
echo $TERADATA_TDCH_URL
echo "TDCH Seperator"
echo $TDCH_Seperator


#Job End Date
JobRunDate=`date +%Y:%m:%d:%H:%M:%S`
echo $JobRunDate



#Create directory if not exist
tdch_avro_schema=$CDL_DDL/Avro_Schema
if [[ ! -e $tdch_avro_schema ]]; then
    mkdir $tdch_avro_schema
elif [[  -d $tdch_avro_schema ]]; then
    echo "$tdch_avro_schem already exist"
fi

# copying schema to local path

AvSchemaFile=$tdch_avro_schema/$AVRO_SCHEMA_FILE_NAME

if [ -f "$AvSchemaFile" ]
then
    rm $AvSchemaFile

   echo "AvSchemaFile Temp  schema File deleted"
fi

AvroSchema=$tdch_avro_schema/$AVRO_SCHEMA_FILE_NAME

echo "Copying Avro Schema Filei from HDFS to APP folder"

hadoop fs -copyToLocal $AVRO_TEMP_LOCATION/$AVRO_SCHEMA_FILE_NAME $tdch_avro_schema/

if [ $? -gt 0 ]; then
echo "Avro schema copy to local folder failed"
exit 1
fi

#echo "Avro Location"
#echo hadoop fs -copyToLocal $AVRO_TEMP_LOCATION/$AVRO_SCHEMA_FILE_NAME $tdch_avro_schema/


hadoop fs -test -d hdfs://nameservice1$HADOOP_PATH

if [ $? -eq 0 ] ; then
echo "Avro data files exists. It will be deleted"
hadoop fs -rmr -skipTrash hdfs://nameservice1$HADOOP_PATH
else
echo " No older avro data files exists"
fi

echo $USER
echo TDCHMETHODS : $NUM_MAPPERS

echo "Avro Schema File"
echo file:////$tdch_avro_schema/$AVRO_SCHEMA_FILE_NAME 
echo    hadoop jar $TDCH_JAR com.teradata.connector.common.tool.ConnectorImportTool \
        -libjars $HIVE_LIB_JARS -classname com.teradata.jdbc.TeraDriver \
        -url $TERADATA_TDCH_URL \
        -username $TD_DB_ACCT -password $PASSWORD \
        -queryband $TDCHQUERYBAND \
        -jobtype hdfs \
        -fileformat avrofile  \
        -sourcetable $teradata_table_name \
        -nummappers $NUM_MAPPERS \
        -method $TDCHMETHODS \
        -separator $TDCH_Seperator \
        -targetpaths hdfs://nameservice1$HADOOP_PATH\
        -avroschemafile file:////$tdch_avro_schema/$AVRO_SCHEMA_FILE_NAME \
        -sourceconditions "$SOURCE_CON"

echo "Executing TDCH........."	
#TDCH import command	
	hadoop jar $TDCH_JAR com.teradata.connector.common.tool.ConnectorImportTool \
	-libjars $HIVE_LIB_JARS -classname com.teradata.jdbc.TeraDriver \
	-url $TERADATA_TDCH_URL \
	-username $TD_DB_ACCT -password $PASSWORD \
        -queryband $TDCHQUERYBAND \
	-jobtype hdfs \
	-fileformat avrofile  \
	-sourcetable $teradata_table_name \
	-nummappers $NUM_MAPPERS \
        -method $TDCHMETHODS \
	-separator $TDCH_Seperator \
	-targetpaths hdfs://nameservice1$HADOOP_PATH\
	-avroschemafile file:////$tdch_avro_schema/$AVRO_SCHEMA_FILE_NAME \
	-sourceconditions "$SOURCE_CON" 
  

	insrtRC=$?
	fnStatusCheck $insrtRC "$teradata_table_name is Loaded"  "Error in loading $teradata_table_name"

	fnLogMsg INFO "CDL load process into Avro format is complete for the table  : $teradata_table_name, JobRunDate : $JobRunDate"

echo "Avro Parquet Insert Job Completed"	


#Job End Date
LoadEndDate=`date +%Y:%m:%d:%H:%M:%S`
echo $LoadEndDate
JobEndTime=$LoadEndDate

echo "Job end Time TDCH"
echo $JobEndTime


#Exporting  and appending variables  to Param file for Audit script

echo "Writing variables in file"
echo /home/$USER/$1
echo export JobRunDate=$JobRunDate >>$1
echo export JobEndTime=$JobEndTime>>$1


