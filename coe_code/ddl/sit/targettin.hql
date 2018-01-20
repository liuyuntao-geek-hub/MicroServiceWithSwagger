CREATE EXTERNAL TABLE IF NOT EXISTS  ts_pdphpipph_nogbd_r000_wh.targeted_tins(
tin String,
targeted_group_old String,
program String,
market String,
target_date String, 
targeted_group String)
PARTITIONED BY(rcrd_date_range String) 
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY  '|' LINES TERMINATED By '\n'
LOCATION  'hdfs://nameservice1/ts/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/warehouse/targeted_tins';

CREATE EXTERNAL TABLE IF NOT EXISTS  ts_pdphpipph_nogbd_r000_wh.targeted_tins_target_date_market (
target_date String,
market String,
count_tins Int,
count_recs Int,
last_updt_dtm String)
PARTITIONED BY(rcrd_date_range String) 
ROW FORMAT DELIMITED 
FIELDS TERMINATED by '|' LINES TERMINATED by '\n'
LOCATION  'hdfs://nameservice1/ts/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/warehouse/targeted_tins_target_date_market';

CREATE EXTERNAL TABLE IF NOT EXISTS ts_pdphpipph_nogbd_r000_wh.targeted_tins_market (
market String,
count_tins Int, 
count_recs Int,
last_updt_dtm String)
PARTITIONED BY(rcrd_date_range String) 
ROW FORMAT DELIMITED 
FIELDS TERMINATED by '|' LINES TERMINATED by '\n'
LOCATION  'hdfs://nameservice1/ts/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/warehouse/targeted_tins_market';

CREATE EXTERNAL TABLE  ts_pdphpipph_nogbd_r000_wh.targeted_tins_target_date (
target_date String,
count_tins Int,
count_recs Int,
last_updt_dtm String)
PARTITIONED BY(rcrd_date_range String) 
ROW FORMAT DELIMITED 
FIELDS TERMINATED by '|' LINES TERMINATED by '\n'
LOCATION  'hdfs://nameservice1/ts/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/warehouse/targeted_tins_target_date';
