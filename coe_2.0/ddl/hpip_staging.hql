USE ${hivedb};
--------SPEND--------
CREATE  EXTERNAL TABLE IF NOT EXISTS spend_s_spend_date_d(
mcid string ,
month string ,
pmnt_type string ,
qtr_nm string ,
mbu string ,
fundcf string ,
prodcf string ,
allowed string ,
paid string ,
start string ,
`end` string,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range string)
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_s_spend_date_d';

CREATE  EXTERNAL TABLE IF NOT EXISTS spend_s_spend_date(
mcid string ,
year_mnth_nbr string ,
pmnt_type string ,
qtr_nm string ,
mbu string ,
fundcf string ,
prodcf string ,
allowed string ,
paid string ,
strt_dt string ,
end_dt string ,
drvd_dt string ,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range string)
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_s_spend_date';
        
CREATE  EXTERNAL TABLE IF NOT EXISTS spend_spend_date_sum(
pmnt_type string ,
sum_allowed double ,
sum_paid double ,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range string)
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_spend_date_sum';

CREATE  EXTERNAL TABLE IF NOT EXISTS spend_spend_spend_date(
mcid string ,
drvd_dt string ,
inpat_allowed double ,
inpat_paid double ,
output_allowed double ,
output_paid double ,
cpttn_allowed double ,
cpttn_paid double ,
rx_allowed double ,
rx_paid double ,
prof_allowed double ,
prof_paid double ,
trnsplnt_allowed double ,
trnsplnt_paid double ,
allowed_total double ,
paid_total double ,
last_updt_dtm timestamp )
PARTITIONED BY (rcrd_date_range string)
STORED AS PARQUET
LOCATION  '${hdfs_path}/spend_spend_spend_date';;
