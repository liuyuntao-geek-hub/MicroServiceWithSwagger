

#======================================================================================================================
# Title                : EHUB_unzip.sh
# Filename             : EHUB_unzip.sh.sh
# Description          : This script unzips the extract file and push it hdfs for processing.
# Developer            : ANTHEM
# Created on           :
# Location             : ATLANTA, GA
# Logic                :
# Parameters           :

#===============================================================================
#shell Script
#===============================================================================

#Load the bash profile/ .profile/
#source $HOME/.bash_profile
#source $HOME/.profile

#Defaulting the profile variables for non-service ids
if [ "$(whoami)" != "srcccpcogxbthpr" ]; then
        CONTROL_DIR="/pr/app/ve2/ccp/cogx/phi/no_gbd/r000/control"
        HDFS_DIR="/pr/hdfsapp/ve2/ccp/cogx/phi/no_gbd/r000/bin/"
        BIN_DIR="/pr/app/ve2/ccp/cogx/phi/no_gbd/r000/bin/"
        LOG_DIR="/pr/app/ve2/ccp/cogx/phi/no_gbd/r000/bin/logs"
fi

#Load generic functions/Variables
if [ -f $CONTROL_DIR/application_shell.properties ];
then
        source $CONTROL_DIR/application_shell.properties
else
        echo "ERROR - application_shell.properties not available in $CONTROL_DIR"
        exit -1
fi

LOG_FILE=$LOG_DIR/"script_"EHUB_unzip"_"$(v_DATETIME)"_"$USER.log

echo "Reading the parameter file" >>$LOG_FILE

. $CONTROL_DIR/ehub.parm

exec 1> $LOG_FILE 2>&1

cd $inbound_dir

ls *.zip > ehub_unzip_collect.txt

while read file
do
unzip -d $output_dir $file
done < ehub_unzip_collect.txt

echo "Moving the files to HDFS" >>$LOG_FILE

hadoop fs -copyFromLocal $output_dir/* $hdfs_out/

echo "Changing the default permissions of the data files" >>$LOG_FILE
hadoop fs -chmod 777 $hdfs_out/*

echo "Moving the files to backup folder from inbound" >>$LOG_FILE
mv $inbound_dir/* $inbound_backup_dir/  >>$LOG_FILE

echo "EHUB unzip script completed succesfully"

	