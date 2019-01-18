-- Language

delete from pdpspcp.wgsp_pcp_vbp_prgm_mapping;

call SYSCS_UTIL.IMPORT_DATA (
'pdpspcp',
'WGSP_PCP_VBP_PRGM_MAPPING',
null,
'hdfs:///user/srcpdpspcpbthpr/flat_files/vbp_program_codes.txt',
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

exit