#======================================================================================================================
# Title     	       : cogx_clm_sqlite_conv
# Filename   	       : cogx_clm_sqlite_conv.sh
# Description 	       : This script import data into SQITE and push to HDFS
# Developer            : ANTHEM
# Created on   	       : 
# Location     	       : ATLANTA, GA
# Logic                : 
# Parameters	       : 

#===============================================================================
#BTEQ Script
#===============================================================================
PARM_FILE=$1
. /$COGX_HDP_ENV/app/ve2/ccp/cogx/phi/no_gbd/$COGX_HDP_ENV_RVSN/bin/scripts/$PARM_FILE.parm
exec 1> $logs_dir/`echo $0`_$(date +"%Y%m%d_%H%M%S").log 2>&1
echo "$PARM_FILE.parm"

cd $output_dir
export PATH=/opt/cloudera/parcels/Anaconda:/opt/cloudera/parcels/Anaconda/bin:$PATH
source activate cognitive-claims
rm -f $output_dir/cogx.db

sqlite3 cogx.db <<END_SQL
drop table if exists um;
.mode list
.separator ^
.import UM_extract.txt um
select count(*)  from um;
CREATE INDEX idx_SRC_SBSCRBR_ID ON um (SRC_SBSCRBR_ID);
END_SQL

last_log=`ls -tr "$logs_dir"/"$0"_* | tail -1`
sqlite_count=`cat $last_log | sed -n '2p'`
echo "sqlite_count : $sqlite_count"
file_rec_count1=`cat $output_dir/UM_extract.txt | wc -l`
file_rec_count=`expr $file_rec_count1 - 1`
subs_count=`cat $output_dir/UM_sub_extract.txt | wc -l`
echo "UM extract file record count : $file_rec_count"
echo "subscriber count : $subs_count"
if [ $sqlite_count -eq $file_rec_count ]
then
	echo "Count matching"
	hdfs dfs -put -f $output_dir/cogx.db $hdfs_out
	echo "sqlite_count : $sqlite_count | UM extract file record count : $file_rec_count | subscriber count : $subs_count" | mail -s "cogx.db generation: counts for today" dl-PDP_COGX_CORE_TEAM@anthem.com
else
	echo "Count NOT matching and will continue using the previous cogx.db"
	echo "sqlite_count : $sqlite_count | UM extract file record count : $file_rec_count | cogx.db and extract file count NOT matching" | mail -s "ATTN:cogx.db:Count NOT matching" dl-PDP_COGX_CORE_TEAM@anthem.com
	exit 100
fi


RETURN_CODE=$?
echo "script return code=$RETURN_CODE"
echo "completed at `date +"%Y%m%d_%H%M%S"`"
exit $RETURN_CODE
