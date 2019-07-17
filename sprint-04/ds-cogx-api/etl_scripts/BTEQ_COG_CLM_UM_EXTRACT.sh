PARM_FILE=$1
. /$COGX_HDP_ENV/app/ve2/ccp/cogx/phi/no_gbd/$COGX_HDP_ENV_RVSN/bin/scripts/$PARM_FILE.parm
exec 1> $logs_dir/`echo $0`_$(date +"%Y%m%d_%H%M%S").log 2>&1
#======================================================================================================================
# Title     	       : BTEQ_COG_CLM_UM_EXTRACT
# Filename   	       : BTEQ_COG_CLM_UM_EXTRACT.sh
# Description 	       : This script extracts data from UM tables in EDWARD
# Source Tables        : UM_SRVC,UM_SRVC_PROV,UM_SRVC_STTS,UM_RQST_PROV,UM_RQST
# Target Tables	       : UM_EXTACT.CSV
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

echo "RFRNC_NBR^SRVC_LINE_NBR^SRC_UM_SRVC_STTS_CD^SRC_SBSCRBR_ID^SRC_MBR_CD^PRMRY_DIAG_CD^RQSTD_PLACE_OF_SRVC_CD^SRC_RQSTD_PLACE_OF_SRVC_CD^AUTHRZD_PLACE_OF_SRVC_CD^SRC_AUTHRZD_PLACE_OF_SRVC_CD^RQSTD_SRVC_FROM_DT^AUTHRZD_SRVC_FROM_DT^RQSTD_SRVC_TO_DT^AUTHRZD_SRVC_TO_DT^RQSTD_PROC_SRVC_CD^AUTHRZD_PROC_SRVC_CD^RQSTD_QTY^AUTHRZD_QTY^SRC_UM_PROV_ID^PROV_ID^src_um_prov_id_rp^prov_id_rp^SRC_PROV_FRST_NM_RP^SRC_PROV_LAST_NM_RP^SRC_PROV_FRST_NM^SRC_PROV_LAST_NM" > $output_dir/UM_extract.txt

bteq<<EOF



/*  put BTEQ in Transaction mode  */
.SET SESSION TRANSACTION BTET;

/* PARM_FILE gives the file path containing the logon details  */
.run file $LOGON/${LOGON_ID}; 

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

DATABASE $ETL_VIEWS_DB;

CREATE VOLATILE TABLE sub (sbscrbr varchar(10)) on commit preserve rows;


.IMPORT VARTEXT ',' FILE = $output_dir/UM_sub_extract.txt;
.REPEAT* pack 1000

.quiet on


USING sbscrbr (VARCHAR(10)) insert into sub values (:sbscrbr);


.EXPORT FILE = $output_dir/UM_extract.txt;
.set underline off;
.set titledashes off;

/***************** Error Handling ********************************/
.IF ERRORCODE <> 0 THEN .GOTO ERRORS
/***************** Error Handling ********************************/

select 
distinct
cast(coalesce(trim( r.RFRNC_NBR),'') as varchar(25))   ||'^'||
cast(coalesce(trim( s.SRVC_LINE_NBR),'') as  varchar(7))  ||'^'||
cast(coalesce(trim(stts.SRC_UM_SRVC_STTS_CD),'') as  varchar(10)) ||'^'||
   cast(coalesce(trim(r.src_sbscrbr_id),'') as varchar(15)) ||'^'||
  cast(coalesce(trim( r.src_mbr_cd),'') as varchar(3)) ||'^'||
  cast(coalesce(trim( r.prmry_diag_cd),'') as varchar(10)) ||'^'||
   cast(coalesce(trim(s.RQSTD_PLACE_OF_SRVC_CD),'') as varchar(10)) ||'^'||
   cast(coalesce(trim(s.SRC_RQSTD_PLACE_OF_SRVC_CD),'') as varchar(15)) ||'^'||
   cast(coalesce(trim(s.AUTHRZD_PLACE_OF_SRVC_CD),'') as varchar(10)) ||'^'||
   cast(coalesce(trim(s.SRC_AUTHRZD_PLACE_OF_SRVC_CD),'') as varchar(15))  ||'^'||
   cast(coalesce(trim(s.RQSTD_SRVC_FROM_DT ),'') as varchar(10)) ||'^'||
   cast(coalesce(trim(s.AUTHRZD_SRVC_FROM_DT),'') as varchar(10)) ||'^'||
  cast(coalesce(trim( s.RQSTD_SRVC_TO_DT),'') as varchar(10)) ||'^'||
   cast(coalesce(trim(s.AUTHRZD_SRVC_TO_DT),'') as varchar(10)) ||'^'||
  cast(coalesce(trim( s.RQSTD_PROC_SRVC_CD),'') as varchar(11)) ||'^'||
  cast(coalesce(trim( s.AUTHRZD_PROC_SRVC_CD),'') as varchar(11))  ||'^'||
  case when trim(s.RQSTD_QTY) ='.00000' then '0.00000'
  else trim (LEADING '0' FROM coalesce(trim(cast(s.RQSTD_QTY as DECIMAL(17,5) FORMAT 'Z99999999999999999.99999')),'')) end   ||'^'||
   case when trim(s.AUTHRZD_QTY) ='.00000' then '0.00000'
  else trim (LEADING '0' FROM coalesce(trim(cast(s.AUTHRZD_QTY as DECIMAL(17,5) FORMAT 'Z99999999999999999.99999')),'')) end   ||'^'||
cast(coalesce(trim(sp.src_um_prov_id),'') as varchar(30))  ||'^'||
cast(coalesce(trim(sp.prov_id),'') as varchar(16))  ||'^'||
cast(coalesce(trim(rp.src_um_prov_id),'') as varchar(30))  ||'^'||
cast(coalesce(trim(rp.prov_id),'') as varchar(16))  ||'^'||
cast(coalesce(trim(rp.SRC_PROV_FRST_NM),'') as varchar(40)) ||'^'||
cast(coalesce(trim(rp.SRC_PROV_LAST_NM),'') as varchar(40)) ||'^'||
cast(coalesce(trim(sp.SRC_PROV_FRST_NM),'') as varchar(40)) ||'^'||
cast(coalesce(trim(sp.SRC_PROV_LAST_NM),'') as varchar(40)) (title '')
    from
    um_rqst r
    inner join
    UM_SRVC s
    on 
    r.RFRNC_NBR = s.RFRNC_NBR
    left join
    UM_RQST_PROV rp
    on
    r.RFRNC_NBR = rp.RFRNC_NBR
    left join
    UM_SRVC_PROV sp
    on
    r.RFRNC_NBR = sp.RFRNC_NBR
    and s.SRVC_LINE_NBR = sp.SRVC_LINE_NBR
    left join
    UM_SRVC_STTS stts
    on
    r.RFRNC_NBR = stts.RFRNC_NBR
    and s.SRVC_LINE_NBR = stts.SRVC_LINE_NBR
    where
    r.src_sbscrbr_id = sub.sbscrbr       
    and stts.SRC_UM_SRVC_STTS_CD not in ('DENIED','CNCLED','PEND','PND');
	
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
