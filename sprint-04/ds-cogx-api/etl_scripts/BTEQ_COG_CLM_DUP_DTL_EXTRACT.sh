PARM_FILE=$1
. /$COGX_HDP_ENV/app/ve2/ccp/cogx/phi/no_gbd/$COGX_HDP_ENV_RVSN/bin/scripts/$PARM_FILE.parm
exec 1> $logs_dir/`echo $0`_$(date +"%Y%m%d_%H%M%S").log 2>&1
#======================================================================================================================
# Title     	       : BTEQ_COG_CLM_DUP_DTL_EXTRACT
# Filename   	       : BTEQ_COG_CLM_DUP_DTL_EXTRACT.sh
# Description 	       : This script extracts data from UM tables in EDWARD
# Source Tables        : UM_SRVC,UM_SRVC_PROV,UM_SRVC_STTS,UM_RQST_PROV,UM_RQST
# Target Tables	       : DUP_DTL_EXTRACT.txt
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

echo "KEY_CHK_DCN_NBR^KEY_CHK_DCN_ITEM_CD^CLM_CMPLTN_DT^DTL_LINE_NBR^ICD_9_1_CD^PROC_CD^TOS_TYPE_CD^PROC_SRVC_CLS_1_CD^PROC_SRVC_CLS_2_CD^PROC_SRVC_CLS_3_CD^HCPCS_CD^BILLD_CHRGD_AMT^BILLD_SRVC_UNIT_QTY^UNITS_OCR_NBR^PROC_MDFR_CD^HCPCS_MDFR_CD^MDFR_1_CD^MDFR_2_CD^MDFR_3_CD^HCFA_PT_CD^POT_CD^ELGBL_EXPNS_AMT^SRVC_FROM_DT_DTL^SRVC_TO_DT_DTL" > $output_dir/DUP_DTL_EXTRACT.txt;

bteq<<EOF



/*  put BTEQ in Transaction mode  */
.SET SESSION TRANSACTION BTET;

/* PARM_FILE gives the file path containing the logon details  */
.run file $LOGON/${LOGON_ID_ODS};

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

DATABASE $ETL_VIEWS_DB_ODS;


.quiet on



.EXPORT FILE = $output_dir/DUP_DTL_EXTRACT.txt;
.set underline off;
.set titledashes off;

/***************** Error Handling ********************************/
.IF ERRORCODE <> 0 THEN .GOTO ERRORS
/***************** Error Handling ********************************/

select
distinct
cast(coalesce(trim( hist.KEY_CHK_DCN_NBR),'') as varchar(11))   ||'^'||
cast(coalesce(trim( hist.KEY_CHK_DCN_ITEM_CD),'') as varchar(2))   ||'^'||
cast(coalesce(trim( hist.CLM_CMPLTN_DT),'') as varchar(10)) ||'^'||
cast(coalesce(trim( hist.DTL_LINE_NBR),'') as varchar(2))   ||'^'||
cast(coalesce(trim(hist.icd_9_1_cd),'') as varchar(1))   ||'^'||
cast(coalesce(trim(hist.PROC_CD),'') as varchar(5))   ||'^'||
cast(coalesce(trim(hist.TOS_TYPE_CD),'') as varchar(8))   ||'^'||
cast(coalesce(trim(hist.PROC_SRVC_CLS_1_CD),'') as varchar(3))   ||'^'||
cast(coalesce(trim(hist.PROC_SRVC_CLS_2_CD),'') as varchar(3))   ||'^'||
cast(coalesce(trim(hist.PROC_SRVC_CLS_3_CD),'') as varchar(3))   ||'^'||
cast(coalesce(trim(hist.HCPCS_CD),'') as varchar(5))   ||'^'||
case when trim(hist.BILLD_CHRGD_AMT) ='.00' then '0.00'  else  trim(hist.BILLD_CHRGD_AMT) end   ||'^'||
trim(trailing '.' from cast(hist.BILLD_SRVC_UNIT_QTY as  varchar(3)))  ||'^'||
case when trim(hist.UNITS_OCR_NBR) ='.00' then '0.00'  else  trim(hist.UNITS_OCR_NBR) end   ||'^'||
cast(coalesce(trim(hist.PROC_MDFR_CD),'') as varchar(2))   ||'^'||
cast(coalesce(trim(hist.HCPCS_MDFR_CD),'') as varchar(2))   ||'^'||
cast(coalesce(trim(hist.MDFR_1_CD),'') as varchar(2))   ||'^'||
cast(coalesce(trim(hist.MDFR_2_CD),'') as varchar(2))   ||'^'||
cast(coalesce(trim(hist.MDFR_3_CD),'') as varchar(2))   ||'^'||
cast(coalesce(trim(hist.HCFA_PT_CD),'') as varchar(2))   ||'^'||
cast(coalesce(trim(hist.POT_CD),'') as varchar(1))   ||'^'||
case when trim(hist.ELGBL_EXPNS_AMT) ='.00' then '0.00'  else  trim(hist.ELGBL_EXPNS_AMT) end   ||'^'||
cast(coalesce(trim( hist.SRVC_FROM_DT),'') as varchar(10)) ||'^'||
cast(coalesce(trim( hist.SRVC_TO_DT),'') as varchar(10))  (title '')
from WGS_CLM_DTL_SGMNT hist
where hist.KEY_CHK_DCN_NBR in (
select
distinct
hist.KEY_CHK_DCN_NBR
from
WGS_CLM_HDR_SGMNT hist
inner join
WGS_CLM_HDR_SGMNT_PEND crnt
on
crnt.KEY_CHK_DCN_NBR <> hist.KEY_CHK_DCN_NBR and
hist.SBSCRBR_UNCRYPTD_CERTFN_1_NBR||hist.SBSCRBR_UNCRYPTD_CERTFN_2_NBR||hist.SBSCRBR_UNCRYPTD_CERTFN_3_NBR =  crnt.SBSCRBR_UNCRYPTD_CERTFN_1_NBR||crnt.SBSCRBR_UNCRYPTD_CERTFN_2_NBR||crnt.SBSCRBR_UNCRYPTD_CERTFN_3_NBR and
hist.key_chk_dcn_item_cd = '80' and
crnt.key_chk_dcn_item_cd = '80' and
hist.srvc_from_dt = crnt.srvc_from_dt and
hist.srvc_thru_dt = crnt.srvc_thru_dt and
hist.PATEINT_MBR_CD = crnt.PATEINT_MBR_CD
INNER JOIN WGS_CLM_NATL_SGMNT_2 EA2 ON
hist.KEY_CHK_DCN_CENTRY_CD = EA2.KEY_CHK_DCN_CENTRY_CD AND
hist.KEY_CHK_DCN_NBR =     EA2.KEY_CHK_DCN_NBR AND 
hist.CLM_CMPLTN_DT = EA2.CLM_CMPLTN_DT AND
hist.KEY_CHK_DCN_ITEM_CD = EA2.KEY_CHK_DCN_ITEM_CD
where
(crnt.ERR_1_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_2_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_3_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_4_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_5_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_6_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_7_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_8_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_9_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_10_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_11_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_12_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_13_CD in ('PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or 
crnt.ERR_14_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_15_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_16_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_17_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_18_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_19_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_20_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_21_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_22_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_23_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_24_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_25_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_26_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_27_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_28_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_29_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_30_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_31_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9') or
crnt.ERR_32_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9')) )
and hist.key_chk_dcn_item_cd = '80' and hist.icd_9_1_cd>0 ;

.EXPORT RESET; 

.quiet off

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
