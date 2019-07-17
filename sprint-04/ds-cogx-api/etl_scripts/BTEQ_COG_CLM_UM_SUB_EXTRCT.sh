PARM_FILE=$1
. /$COGX_HDP_ENV/app/ve2/ccp/cogx/phi/no_gbd/$COGX_HDP_ENV_RVSN/bin/scripts/$PARM_FILE.parm
exec 1> $logs_dir/`echo $0`_$(date +"%Y%m%d_%H%M%S").log 2>&1
#======================================================================================================================
# Title     	       : BTEQ_COG_CLM_UM_SUB_EXTRACT
# Filename   	       : BTEQ_COG_CLM_UM__SUB_EXTRACT.sh
# Description 	       : This script extracts scubscriber from UM tables in EDWARD
# Source Tables        : WGS_CLM_HDR_SGMNT_PEND
# Target Tables	       : UM__SUB_EXTACT.txt
# Developer            : ANTHEM
# Created on   	       : 
# Location     	       : ATLANTA, GA
# Logic                : 
# Parameters	       : 

#===============================================================================
#BTEQ Script
#===============================================================================

PARM_FILE=$1

echo "script file="$0
echo "parm file="$PARM_FILE.parm

rm -f $output_dir/UM_sub_extract.txt;


bteq<<EOF


/*  put BTEQ in Transaction mode  */
.SET SESSION TRANSACTION BTET;

/* PARM_FILE gives the file path containing the logon details  */
.run file $LOGON/${LOGON_ID_SUB}; 

/***************** Error Handling ********************************/
.IF ERRORCODE <> 0 THEN .GOTO ERRORS
/***************** Error Handling ********************************/

.set width 1500;

SELECT SESSION;

/***************** Error Handling ********************************/
.IF ERRORCODE <> 0 THEN .GOTO ERRORS
/***************** Error Handling ********************************/

SET QUERY_BAND='ApplicationName=$0;Frequency=DAILY;' FOR SESSION;

/***************** Error Handling ********************************/
.IF ERRORCODE <> 0 THEN .GOTO ERRORS
/***************** Error Handling ********************************/

DATABASE $ETL_VIEWS_DB_SUB;


.EXPORT FILE = $output_dir/UM_sub_extract.txt;
.set underline off;
.set titledashes off;

/***************** Error Handling ********************************/
.IF ERRORCODE <> 0 THEN .GOTO ERRORS
/***************** Error Handling ********************************/

select 
distinct 
cast(coalesce(trim(SBSCRBR_CERTFN_1_NBR||SBSCRBR_CERTFN_2_NBR||SBSCRBR_CERTFN_3_NBR),'') as varchar(10)) (title '')
 from
WGS_CLM_HDR_SGMNT_PEND
 where
(ERR_1_CD in ('UM0','UM1','UM2','PAM') or 
ERR_2_CD in ('UM0','UM1','UM2','PAM') or 
ERR_3_CD in ('UM0','UM1','UM2','PAM') or 
ERR_4_CD in ('UM0','UM1','UM2','PAM') or 
ERR_5_CD in ('UM0','UM1','UM2','PAM') or 
ERR_6_CD in ('UM0','UM1','UM2','PAM') or 
ERR_7_CD in ('UM0','UM1','UM2','PAM') or 
ERR_8_CD in ('UM0','UM1','UM2','PAM') or 
ERR_9_CD in ('UM0','UM1','UM2','PAM') or 
ERR_10_CD in ('UM0','UM1','UM2','PAM') or 
ERR_11_CD in ('UM0','UM1','UM2','PAM') or 
ERR_12_CD in ('UM0','UM1','UM2','PAM') or 
ERR_13_CD in ('UM0','UM1','UM2','PAM') or 
ERR_14_CD in ('UM0','UM1','UM2','PAM') or 
ERR_15_CD in ('UM0','UM1','UM2','PAM') or 
ERR_16_CD in ('UM0','UM1','UM2','PAM') or 
ERR_17_CD in ('UM0','UM1','UM2','PAM') or 
ERR_18_CD in ('UM0','UM1','UM2','PAM') or 
ERR_19_CD in ('UM0','UM1','UM2','PAM') or 
ERR_20_CD in ('UM0','UM1','UM2','PAM') or 
ERR_21_CD in ('UM0','UM1','UM2','PAM') or 
ERR_22_CD in ('UM0','UM1','UM2','PAM') or 
ERR_23_CD in ('UM0','UM1','UM2','PAM') or 
ERR_24_CD in ('UM0','UM1','UM2','PAM') or 
ERR_25_CD in ('UM0','UM1','UM2','PAM') or 
ERR_26_CD in ('UM0','UM1','UM2','PAM') or 
ERR_27_CD in ('UM0','UM1','UM2','PAM') or 
ERR_28_CD in ('UM0','UM1','UM2','PAM') or 
ERR_29_CD in ('UM0','UM1','UM2','PAM') or 
ERR_30_CD in ('UM0','UM1','UM2','PAM') or 
ERR_31_CD in ('UM0','UM1','UM2','PAM') or 
ERR_32_CD in ('UM0','UM1','UM2','PAM')) 
and trim(KEY_CHK_DCN_ITEM_CD) = '80';

	
.EXPORT RESET; 

.LOGOFF
/***************** Error Handling ********************************/
.IF ERRORCODE <> 0 THEN .GOTO ERRORS
/***************** Error Handling ********************************/


.QUIT 0

.LABEL ERRORS

.QUIT ERRORCODE

.LABEL LOADFAIL

.QUIT 100


EOF

# show AIX return code and exit with it
RETURN_CODE=$?
echo "script return code=$RETURN_CODE"
echo "completed at `date +"%Y%m%d_%H%M%S"`"
exit $RETURN_CODE
