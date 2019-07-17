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
rm -f $output_dir/cogx_dup.db

sqlite3 cogx_dup.db <<END_SQL
drop table if exists um;
.mode list
.separator ^
.import DUP_HDR_EXTRACT.txt hist_hdr
.import DUP_DTL_EXTRACT.txt hist_dtl
select count(*)  from hist_hdr;
select count(*)  from hist_dtl;
CREATE INDEX idx_lookup_id on hist_dtl (KEY_CHK_DCN_NBR, KEY_CHK_DCN_ITEM_CD, CLM_CMPLTN_DT);
CREATE INDEX idx_dup_claims on hist_hdr (MEMBER_SSN, PAT_MBR_CD, SRVC_FROM_DT, SRVC_THRU_DT);
END_SQL

last_log=`ls -tr "$logs_dir"/"$0"_* | tail -1`
hdr_sqlite_count=`cat $last_log | sed -n '2p'`
dtl_sqlite_count=`cat $last_log | sed -n '3p'`
echo "hdr_sqlite_count : $hdr_sqlite_count"
echo "dtl_sqlite_count : $dtl_sqlite_count"
hdr_rec_count1=`cat $output_dir/DUP_HDR_EXTRACT.txt | wc -l`
hdr_rec_count=`expr $hdr_rec_count1 - 1`
dtl_rec_count1=`cat $output_dir/DUP_DTL_EXTRACT.txt | wc -l`
dtl_rec_count=`expr $dtl_rec_count1 - 1`

if [ $hdr_sqlite_count -eq $hdr_rec_count ] && [ $dtl_sqlite_count -eq $dtl_rec_count ]
then
	echo "Count matching"
	hdfs dfs -put -f $output_dir/cogx_dup.db $hdfs_out
	#echo "HDR_count : $hdr_sqlite_count | DTL_count : $dtl_sqlite_count" | mail -s "cogx.db generation: counts for today" dl-PDP_COGX_CORE_TEAM@anthem.com
else
	echo "Count NOT matching and will continue using the previous cogx.db"
	echo "HDR_count : $hdr_sqlite_count | DTL_count : $dtl_sqlite_count" | mail -s "cogx.db generation: counts NOT Matching" dl-PDP_COGX_CORE_TEAM@anthem.com
	#hdfs dfs -put -f $output_dir/cogx_dup.db $hdfs_out
	exit 100
fi




RETURN_CODE=$?
echo "script return code=$RETURN_CODE"
echo "completed at `date +"%Y%m%d_%H%M%S"`"
exit $RETURN_CODE
