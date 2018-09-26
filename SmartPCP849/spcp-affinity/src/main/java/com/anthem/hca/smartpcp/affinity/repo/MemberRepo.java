package com.anthem.hca.smartpcp.affinity.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.constants.MemberConstants;
import com.anthem.hca.smartpcp.affinity.model.PcpIdWithRank;
import com.anthem.hca.smartpcp.common.am.vo.Member;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			MemberRepo is used to fetch a list of PCPIds with PIMS Ranking in sorted order from Member details 
 * 			sent from SMART PCP.
 * 
 * @author AF65409 
 */
@Service
public class MemberRepo {

	@Value("${affinity.schema}")
	private String schema;
	@Value("${affinity.memberTable}")
	private String memberTable;

	@Autowired
	private JdbcTemplate jdbcNamedTemplate;

	/**
	 * @param member
	 * @return List<PcpIdWithRank>
	 * 
	 * 		getPCPIdWithRankList is used to fetch a list of PCPIds with PIMS Ranking in sorted order from 
	 * 		Member details sent from SMART PCP.
	 */
	
	public List<PcpIdWithRank> getPCPIdWithRankList(Member member) {

//		MapSqlParameterSource parameters = new MapSqlParameterSource();
//		parameters.addValue(MemberConstants.MBR_SQNC_NBR, member.getMemberSequenceNumber().replaceFirst("^0+(?!$)", ""));
//		parameters.addValue(MemberConstants.MBR_HCID, member.getMemberEid());
//		parameters.addValue(MemberConstants.MBR_DOB, member.getMemberDob());
//		parameters.addValue(MemberConstants.MBR_FIRST_NAME, member.getMemberFirstName());
		
		String query ="SELECT DISTINCT "+MemberConstants.MCID_LABEL+" AS MCID, "+MemberConstants.TAX_ID_LABEL+" AS TIN, "+MemberConstants.IP_NPI_LABEL+" AS NPI, "+MemberConstants.PROV_PCP_ID_LABEL+" AS PCP_ID, "+MemberConstants.PIMS_RANK_LABEL+" AS PCP_RANK FROM "+schema+"."+memberTable+" WHERE "+MemberConstants.HC_ID_LABEL+" = '"+ member.getMemberEid() 
		+"' AND "+MemberConstants.BRTH_DT_LABEL+" ='" + member.getMemberDob() +"' AND "+MemberConstants.FRST_NM_LABEL+" ='"+member.getMemberFirstName()+"' AND "
				+MemberConstants.MBR_SQNC_NBR_LABEL+" ='" + member.getMemberSequenceNumber().replaceFirst("^0+(?!$)", "") +"' ORDER BY "+MemberConstants.PIMS_RANK_LABEL+" ASC";
		
		return jdbcNamedTemplate.query(query,new ResultSetExtractor<List<PcpIdWithRank>>(){  
			@Override  
			public List<PcpIdWithRank> extractData(ResultSet resultSet) throws SQLException,  
			DataAccessException {  
				List<PcpIdWithRank> pcpIdWithRankList = new ArrayList<>();
				while(resultSet.next()) {
					PcpIdWithRank pcpIdWithRank = new PcpIdWithRank();
					pcpIdWithRank.setMcid(resultSet.getString(MemberConstants.MCID_LABEL));
					pcpIdWithRank.setTin(resultSet.getString(MemberConstants.TIN_LABEL));
					pcpIdWithRank.setNpi(resultSet.getString(MemberConstants.NPI_LABEL));
					pcpIdWithRank.setPcpId(resultSet.getString(MemberConstants.PCP_ID_LABEL));
					pcpIdWithRank.setPcpRank(resultSet.getString(MemberConstants.PCP_RANK_LABEL));
					pcpIdWithRankList.add(pcpIdWithRank);
				}
				return pcpIdWithRankList;
			}});
	}
}