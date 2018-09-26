package com.anthem.hca.smartpcp.affinity.resultsetextractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.anthem.hca.smartpcp.constants.MemberConstants;
import com.anthem.hca.smartpcp.model.PcpIdWithRank;



/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		PCPIdWithRankListResultSetExtractor is used to map a list of PCPs and details with what we got 
 * 		from SQL query from MemberRepo for Member details sent from SMART PCP.
 * 
 * @author Khushbu Jain AF65409 
 */
public class PCPIdWithRankListResultSetExtractor implements ResultSetExtractor<List<PcpIdWithRank>>{

	/**
	 * @param resultSet				ResultSet with details like PCP,TIN,NPI,MCID and PIMS ranking.
	 * @return pcpIdWithRankList	List of PCP with details like PCP,TIN,NPI,MCID and PIMS ranking.
	 * @throws SQLException			Exception when calling Database.
	 * 
	 *    	extractData is used to map a list of PCPs and details with what we got from SQL query 
	 *    	from MemberRepo for Member details sent from SMART PCP.
	 * 
	 */
	@Override
	public List<PcpIdWithRank> extractData(ResultSet resultSet) throws SQLException {

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
	}
}