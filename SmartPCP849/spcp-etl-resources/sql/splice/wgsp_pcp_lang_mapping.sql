-- language mapping file

call SYSCS_UTIL.IMPORT_DATA (
'dv_pdpspcp_xm',
'WGSP_PCP_LANG_MAPPING',
null,
'hdfs:/dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/etl/Data_files/Language_mapping.csv',
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
