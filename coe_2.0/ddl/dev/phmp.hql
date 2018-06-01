CREATE EXTERNAL TABLE IF NOT EXISTS dv_pdphpipph_nogbd_r000_wh.zz_phmp_mtclm(
clm_adjstmnt_key string ,
mbrshp_sor_cd string ,
clm_its_host_cd string ,
srvc_rndrg_type_cd string ,
src_prov_natl_prov_id string ,
src_billg_tax_id string ,
mbr_key string ,
rx_filled_dt timestamp,
last_updt_dtm timestamp)
partitioned by(clm_sor_cd string)
stored as parquet
location  'hdfs://nameservice1/dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/warehouse/zz_phmp_mtclm';


CREATE EXTERNAL TABLE IF NOT EXISTS dv_pdphpipph_nogbd_r000_wh.zz_phmp_customer(
source_rowID string ,
first_name string ,
last_name string ,
street string ,
state string ,
zip string ,
phone string ,
email string ,
source string ,
rowID string,
last_updt_dtm timestamp)
partitioned by(city string)
stored as parquet
location  'hdfs://nameservice1/dv/hdfsdata/ve2/pdp/hpip/phi/no_gbd/r000/warehouse/zz_phmp_customer';
