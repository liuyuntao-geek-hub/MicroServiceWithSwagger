package com.anthem.hca.smartpcp.affinity.repo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.resultsetextractor.PCPInfoDtlsListResultSetExtractor;
import com.anthem.hca.smartpcp.constants.ProviderConstants;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.Provider;



/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		ProviderRepo is used to fetch provider information from PROVIDER INFO view for list of PCPs sent from MemberRepo.
 * 
 * @author Khushbu Jain AF65409
 */
@Service
public class ProviderRepo {

	@Value("${affinity.providerQuery}")
	private String providerQuery;

	@Autowired
	private NamedParameterJdbcTemplate jdbcNamedTemplate;

	/**
	 * @param member,pcpIdSet	The Member JSON Body and List of PCP.
	 * @return List<PCP>		List of PCP with details.
	 * 
	 * 		getPCPInfoDtlsList is used to fetch provider information from PROVIDER INFO view for list of PCPs 
	 * 		sent from MemberRepo. It only fetch details of PCPs which fall under Member Regional Network. 
	 */

	public List<Provider> getPCPInfoDtlsList(Member member, Set<String> pcpIdSet) {

		List<String> memberNtwrkId = member.getMemberNetworkId();
		List<String> memberContractCode = member.getMemberContractCode();

		Set<String> memberNtwrkIdAndContractCodeSet = new HashSet<>();
		if(memberContractCode!=null && !memberContractCode.isEmpty()){
			memberNtwrkIdAndContractCodeSet.addAll(memberContractCode);
		}
		if(memberNtwrkId!=null && !memberNtwrkId.isEmpty()){
			memberNtwrkIdAndContractCodeSet.addAll(memberNtwrkId);
		}

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(ProviderConstants.MBR_NTWRK_ID_LIST, memberNtwrkId);
		parameters.addValue(ProviderConstants.PCP_ID_LIST, pcpIdSet);

		String query = providerQuery;

		return jdbcNamedTemplate.query(query,parameters,new PCPInfoDtlsListResultSetExtractor());
	}
}