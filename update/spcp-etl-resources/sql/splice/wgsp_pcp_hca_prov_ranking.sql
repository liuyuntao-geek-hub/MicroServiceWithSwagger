-- MDO Ranking

delete from pdpspcp.hca_prov_ranking;

call SYSCS_UTIL.IMPORT_DATA (
'pdpspcp',
'HCA_PROV_RANKING',
null,
'hdfs:///user/srcpdpspcpbthpr/flat_files/mdoranking.txt',
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
