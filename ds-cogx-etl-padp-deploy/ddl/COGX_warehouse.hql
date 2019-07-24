use pr_ccpcogxph_gbd_r000_wh;

DROP TABLE IF EXISTS cogx_etl_audit purge;

CREATE TABLE IF NOT EXISTS cogx_etl_audit (
program String, 
user_id String, 
app_id String, 
start_time String, 
app_duration String, 
status String, 
loaded_row_count BIGINT,
LastUpdate timestamp)
stored as parquet
location  'hdfs:///pr/hdfsdata/ve2/ccp/cogx/phi/gbd/r000/warehouse/cogx_etl_audit';