package com.anthem.hca.smartpcp.affinity.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.constants.ProviderConstants;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ProviderRepos is used to fetch provider information from PROVIDER INFO table for 
 * 			list of PCPIds with PIMS Ranking in sorted order.
 * 
 * @author AF65409 
 */
@Service
public class ProviderRepo {

	@Value("${affinity.schema}")
	private String schema;
	@Value("${affinity.providerTable}")
	private String providerTable;

	@Autowired
	private JdbcTemplate jdbcNamedTemplate;

	/**
	 * @param member,pcpIdSet
	 * @return List<PCP>
	 * 
	 * 			getPCPInfoDtlsList is used to fetch provider information from PROVIDER INFO table for 
	 * 			list of PCPIds with PIMS Ranking in sorted order. 
	 */

	public List<PCP> getPCPInfoDtlsList(Member member, Set<String> pcpIdSet) {

		List<String> memberNtwrkId = member.getMemberNetworkId();
		List<String> memberContractCode = member.getMemberContractCode();

		Set<String> memberNtwrkIdAndContractCodeSet = new HashSet<>();
		if(memberContractCode!=null && !memberContractCode.isEmpty()){
			memberNtwrkIdAndContractCodeSet.addAll(memberContractCode);
		}
		if(memberNtwrkId!=null && !memberNtwrkId.isEmpty()){
			memberNtwrkIdAndContractCodeSet.addAll(memberNtwrkId);
		}


		String query = "SELECT "+ProviderConstants.PCP_ID_LABEL+" AS PROV_PCP_ID, " +ProviderConstants.LATD_LABEL+" AS LATD_CORDNT_NBR, " +ProviderConstants.LNGTD_LABEL+" AS LNGTD_CORDNT_NBR, " +ProviderConstants.RGNL_NTWK_ID_LABEL+" AS RGNL_NTWK_ID, " +ProviderConstants.TIER_LEVEL_LABEL+" AS TIER_LEVEL, "+ProviderConstants.SPCLTY_DESC_LABEL+"  AS SPCLTY_DESC, "+ProviderConstants.GRPG_RLTD_PADRS_TRMNTN_DT_LABEL+" AS  GRPG_RLTD_PADRS_TRMNTN_DT, "+ProviderConstants.GRPG_RLTD_PADRS_EFCTV_DT_LABEL+" AS GRPG_RLTD_PADRS_EFCTV_DT, "+ProviderConstants.MAX_MBR_CNT_LABEL+", "+ProviderConstants.CURNT_MBR_CNT_LABEL+", "+ProviderConstants.ACC_NEW_PATIENT_FLAG_LABEL+" FROM " +schema+"."+providerTable
				+" WHERE "+ProviderConstants.PCP_ID_LABEL+" IN ("+ separateByCommaForSQL(pcpIdSet.toArray()) +") AND "
				+ProviderConstants.RGNL_NTWK_ID_LABEL+" IN ("+ separateByCommaForSQL(memberNtwrkIdAndContractCodeSet.toArray()) +")";

		return jdbcNamedTemplate.query(query,new ResultSetExtractor<List<PCP>>(){  
			@Override  
			public List<PCP> extractData(ResultSet resultSet) throws SQLException {  
				List<PCP> pcpList = new ArrayList<>();
				while(resultSet.next()) {
					PCP pcp = new PCP();
					pcp.setProvPcpId(resultSet.getString(ProviderConstants.PCP_ID_LABEL));
					pcp.setLatdCordntNbr(resultSet.getDouble(ProviderConstants.LATD_LABEL));
					pcp.setLngtdCordntNbr(resultSet.getDouble(ProviderConstants.LNGTD_LABEL));
					pcp.setRgnlNtwkId(resultSet.getString(ProviderConstants.RGNL_NTWK_ID_LABEL));
					pcp.setTierLvl(resultSet.getInt(ProviderConstants.TIER_LEVEL_LABEL));
					pcp.setSpcltyDesc(resultSet.getString(ProviderConstants.SPCLTY_DESC_LABEL));
					pcp.setGrpgRltdPadrsTrmntnDt(new java.util.Date(resultSet.getDate(ProviderConstants.GRPG_RLTD_PADRS_TRMNTN_DT_LABEL).getTime()));
					pcp.setGrpgRltdPadrsEfctvDt(new java.util.Date(resultSet.getDate(ProviderConstants.GRPG_RLTD_PADRS_EFCTV_DT_LABEL).getTime()));
					pcp.setMaxMbrCnt(resultSet.getInt(ProviderConstants.MAX_MBR_CNT_LABEL));
					pcp.setCurntMbrCnt(resultSet.getInt(ProviderConstants.CURNT_MBR_CNT_LABEL));
					pcp.setAccNewPatientFlag(resultSet.getString(ProviderConstants.ACC_NEW_PATIENT_FLAG_LABEL));
					pcpList.add(pcp);
				}
				return pcpList;
			}});
	}

	private String separateByCommaForSQL(Object[] input) {

		StringBuilder sb = new StringBuilder("'"+input[0].toString()+ "'");
		int arraySize = input.length;
		for (int i = 1; i < arraySize; i++)
			sb.append(",'").append(input[i].toString()).append("'");

		return sb.toString();
	}
}