USE ${hivedb};
--------SPEND:1.spend_mbr_monyear 2.spend_mbr_daymonyear 3.spend_pmnt_type_sum 4.spend_mbr_daymonyear_pmnt_type External tables --------
CREATE  EXTERNAL TABLE IF NOT EXISTS spend_mbr_monyear(
mcid varchar(11) ,
month varchar(11) ,
pmnt_type varchar(16) ,
qtr_nm varchar(8) ,
mbu varchar(6) ,
fundcf varchar(3) ,
prodcf varchar(5) ,
allowed decimal(20,2) ,
paid decimal(20,2) ,
start timestamp ,
`end` timestamp,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range varchar(100))
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_mbr_monyear';

CREATE  EXTERNAL TABLE IF NOT EXISTS spend_mbr_daymonyear(
mcid varchar(11) ,
pmnt_type varchar(16) ,
qtr_nm varchar(8) ,
mbu varchar(6) ,
fundcf varchar(3) ,
prodcf varchar(5) ,
allowed decimal(20,2) ,
paid decimal(20,2) ,
start timestamp ,
`end` timestamp ,
drvddos timestamp ,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range varchar(100))
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_mbr_daymonyear';
        
CREATE  EXTERNAL TABLE IF NOT EXISTS spend_pmnt_type_sum(
pmnt_type varchar(16) ,
allowed_total decimal(28,2) ,
paid_total decimal(28,2),
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range varchar(100))
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_pmnt_type_sum';

CREATE  EXTERNAL TABLE IF NOT EXISTS spend_mbr_daymonyear_pmnt_type(
mcid varchar(11) ,
drvddos timestamp ,
inpat_allowed decimal(38,2)  ,
inpat_paid decimal(38,2)  ,
outpat_allowed decimal(38,2)  ,
outpat_paid decimal(38,2)  ,
cpttn_allowed decimal(38,2)  ,
cpttn_paid decimal(38,2)  ,
rx_allowed decimal(38,2)  ,
rx_paid decimal(38,2)  ,
prof_allowed decimal(38,2)  ,
prof_paid decimal(38,2)  ,
trnsplnt_allowed decimal(38,2)  ,
trnsplnt_paid decimal(38,2)  ,
allowed_total decimal(38,2)  ,
paid_total decimal(38,2)  ,
start timestamp ,
`end` timestamp,
mbu varchar(6) ,
fundcf varchar(3) ,
prodcf varchar(5) ,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range varchar(100))
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_mbr_daymonyear_pmnt_type';

