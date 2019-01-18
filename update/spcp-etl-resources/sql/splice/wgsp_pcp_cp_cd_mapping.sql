-- CP_TYPE_CD

delete from pdpspcp.wgsp_pcp_cp_cd_mapping;
 
call SYSCS_UTIL.IMPORT_DATA (
'pdpspcp',
'WGSP_PCP_CP_CD_MAPPING',
null,
'hdfs:///user/srcpdpspcpbthpr/flat_files/cp_type_cd.txt',
'|',
null,
'yyyy-MM-dd HH:mm:ss.SSZ',
'yyyy-MM-dd',
null,
'25',
'hdfs:///user/srcpdpspcpbthpr/bad_records',
true,
null
);

exit;

