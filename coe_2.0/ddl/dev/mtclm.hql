CREATE EXTERNAL TABLE IF NOT EXISTS dv_pdphpipph_nogbd_r000_wh.mtclm(
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
partitioned by(clm_sor_cd string )
stored as parquet
location  'hdfs://nameservice1/dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/warehouse/mtclm';

CREATE EXTERNAL TABLE IF NOT EXISTS dv_pdphpipph_nogbd_r000_wh.mtclm_coa(
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
partitioned by(clm_sor_cd string )
stored as parquet
location  'hdfs://nameservice1/dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/warehouse/mtclm_coa';