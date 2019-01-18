#====================================================================================================================================================
# Title            : BitBucket_prod_Migration
# ProjectName      : spcp-etl-resources
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

if [[ ! -d "$SPCP_EDGE_ROOT/ddl/splice" ]]; then
        mkdir "$SPCP_EDGE_ROOT/ddl/splice"
fi

if [[ ! -d "$SPCP_EDGE_ROOT/etl/splicesql" ]]; then
        mkdir "$SPCP_EDGE_ROOT/etl/splicesql"
fi


# HDFS folder checking
if ! hadoop fs -test -d "$SPCP_HDFS_USER_HOME/flat_files"; then
    hadoop fs -mkdir -p "$SPCP_HDFS_USER_HOME/flat_files"
fi

if ! hadoop fs -test -d "$SPCP_HDFS_USER_HOME/bad_records"; then
    hadoop fs -mkdir -p "$SPCP_HDFS_USER_HOME/bad_records"
fi

if ! hadoop fs -test -d "$SPCP_HDFS_ROOT/etl/vbp"; then
    hadoop fs -mkdir -p "$SPCP_HDFS_ROOT/etl/vbp"
fi



#Copy Wrapper scripts
cp -f $HOME/spcp-etl-resources/bin/* $SPCP_EDGE_ROOT/bin/
dos2unix $SPCP_EDGE_ROOT/bin/*.sh
chmod u+x $SPCP_EDGE_ROOT/bin/*.sh
echo "Step 1: scripts copied"

#Copy startshell/kerboros scripts & List file
cp -f $HOME/spcp-etl-resources/scripts/* $SPCP_EDGE_ROOT/bin/scripts
dos2unix $SPCP_EDGE_ROOT/bin/scripts/*
chmod u+x $SPCP_EDGE_ROOT/bin/scripts/*
echo "Step 2: List files, startshell script and kerberos script copied"

#Copy jar file
cp -f $HOME/spcp-etl-resources/jar/* $SPCP_EDGE_ROOT/bin/jar/
echo "Step 3: jar file copied"

#Copy edge node config files 
cp -f $HOME/spcp-etl-resources/control/* $SPCP_EDGE_ROOT/control
dos2unix $SPCP_EDGE_ROOT/control/*
echo "Step 4: Script config files copied"

#Copy Splice DDL's 
cp -f $HOME/spcp-etl-resources/sql/ddl/splice/* $SPCP_EDGE_ROOT/ddl/splice
dos2unix $SPCP_EDGE_ROOT/ddl/splice/*
echo "Step 5: Splice DDL files copied"


#Copy Ref adrs and Static data load stored proc call files
cp -f $HOME/spcp-etl-resources/sql/splice/* $SPCP_EDGE_ROOT/etl/splicesql
dos2unix $SPCP_EDGE_ROOT/etl/splicesql/*
echo "Step 6: Ref adrs and Static data load stored proc call sql files copied"


#Copy files to hadoop configuration location

#Copy conf/Query/XML Files
dos2unix $HOME/spcp-etl-resources/conf/*
hadoop fs -put -f  $HOME/spcp-etl-resources/conf/*  $SPCP_HDFS_ROOT/control
echo "Step 7: HDFS config and xml files copied"

#Copy Static data to hdfs
dos2unix $HOME/spcp-etl-resources/data/*
hadoop fs -put -f $HOME/spcp-etl-resources/data/* $SPCP_HDFS_USER_HOME/flat_files/
#splce flat file import need full previleges for hbase user on source and bad records paths
hadoop fs -chmod -R 777 $SPCP_HDFS_USER_HOME/flat_files
hadoop fs -chmod -R 777 $SPCP_HDFS_USER_HOME/bad_records

echo "Step 8: Static data copied to hdfs "

echo "Execution prod Migration Completed -- End of Script"
