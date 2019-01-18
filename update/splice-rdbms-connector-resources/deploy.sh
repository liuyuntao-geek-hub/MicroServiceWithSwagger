#====================================================================================================================================================
# Title            : BitBucket_prod_Migration
# ProjectName      : splice-rdbms-connector-resources
# Filename         : SPCP_BitBucket_Prod_Code_Migration_Release.sh
# Description      : Script moves all the bibucket files to prod respective environment 
# Developer        : Anthem
# Created on       : July 2018
# Location         : ATLANTA
# Logic            : 
# Parameters       : 
# Return codes     : 
# Date                         Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2018/07/18                      1     Initial Version

#  ***************************************************************************************************************************


echo "Executing prod Migration Script.."

source ~/.bash_profile

echo $SPCP_EDGE_ROOT
echo $SPCP_HDFS_ROOT


#Check and create subfolders
if [[ ! -d "$SPCP_EDGE_ROOT/bin/scripts" ]]; then
        mkdir "$SPCP_EDGE_ROOT/bin/scripts"
fi

if [[ ! -d "$SPCP_EDGE_ROOT/bin/jar" ]]; then
        mkdir "$SPCP_EDGE_ROOT/bin/jar"
fi

#Hadoop streaming merge single file  target dir and Sqoop export dir
if ! hadoop fs -test -d "$SPCP_HDFS_ROOT/etl/sqoop_output"; then
    hadoop fs -mkdir -p "$SPCP_HDFS_ROOT/etl/sqoop_output"
fi

#Splice table Import target path
if ! hadoop fs -test -d "$SPCP_HDFS_ROOT/etl/sqoop_input"; then
    hadoop fs -mkdir -p "$SPCP_HDFS_ROOT/etl/sqoop_input"
fi

#Copy Wrapper scripts
cp -f $HOME/splice-rdbms-connector-resources/bin/* $SPCP_EDGE_ROOT/bin/
dos2unix $SPCP_EDGE_ROOT/bin/*.sh
chmod u+x $SPCP_EDGE_ROOT/bin/*.sh
echo "Step 1: scripts copied"

#Copy startshell/kerboros scripts & List file
cp -f $HOME/splice-rdbms-connector-resources/scripts/* $SPCP_EDGE_ROOT/bin/scripts
dos2unix $SPCP_EDGE_ROOT/bin/scripts/*
chmod u+x $SPCP_EDGE_ROOT/bin/scripts/*
echo "Step 2: List files, startshell script and kerberos script copied"

#Copy startshell/kerboros scripts & List file
cp -f $HOME/splice-rdbms-connector-resources/jar/* $SPCP_EDGE_ROOT/bin/jar/
echo "Step 3: jar file copied"

#Copy edge node config files 
cp -f $HOME/splice-rdbms-connector-resources/control/* $SPCP_EDGE_ROOT/control
dos2unix $SPCP_EDGE_ROOT/control/*
echo "Step 4: Script config files copied"

#Copy files to hadoop configuration location

#Copy conf/Query/XML Files
dos2unix $HOME/splice-rdbms-connector-resources/conf/*
hadoop fs -put -f  $HOME/splice-rdbms-connector-resources/conf/*  $SPCP_HDFS_ROOT/control
echo "Step 5: HDFS config and xml files copied"

echo "Execution prod Migration Completed -- End of Script"



