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
import com.anthem.hca.smartpcp.constants.ProviderInfoConstants;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.Provider;

/**
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * MemberRepo is used to fetch a list of PCPs, their Mcids, Tins and Npis with
 * PIMS Ranking in ascending order from MBR_INFO view for Member details sent
 * from SMART PCP.
 * 
 * @author Khushbu Jain AF65409
 */
@Service
public class MemberRepo {

	@Value("${affinity.query}")
	private String affinityQuery;

	@Autowired
	private NamedParameterJdbcTemplate jdbcNamedTemplate;

	public List<Provider> getPCPsForAffinity(Member member) {

		if (member.getMemberSequenceNumber().replaceFirst("^0+(?!$)", "") == null) {
			member.setMemberSequenceNumber("0");
		} else {
			member.setMemberSequenceNumber(member.getMemberSequenceNumber().replaceFirst("^0+(?!$)", ""));
		}

		List<String> memberNtwrkId = member.getMemberNetworkId();
		List<String> memberContractCode = member.getMemberContractCode();

		Set<String> memberNtwrkIdAndContractCodeSet = new HashSet<>();
		if (memberContractCode != null && !memberContractCode.isEmpty()) {
			memberNtwrkIdAndContractCodeSet.addAll(memberContractCode);
		}
		if (memberNtwrkId != null && !memberNtwrkId.isEmpty()) {
			memberNtwrkIdAndContractCodeSet.addAll(memberNtwrkId);
		}

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(ProviderInfoConstants.MBR_NTWRK_ID_LIST, memberNtwrkIdAndContractCodeSet);
		parameters.addValue("mbrEid", member.getMemberEid());
		parameters.addValue("mbrDob", member.getMemberDob());
		parameters.addValue("frstNm", member.getMemberFirstName());
		parameters.addValue("mbrSequnceNm", member.getMemberSequenceNumber());
		return jdbcNamedTemplate.query(affinityQuery, parameters, new PCPInfoDtlsListResultSetExtractor());

	}
}