#====================================================================================================================================================
# Title            : BitBucket_prod_Migration
# ProjectName      : COGX_ETL
# Filename         : COGX_BitBucket_Prod_Code_Migration_Release.sh
# Description      : Script moves all the bitbucket files to prod respective environment 
# Developer        : Anthem
# Created on       : June 2019
# Location         : INDIA
# Logic            : 
# Parameters       : 
# Return codes     : 
# Date                         Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2019/06/10                      1     Initial Version
#  ***************************************************************************************************************************

#Variables

export SRC_EDGE="/home/srcccpcogxbthpr/Release_06_21_19/ds-cogx-etl-padp-deploy/local"
export SRC_HDFS="/home/srcccpcogxbthpr/Release_06_21_19/ds-cogx-etl-padp-deploy/hdfs"
export SRC_DDL="/home/srcccpcogxbthpr/Release_06_21_19/ds-cogx-etl-padp-deploy/ddl"

#TODO - Check if the below dir are created in PROD bdpr3r6e1pr.wellpoint.com
export EDGE_COGX_DIR="/pr/app/ve2/ccp/cogx/phi/gbd/r000"
export EDGE_COGX_DIR_DATA="/pr/data/ve2/ccp/cogx/phi/gbd/r000"
export HDFS_COGX_DIR="/pr/hdfsapp/ve2/ccp/cogx/phi/gbd/r000"
export HDFS_COGX_INB_DB_DIR="/pr/hdfsdata/ve2/ccp/cogx/phi/gbd/r000/inbound"

echo "**************************************************************"
echo "Executing prod Migration Script.."
echo "**************************************************************"

#-------------------------------------------Folders creation--------------------------------------#
#Check and create subfolders

if [ -d "$EDGE_COGX_DIR/bin/scripts" ];
then
	echo "**************************************************************"
	echo "INFO: $EDGE_COGX_DIR/bin/scripts - Scripts folder already exists"
	echo "**************************************************************"
else
	mkdir $EDGE_COGX_DIR/bin/scripts
	echo "**************************************************************"
	echo "INFO: $EDGE_COGX_DIR/bin/scripts - Scripts folder created"
	echo "**************************************************************"
fi


if [ -d "$EDGE_COGX_DIR/bin/ds-cogx-api" ];
then
   echo "**************************************************************"
   echo "INFO: $EDGE_COGX_DIR/bin/ds-cogx-api folder exists"
   echo "**************************************************************"
else
	mkdir $EDGE_COGX_DIR/bin/ds-cogx-api
	echo "**************************************************************"
	echo "INFO: $EDGE_COGX_DIR/bin/ds-cogx-api - folder created"
	echo "**************************************************************"
fi

if [ -d "$EDGE_COGX_DIR_DATA/inbound/ehub" ];
then
   echo "**************************************************************"
   echo "INFO: $EDGE_COGX_DIR_DATA/inbound/ehub folder exists"
   echo "**************************************************************"
else
	mkdir $EDGE_COGX_DIR_DATA/inbound/ehub
	echo "**************************************************************"
	echo "INFO: $EDGE_COGX_DIR_DATA/inbound/ehub - folder created"
	echo "**************************************************************"
fi

if [ -d "$EDGE_COGX_DIR_DATA/inbound/ehub_bkp" ];
then
   echo "**************************************************************"
   echo "INFO: $EDGE_COGX_DIR_DATA/inbound/ehub_bkp folder exists"
   echo "**************************************************************"
else
	mkdir $EDGE_COGX_DIR_DATA/inbound/ehub_bkp
	echo "**************************************************************"
	echo "INFO: $EDGE_COGX_DIR_DATA/inbound/ehub_bkp - folder created"
	echo "**************************************************************"
fi

if [ -d "$EDGE_COGX_DIR_DATA/outbound/ehub" ];
then
   echo "**************************************************************"
   echo "INFO: $EDGE_COGX_DIR_DATA/outbound/ehub folder exists"
   echo "**************************************************************"
else
	mkdir $EDGE_COGX_DIR_DATA/outbound/ehub
	echo "**************************************************************"
	echo "INFO: $EDGE_COGX_DIR_DATA/outbound/ehub - folder created"
	echo "**************************************************************"
fi

#-------------------------------------------Local copy--------------------------------------#
#Copy startshell/kerboros scripts_test & List file


#UM History and Incremental 
cp -f $SRC_EDGE/scripts/UM_HIST_SPARK.sh $EDGE_COGX_DIR/bin/scripts/
cp -f $SRC_EDGE/scripts/UM_INCR_SPARK.sh $EDGE_COGX_DIR/bin/scripts/
cp -f $SRC_EDGE/scripts/UM_HIST_SPARK_HIVE.sh $EDGE_COGX_DIR/bin/scripts/
cp -f $SRC_EDGE/scripts/UM_INCR_SPARK_HIVE.sh $EDGE_COGX_DIR/bin/scripts/

#BDF History and Incremental
cp -f $SRC_EDGE/scripts/BDF_DUP_hist_SPARK.sh $EDGE_COGX_DIR/bin/scripts/
cp -f $SRC_EDGE/scripts/BDF_DUP_INCR_SPARK.sh $EDGE_COGX_DIR/bin/scripts/

#EHUB 
cp -f $SRC_EDGE/scripts/EHUB_unzip.sh $EDGE_COGX_DIR/bin/scripts/
cp -f $SRC_EDGE/scripts/EHUB_SPARK_LOAD.sh $EDGE_COGX_DIR/bin/scripts/
cp -f $SRC_EDGE/control/ehub.parm $EDGE_COGX_DIR/control/

#Generic scripts
cp -f $SRC_EDGE/control/application_shell.properties $EDGE_COGX_DIR/control/

#OTHER DEPLOYMENT STD SCRIPTS
chmod 755 $EDGE_COGX_DIR/bin/scripts/*
dos2unix $EDGE_COGX_DIR/bin/scripts/*

chmod 755 $EDGE_COGX_DIR/control/*
dos2unix $EDGE_COGX_DIR/control/*

echo "**************************************************************"
echo "Step 3: scripts cleaned up and permission granted"
echo "**************************************************************"

#-------------------------------------------HDFS copy--------------------------------------#

#Check the existance of hadoop jar folder and create if not exist



if hadoop fs -test -d $HDFS_COGX_DIR/control/EhubExtract ;
	then 
	echo "**************************************************************"
	echo "INFO: $HDFS_COGX_DIR/control/EhubExtract HDFS Directory Exist "
	echo "**************************************************************"
else
	
	hadoop fs -mkdir -p $HDFS_COGX_DIR/control/EhubExtract;
	echo "**************************************************************"
	echo  "INFO: EhubsExtract HDFS Directory CREATED"
	echo "**************************************************************"
fi

#COPY JAR FILES
hadoop fs -put -f $SRC_HDFS/jar/ds-cogx-etl-1.0.0.jar $HDFS_COGX_DIR/bin 
echo "**************************************************************"
echo "Step 8.1: $SRC_HDFS/jar/ds-cogx-etl-1.0.0.jar copied to HDFS"
echo "**************************************************************"

#Copy generic files to HDFS
hadoop fs -put -f $SRC_HDFS/control/query_cogxEhub.properties $HDFS_COGX_DIR/control/
hadoop fs -put -f $SRC_HDFS/control/query_cogxHiveBDFHistory.properties $HDFS_COGX_DIR/control/
hadoop fs -put -f $SRC_HDFS/control/query_cogxHiveBDFIncremental.properties $HDFS_COGX_DIR/control/
hadoop fs -put -f $SRC_HDFS/control/query_cogxHiveUMHistory.properties $HDFS_COGX_DIR/control/
hadoop fs -put -f $SRC_HDFS/control/query_cogxHiveUMIncremental.properties $HDFS_COGX_DIR/control/
hadoop fs -put -f $SRC_HDFS/control/query_cogxTeradataUMHistory.properties $HDFS_COGX_DIR/control/
hadoop fs -put -f $SRC_HDFS/control/query_cogxTeradataUMIncremental.properties $HDFS_COGX_DIR/control/
hadoop fs -put -f $SRC_HDFS/control/application_prd.properties $HDFS_COGX_DIR/control/
#get the hbase-site.xml path 
hadoop fs -put -f /etc/hbase/conf/hbase-site.xml $HDFS_COGX_DIR/control/

#######Create DDL for inbound and outbound tables in Hive#########3
## TODO : Check if the beeline command is the right one from prod

beeline -u 'jdbc:hive2://bdpr3hs2lb.wellpoint.com:10000/default;principal=hive/_HOST@US.AD.WELLPOINT.COM;ssl=true' -f $SRC_DDL/COGX_warehouse.hql
if [ $? -ne 0 ];then
	echo "**************************************************************"
	echo "AUDIT table creation failed"
	echo "**************************************************************"
else 
	echo "**************************************************************"
	echo "AUDIT table created"
	echo "**************************************************************"
fi

##TODO : Check for creation of BDF inbound tables
beeline -u 'jdbc:hive2://bdpr3hs2lb.wellpoint.com:10000/default;principal=hive/_HOST@US.AD.WELLPOINT.COM;ssl=true' -f $SRC_DDL/COGX_inbound.hql
if [ $? -ne 0 ];then
	echo "**************************************************************"
	echo "Source Tables creation failed"
	echo "**************************************************************"
else 
	echo "**************************************************************"
	echo "Source Tables created"
	echo "DDL execution completed sucessfully"
	echo "**************************************************************"
fi


hbase shell $SRC_DDL/COGX_hbase_target.txt
if [ $? -ne 0 ];then
	echo "**************************************************************"
	echo "Target Tables creation failed"
	echo "**************************************************************"
else 
	echo "**************************************************************"
	echo "Target Tables created"
	echo "Hbase commands execution completed sucessfully"
	echo "**************************************************************"
fi

echo "**************************************************************"
echo "Migration Script execution completed"
echo "**************************************************************"