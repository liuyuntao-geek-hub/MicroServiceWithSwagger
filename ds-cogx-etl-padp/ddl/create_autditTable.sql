
CREATE  TABLE IF NOT EXISTS dv_ccpcogxph_nogbd_r000_wh.cogx_um_teradata_hb_audit (
program String, 
user_id String, 
app_id String, 
start_time String, 
app_duration String, 
status String, 
loaded_row_count BIGINT,
LastUpdate timestamp)

stored as parquet
location  'hdfs:///dv/hdfsdata/ve2/ccp/cogx/phi/no_gbd/r000/warehouse/cogx_um_teradata_hb_audit';