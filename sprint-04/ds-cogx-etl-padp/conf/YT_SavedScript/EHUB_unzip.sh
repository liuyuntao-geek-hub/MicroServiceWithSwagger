#======================================================================================================================
# Title     	       : EHUB_unzip.sh
# Filename   	       : EHUB_unzip.sh.sh
# Description 	       : This script unzips the extract file and push it hdfs for processing.
# Developer            : ANTHEM
# Created on   	       : 
# Location     	       : ATLANTA, GA
# Logic                : 
# Parameters	       : 

#===============================================================================
#shell Script
#===============================================================================

. /$COGX_HDP_ENV/app/ve2/ccp/cogx/phi/no_gbd/$COGX_HDP_ENV_RVSN/bin/scripts/ehub.parm
exec 1> $logs_dir/`echo $0`_$(date +"%Y%m%d_%H%M%S").log 2>&1

cd $inbound_dir

ls *.zip > ehub_unzip_collect.txt

while read file
do
unzip -d $output_dir file
done < ehub_unzip_collect.txt
