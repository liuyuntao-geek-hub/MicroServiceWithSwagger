-- Language
-- needs confirmation

-- Specialty codes

call SYSCS_UTIL.IMPORT_DATA (
'dv_pdpspcp_xm',
'WGSP_PCP_SPCLTY_CD_MAPPING',
null,
'hdfs:/dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/etl/Data_files/Specialty_mapping.csv',
'|',
null,
'yyyy-MM-dd HH:mm:ss.SSZ',
'yyyy-MM-dd',
null,
'25',
'/user/ad13338/splice',
true,
null
);