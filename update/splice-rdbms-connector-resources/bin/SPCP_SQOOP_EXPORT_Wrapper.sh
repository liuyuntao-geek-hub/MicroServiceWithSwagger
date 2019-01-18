#!/bin/sh

# Title            : SPCP_SQOOP_EXPORT_WRAPPER
# ProjectName      : 849 SMARTPCP select 
# Filename         : SPCP_SQOOP_EXPORT_WRAPPER.sh
# Description      : Shell Script for Member Info Sqoop Export
# Developer        : Anthem
# Created on       : JUNE 2017
# Location         : INDIA

ENV=$1
EDGE_PATH=$2
VIEW_NAME=$3
CONFIG_NAME="src_script_application_"$1".properties"
SUBJECT_AREA="spcp-sqoop-export"

#Fetch properties from Config file -- redirect that to property file in edge node
#source that property files and get the needed params as below
echo $ENV
echo $EDGE_PATH
echo $CONFIG_NAME


source $EDGE_PATH/$CONFIG_NAME
echo $EDGE_PATH/$CONFIG_NAME
echo $HADOOP_SQOOP_OUTPUT_PATH
echo $JAR_FILE_PATH
echo $HADOOP_STREAMING_JAR
echo $HADOOP_SQOOP_INPUT_PATH

if hdfs dfs -test -e ${HADOOP_SQOOP_OUTPUT_PATH} ;then
echo "files exist"
hadoop fs -rm -r -skipTrash ${HADOOP_SQOOP_OUTPUT_PATH}
fi

hadoop jar ${HADOOP_STREAMING_JAR} \
-Dmapreduce.job.queuename=cdl_yarn \
-Dmapred.textoutputformat.separator='|' \
-Dmapred.reduce.tasks=1 \
-input "${HADOOP_SQOOP_INPUT_PATH}/" \
-output "${HADOOP_SQOOP_OUTPUT_PATH}/" \
-mapper cat \
-reducer cat

prev_cmd_status=$?
if [ $prev_cmd_status -ne 0 ]; then
echo "error while merging csv files using hadoop streaming"
exit 1
else
hadoop fs -rm -skipTrash ${HADOOP_SQOOP_OUTPUT_PATH}/_SUCCESS
java -cp ${JAR_FILE_PATH}/${JAR_NAME} com.anthem.hca.splice.export.sqoop.SqoopExport ${EDGE_NODE_PATH}/src_sqoop_${ENV}.conf ${VIEW_NAME}
fi

