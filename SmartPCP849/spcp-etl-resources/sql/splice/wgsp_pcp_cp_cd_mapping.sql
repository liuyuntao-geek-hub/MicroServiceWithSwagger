-- CP_TYPE_CD
 
call SYSCS_UTIL.IMPORT_DATA (
'dv_pdpspcp_xm',
'WGSP_PCP_CP_CD_MAPPING',
null,
'hdfs:/dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/etl/data/CP_TYPE_CD.txt',
';',
null,
'yyyy-MM-dd HH:mm:ss.SSZ',
'yyyy-MM-dd',
null,
'25',
'/user/ad13338/splice',
true,
null
);
