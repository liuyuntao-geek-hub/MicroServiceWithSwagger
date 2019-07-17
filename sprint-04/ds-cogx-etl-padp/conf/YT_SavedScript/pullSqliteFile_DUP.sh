#======================================================================================================================
# Title     	       : pullSqliteFile
# Filename   	       : pullSqliteFile.sh
# Description 	       : This script helps to push sqlite db cogx to different edge nodes
# Developer            : ANTHEM
# Created on   	       : 
# Location     	       : ATLANTA, GA
# Logic                : 
# Parameters	       : 

#===============================================================================
#Script
#===============================================================================
PARM_FILE=$1
. /$COGX_HDP_ENV/app/ve2/ccp/cogx/phi/no_gbd/$COGX_HDP_ENV_RVSN/bin/scripts/$PARM_FILE.parm
exec 1> $logs_dir/`echo $0`_$(date +"%Y%m%d_%H%M%S").log 2>&1

echo "script file="$0
echo "parm file="$PARM_FILE.parm

cd $sqlite_out
pwd
rm -f cogx_dup.db
hdfs dfs -get $hdfs_out/cogx_dup.db $sqlite_out/
if [ -d "$sqlite_out/data" ]
then
	echo "data dir already exists"
	mv $sqlite_out/cogx_dup.db $sqlite_out/data
else
	mkdir data
	chmod 755 data
	mv $sqlite_out/cogx_dup.db $sqlite_out/data
fi

# show AIX return code and exit with it
RETURN_CODE=$?
echo "script return code=$RETURN_CODE"
exit $RETURN_CODE

