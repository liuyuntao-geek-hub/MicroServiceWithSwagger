package com.anthem.hca.smartpcp.affinity.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.resultsetextractor.PCPIdWithRankListResultSetExtractor;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.PcpIdWithRank;



/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		MemberRepo is used to fetch a list of PCPs, their Mcids, Tins and Npis with PIMS Ranking in ascending order 
 * 		from MBR_INFO view for Member details sent from SMART PCP.
 * 
 * @author Khushbu Jain AF65409 
 */
@Service
public class MemberRepo {

	@Value("${affinity.memberQuery}")
	private String memberQuery;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @param member				The Member JSON Body.
	 * @return List<PcpIdWithRank>	List of PCP with other details.
	 * 
	 * 		getPCPIdWithRankList is used to fetch a list of PCPs, their Mcids, Tins and Npis with PIMS Ranking in ascending order 
	 * 		for Member details sent from SMART PCP. Here we have removed leading zeroes from MemberSequenceNumber. 
	 */

	public List<PcpIdWithRank> getPCPIdWithRankList(Member member) {

		if(member.getMemberSequenceNumber().replaceFirst("^0+(?!$)", "") == null) {
			member.setMemberSequenceNumber("0");
		} else {
			member.setMemberSequenceNumber(member.getMemberSequenceNumber().replaceFirst("^0+(?!$)", ""));
		}

		String query = memberQuery;

		return jdbcTemplate.query(query, new Object[] {member.getMemberEid(),member.getMemberDob(),member.getMemberFirstName(), member.getMemberSequenceNumber()}, new PCPIdWithRankListResultSetExtractor());
	}
}