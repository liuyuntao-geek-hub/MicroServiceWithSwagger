USE ${hivedb};

------ABC Framework: AUDIT and VARIANCE External tables------

CREATE  EXTERNAL TABLE IF NOT EXISTS hpip_variance(
tableName varchar(100), 
partitionColumn varchar(100),
partitionValue varchar(100), 
columnName varchar(100),
operationDescription varchar(100),
operation varchar(100), 
operationValue double, 
percentageVariance double,
threshold double,
isThresholdCrossed varchar(100),
subjectArea varchar(100),
last_updt_dtm timestamp 
)
STORED AS PARQUET
LOCATION  '${hdfs_path}/hpip_variance';

CREATE  EXTERNAL TABLE IF NOT EXISTS hpip_audit(
program varchar(50),
user_id varchar(50) ,
app_id varchar(50) ,
start_time timestamp,
duration varchar(10),
status varchar(20),
lst_updt_dtm timestamp)
STORED AS PARQUET
LOCATION  '${hdfs_path}/hpip_audit';

--------1.MTCLM AND 2. MTCLM_COA Managed Tables------

CREATE TABLE IF NOT EXISTS mtclm(
clm_adjstmnt_key varchar(32), 
mbrshp_sor_cd varchar(10), 
clm_its_host_cd varchar(5), 
srvc_rndrg_type_cd varchar(5), 
src_prov_natl_prov_id varchar(10), 
src_billg_tax_id varchar(32), 
mbr_key varchar(32), 
rx_filled_dt timestamp ,
gl_post_dt timestamp , 
admt_dt timestamp , 
last_updt_dtm timestamp,
adjdctn_dt timestamp)
PARTITIONED BY(clm_sor_cd varchar(10))
STORED AS PARQUET
LOCATION  '${hdfs_path}/mtclm';

CREATE TABLE IF NOT EXISTS mtclm_coa(
clm_adjstmnt_key varchar(32), 
clm_line_nbr varchar(6), 
mbu_cf_cd varchar(6),
cmpny_cf_cd varchar(5),
inpat_cd varchar(5),
last_updt_dtm timestamp)
PARTITIONED BY(prod_cf_cd varchar(5))
STORED AS PARQUET
LOCATION  '${hdfs_path}/mtclm_coa';

-----TARGETTED TIN: 1.targeted_tins,2.targeted_tins_target_date_market,--------
-----3.targeted_tins_market,4.targeted_tins_target_date External tables--------

CREATE EXTERNAL TABLE IF NOT EXISTS targeted_tins(
tin varchar(9),
targeted_group_old varchar(68),
target_date timestamp,
program varchar(3),
market varchar(2), 
targeted_group varchar(68),
last_updt_dtm timestamp)
PARTITIONED BY(run_date varchar(68)) 
STORED AS PARQUET
LOCATION  '${hdfs_path}/targeted_tins';


CREATE EXTERNAL TABLE IF NOT EXISTS  targeted_tins_target_date_market (
target_date timestamp,
market varchar(2),
count_tins Int,
count_recs Int,
last_updt_dtm timestamp)
PARTITIONED BY(run_date varchar(68)) 
STORED AS PARQUET
LOCATION  '${hdfs_path}/targeted_tins_target_date_market';

CREATE EXTERNAL TABLE IF NOT EXISTS targeted_tins_market (
market varchar(2),
count_tins Int, 
count_recs Int,
last_updt_dtm timestamp)
PARTITIONED BY(run_date varchar(68)) 
STORED AS PARQUET
LOCATION  '${hdfs_path}/targeted_tins_market';

CREATE EXTERNAL TABLE IF NOT EXISTS targeted_tins_target_date (
target_date timestamp,
count_tins Int,
count_recs Int,
last_updt_dtm timestamp)
PARTITIONED BY(run_date varchar(68)) 
STORED AS PARQUET
LOCATION  '${hdfs_path}/targeted_tins_target_date';

-------SPEND:1.spend_mbr_level External table------

CREATE EXTERNAL TABLE IF NOT EXISTS  spend_mbr_level(
member_mcid varchar(11), 
allowed_inpatient_no_transplant decimal(28,2), 
allowed_transplant decimal(28,2), 
allowed_outpatient decimal(28,2), 
allowed_professional decimal(28,2), 
allowed_rx decimal(28,2), 
allowed_capitated decimal(28,2), 
allowed_other decimal(28,2), 
allowed_inpatient decimal(28,2), 
allowed_total decimal(28,2),
paid_inpatient_no_transplant decimal(28,2), 
paid_transplant decimal(28,2), 
paid_outpatient decimal(28,2), 
paid_professional decimal(28,2), 
paid_rx decimal(28,2), 
paid_capitated decimal(28,2), 
paid_other decimal(28,2), 
paid_inpatient decimal(28,2), 
paid_total decimal(28,2)  ,
seffdt12 timestamp, 
senddt timestamp,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range varchar(100))
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_mbr_level';