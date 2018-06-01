USE ${hivedb};

------VARIANCE------

CREATE  EXTERNAL TABLE IF NOT EXISTS hpip_variance(
tableName string, 
partitionColumn string,
partitionValue string, 
columnName string,
operationDescription string,
operation string, 
operationValue double, 
percentageVariance double,
threshold double,
isThresholdCrossed string,
last_updt_dtm timestamp 
)
STORED AS PARQUET
LOCATION  '${hdfs_path}/hpip_variance';

------MTCLM AND MTCLM_COA------

CREATE EXTERNAL TABLE IF NOT EXISTS mtclm(
clm_adjstmnt_key string , 
mbrshp_sor_cd string , 
clm_its_host_cd string , 
srvc_rndrg_type_cd string , 
src_prov_natl_prov_id string , 
src_billg_tax_id string , 
mbr_key string , 
rx_filled_dt timestamp ,
gl_post_dt timestamp , 
admt_dt timestamp , 
last_updt_dtm timestamp,
adjdctn_dt timestamp)
PARTITIONED BY(clm_sor_cd string )
STORED AS PARQUET
LOCATION  '${hdfs_path}/mtclm';

CREATE EXTERNAL TABLE IF NOT EXISTS mtclm_coa(
clm_adjstmnt_key string , 
clm_line_nbr string , 
inpat_cd string , 
mbu_cf_cd string , 
prod_cf_cd string , 
cmpny_cf_cd string , 
clm_line_encntr_cd string , 
clm_line_stts_cd string , 
hlth_srvc_type_cd string ,
last_updt_dtm timestamp,
adjdctn_dt timestamp)
PARTITIONED BY(clm_sor_cd string )
STORED AS PARQUET
LOCATION  '${hdfs_path}/mtclm_coa';

-----TARGETTED TIN--------

CREATE EXTERNAL TABLE IF NOT EXISTS targeted_tins(
tin String,
targeted_group_old String,
program String,
market String,
target_date String, 
targeted_group String)
PARTITIONED BY(rcrd_date_range String) 
STORED AS PARQUET
LOCATION  '${hdfs_path}/targeted_tins';

CREATE EXTERNAL TABLE IF NOT EXISTS  targeted_tins_target_date_market (
target_date String,
market String,
count_tins Int,
count_recs Int,
last_updt_dtm String)
PARTITIONED BY(rcrd_date_range String) 
STORED AS PARQUET
LOCATION  '${hdfs_path}/targeted_tins_target_date_market';

CREATE EXTERNAL TABLE IF NOT EXISTS targeted_tins_market (
market String,
count_tins Int, 
count_recs Int,
last_updt_dtm String)
PARTITIONED BY(rcrd_date_range String) 
STORED AS PARQUET
LOCATION  '${hdfs_path}/targeted_tins_market';

CREATE EXTERNAL TABLE  targeted_tins_target_date (
target_date String,
count_tins Int,
count_recs Int,
last_updt_dtm String)
PARTITIONED BY(rcrd_date_range String) 
STORED AS PARQUET
LOCATION  '${hdfs_path}/targeted_tins_target_date';

-------SPEND------

CREATE EXTERNAL TABLE IF NOT EXISTS spend_mbr_allow_s(
mcid string, 
allowed_inpatient_no_transplant string, 
allowed_transplant string, 
allowed_outpatient string, 
allowed_professional string, 
allowed_rx string, 
allowed_capitated string, 
allowed_other string, 
allowed_inpatient string, 
allowed_total string,
paid_inpatient_no_transplant string, 
paid_transplant string, 
paid_outpatient string, 
paid_professional string, 
paid_rx string, 
paid_capitated string, 
paid_other string, 
paid_inpatient string, 
paid_total string  ,
seffdt12 string, 
senddt string,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range string)
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_mbr_allow_s';

