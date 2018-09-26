package com.anthem.hca.smartpcp.affinity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.anthem.hca.smartpcp.affinity.constants.ProviderConstants;
import com.anthem.hca.smartpcp.common.am.vo.PCP;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			PcpRowMapper is used to map the information got from Provider Table from Member details 
 * 			sent from SMART PCP.
 * 
 * @author AF65409 
 */
public class PcpRowMapper implements RowMapper<PCP>{

	/**
	 * @param resultSet, arg1
	 * @return PCP
	 * 
	 *    		mapRow is used to map the information got from Provider Table from Member details 
	 *    		sent from SMART PCP.
	 * 
	 */
	public PCP mapRow(ResultSet resultSet, int arg1) throws SQLException {

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

		return pcp;
	}
}
