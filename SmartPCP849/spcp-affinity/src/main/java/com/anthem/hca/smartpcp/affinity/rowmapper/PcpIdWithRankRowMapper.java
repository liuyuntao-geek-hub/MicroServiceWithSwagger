package com.anthem.hca.smartpcp.affinity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.anthem.hca.smartpcp.affinity.constants.MemberConstants;
import com.anthem.hca.smartpcp.affinity.model.PcpIdWithRank;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				PCPIdWithRankRowMapper is used to map a list of PCPIds with PIMS Ranking in 
 * 				sorted order from Member details sent from SMART PCP.
 * 
 * @author AF65409 
 */
public class PcpIdWithRankRowMapper implements RowMapper<PcpIdWithRank>{

	/**
	 * @param resultSet, arg1
	 * @return PcpIdWithRank
	 * 
	 *    		mapRow is used to map a list of PCPIds with PIMS Ranking in sorted order from 
	 *    		Member details sent from SMART PCP
	 * 
	 */
	public PcpIdWithRank mapRow(ResultSet resultSet, int arg1) throws SQLException {

		PcpIdWithRank pcpIdWithRank = new PcpIdWithRank();
		pcpIdWithRank.setMcid(resultSet.getString(MemberConstants.MCID_LABEL));
		pcpIdWithRank.setTin(resultSet.getString(MemberConstants.TIN_LABEL));
		pcpIdWithRank.setNpi(resultSet.getString(MemberConstants.NPI_LABEL));
		pcpIdWithRank.setPcpId(resultSet.getString(MemberConstants.PCP_ID_LABEL));
		pcpIdWithRank.setPcpRank(resultSet.getString(MemberConstants.PCP_RANK_LABEL));

		return pcpIdWithRank;
	}
}