/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.ResultSetExtractor;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.constants.ProviderInfoConstants;

public class ProviderInfoEtlRowMapper implements ResultSetExtractor<List<PCP>> {

	@Override
	public List<PCP> extractData(ResultSet resultSet) throws SQLException {
		List<PCP> providersList = new ArrayList<>();
		PCP pcp;
		List<String> defaultLangList = null;
		while (resultSet.next()) {
			pcp = new PCP();
			pcp.setProvPcpId(resultSet.getString(ProviderInfoConstants.PROV_PCP_ID_LABEL));
			pcp.setPcpLastNm(resultSet.getString(ProviderInfoConstants.PCP_LAST_NM_LABEL));
			pcp.setPcpRankgId(resultSet.getString(ProviderInfoConstants.PCP_RANKG_ID_LABEL));
			pcp.setLatdCordntNbr(resultSet.getDouble(ProviderInfoConstants.LATD_CORDNT_NBR_LABEL));
			pcp.setLngtdCordntNbr(resultSet.getDouble(ProviderInfoConstants.LNGTD_CORDNT_NBR_LABEL));
			pcp.setRgnlNtwkId(resultSet.getString(ProviderInfoConstants.RGNL_NTWK_ID_LABEL));
			pcp.setGrpgRltdPadrsTrmntnDt(new java.util.Date(
					resultSet.getDate(ProviderInfoConstants.GRPG_RLTD_PADRS_TRMNTN_DT_LABEL).getTime()));
			pcp.setGrpgRltdPadrsEfctvDt(new java.util.Date(
					resultSet.getDate(ProviderInfoConstants.GRPG_RLTD_PADRS_EFCTV_DT_LABEL).getTime()));
			pcp.setMaxMbrCnt(resultSet.getInt(ProviderInfoConstants.MAX_MBR_CNT_LABEL));
			pcp.setCurntMbrCnt(resultSet.getInt(ProviderInfoConstants.CURNT_MBR_CNT_LABEL));
			pcp.setAccNewPatientFlag(resultSet.getString(ProviderInfoConstants.ACC_NEW_PATIENT_FLAG));
			pcp.setSpcltyDesc(resultSet.getString(ProviderInfoConstants.SPCLTY_DESC_LABEL));
			pcp.setTierLvl(resultSet.getInt(ProviderInfoConstants.TIER_LEVEL_LABEL));
			pcp.setVbpFlag(resultSet.getString(ProviderInfoConstants.VBP_FLAG_LABEL));
			if (null != resultSet.getString(ProviderInfoConstants.PCP_LANG_LABEL)
					&& !StringUtils.isBlank(resultSet.getString(ProviderInfoConstants.PCP_LANG_LABEL))) {
				pcp.setPcpLang(Arrays.asList(resultSet.getString(ProviderInfoConstants.PCP_LANG_LABEL).split(",")));
			} else {
				if (null == defaultLangList) {
					defaultLangList = new ArrayList<>();
					defaultLangList.add("ENG");
				}
				pcp.setPcpLang(defaultLangList);
			}
			providersList.add(pcp);
		}
		return providersList;
	}

}
