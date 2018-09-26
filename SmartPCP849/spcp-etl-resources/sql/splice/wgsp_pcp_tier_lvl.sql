-- Mock tier file

call SYSCS_UTIL.IMPORT_DATA (
'dv_pdpspcp_xm',
'WGSP_PCP_TIER_LVL',
null,
'hdfs:/dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/etl/data/849_Tiering_Mockup.csv',
'|',
null,
'yyyy-MM-dd HH:mm:ss.SSZ',
'yyyy-MM-dd',
null,
'25',
'hdfs:/dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/etl/data/badrecords/',
true,
null
);