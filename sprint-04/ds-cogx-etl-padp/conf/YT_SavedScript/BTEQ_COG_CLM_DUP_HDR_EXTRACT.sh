PARM_FILE=$1
. /$COGX_HDP_ENV/app/ve2/ccp/cogx/phi/no_gbd/$COGX_HDP_ENV_RVSN/bin/scripts/$PARM_FILE.parm
exec 1> $logs_dir/`echo $0`_$(date +"%Y%m%d_%H%M%S").log 2>&1
#======================================================================================================================
# Title     	       : BTEQ_COG_CLM_DUP_HDR_EXTRACT
# Filename   	       : BTEQ_COG_CLM_DUP_HDR_EXTRACT.sh
# Description 	       : This script extracts data from ODS tables in DWPROD1
# Source Tables        : WGS_CLM_HDR_SGMNT,WGS_CLM_HDR_SGMNT_PEND,WGS_CLM_AUDT_SGMNT_PEND, WGS_CLM_NATL_SGMNT_2
# Target Tables	       : DUP_HDR_EXTRACT.txt
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

echo "KEY_CHK_DCN_NBR^KEY_CHK_DCN_ITEM_CD^CLM_CMPLTN_DT^CLM_PAYMNT_ACTN_1_CD^CLM_PAYMNT_ACTN_2_6_CD^MEMBER_SSN^PAT_MBR_CD^GRP_NBR^SRVC_FROM_DT^SRVC_THRU_DT^PROV_TAX_ID^PROV_NM^PROV_SCNDRY_NM^PROV_SPCLTY_CD^PROV_LCNS_CD^TOTL_CHRG_AMT^TYPE_OF_BILL_CD^MDCL_RCRD_2_NBR^MRN_NBR^ICD_A_CD^ICD_B_CD^ICD_C_CD^ICD_D_CD^ICD_E_CD^PRVDR_STATUS^CLAIM_TYPE"  > $output_dir/DUP_HDR_EXTRACT.txt

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

.EXPORT FILE = $output_dir/DUP_HDR_EXTRACT.txt;
.set underline off;
.set titledashes off;

/***************** Error Handling ********************************/
.IF ERRORCODE <> 0 THEN .GOTO ERRORS
/***************** Error Handling ********************************/

select
distinct
cast(coalesce(trim(hist.KEY_CHK_DCN_NBR),'') as varchar(11))   ||'^'||
cast(coalesce(trim(hist.KEY_CHK_DCN_ITEM_CD),'') as varchar(2))   ||'^'||
cast(coalesce(trim( hist.CLM_CMPLTN_DT),'') as varchar(10)) ||'^'||
cast(coalesce(trim(hist.CLM_PAYMNT_ACTN_1_CD),'') as varchar(1))   ||'^'||
cast(coalesce(trim(hist.CLM_PAYMNT_ACTN_2_6_CD),'') as varchar(5))   ||'^'||
cast(coalesce(trim(hist.SBSCRBR_UNCRYPTD_CERTFN_1_NBR||hist.SBSCRBR_UNCRYPTD_CERTFN_2_NBR||hist.SBSCRBR_UNCRYPTD_CERTFN_3_NBR),'') as varchar(9))   ||'^'||
trim(trailing '.' from cast(hist.PATEINT_MBR_CD as  varchar(3)))  ||'^'||
--cast(trim(coalesce(hist.PAT_MBR_CD,'')) as varchar(3))    ||'^'||
cast(coalesce(trim(hist.GRP_NBR),'') as varchar(10))   ||'^'||
cast(coalesce(trim( hist.srvc_from_dt),'') as varchar(10)) ||'^'||
cast(coalesce(trim( hist.srvc_thru_dt),'') as varchar(10)) ||'^'||
cast(coalesce(trim(hist.PROV_UNCRYPTD_TAX_ID),'') as varchar(9))   ||'^'||
cast(coalesce(trim(hist.PROV_UNCRYPTD_NM),'') as varchar(25))   ||'^'||
cast(coalesce(trim(hist.PROV_UNCRYPTD_SCNDRY_NM),'') as varchar(25))   ||'^'||
cast(coalesce(trim(hist.PROV_SPCLTY_CD),'') as varchar(3))   ||'^'||
cast(coalesce(trim(hist.PROV_UNCRYPTD_LCNS_ALPH_CD||hist.PROV_UNCRYPTD_LCNS_NMRC_CD),'') as varchar(11))   ||'^'||
case when trim(hist.TOTL_CHRG_AMT) ='.00' then '0.00'  else  trim(hist.TOTL_CHRG_AMT) end   ||'^'|| 
cast(coalesce(trim(EA2.TYPE_OF_BILL_CD),'') as varchar(1))   ||'^'||
cast(coalesce(trim(hist.MDCL_RCRD_2_NBR),'') as varchar(10))   ||'^'||
cast(coalesce(trim(hist.MRN_NBR),'') as varchar(10))   ||'^'||
cast(coalesce(trim(hist.ICD_A_CD),'') as varchar(10))   ||'^'||
cast(coalesce(trim(hist.ICD_B_CD),'') as varchar(10))   ||'^'||
cast(coalesce(trim(hist.ICD_C_CD),'') as varchar(10))   ||'^'||
cast(coalesce(trim(hist.ICD_D_CD),'') as varchar(10))   ||'^'||
cast(coalesce(trim(hist.ICD_E_CD),'') as varchar(10))   ||'^'||
CASE 
       WHEN (hist.ITS_HOME_IND = 'Y' AND hist.ITS_ORGNL_SCCF_NEW_NBR <> '*' AND  hist.ITS_PARG_PROV_IND in ('P' , 'Y')) OR  hist.PROV_IND NOT IN ('D','N') THEN 'PAR'
       ELSE 'NON-PAR'
END ||'^'||
CASE 
    WHEN hist.CLM_TYPE_CD in ('MA', 'PA', 'PC', 'MM', 'PM') then 'PROF'
    WHEN hist.CLM_TYPE_CD in ( 'IA', 'IC', 'ID') then 'INPT'
    WHEN hist.CLM_TYPE_CD in ( 'OA', 'OC', 'OD') then 'OUTPT'
    WHEN hist.CLM_TYPE_CD in ( 'SA', 'SC') then 'SN'
ELSE hist.CLM_TYPE_CD END (title '')
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
crnt.ERR_32_CD in ( 'PAJ','CBC','SCB','DAT','QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','QX','QY','QZ','Q0','Q47','Q5','Q7','Q8','Q9')
) ;
	
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
