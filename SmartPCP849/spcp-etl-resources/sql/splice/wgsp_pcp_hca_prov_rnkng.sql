-- MDO Ranking

call SYSCS_UTIL.IMPORT_DATA (
'dv_pdpspcp_xm',
'WGSP_PCP_HCA_PROV_RNKNG',
null,
'hdfs:/dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/etl/Data_files/MDORanking_NV_test5302018_R1.csv',
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