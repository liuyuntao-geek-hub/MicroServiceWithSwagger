package com.anthem.hca.smartpcp.affinity.resultsetextractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ProviderInfoConstants;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Provider;





/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description -  Row Mapper class to map the pcp values fetched from database to Java object.
 * 
 * 
 * @author AF71111
 */
public class PCPInfoDtlsListResultSetExtractor implements ResultSetExtractor<List<Provider>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.
	 * ResultSet)
	 * 
	 * @return PCP
	 * 
	 */
	@Override
	public List<Provider> extractData(ResultSet resultSet) throws SQLException {

		List<Provider> providerList = new ArrayList<>();
	
		while (resultSet.next()) {
			Provider provider = new Provider();
			provider.setProvPcpId(resultSet.getString(ProviderInfoConstants.PROV_PCP_ID_COLUMN_LABEL));
			String pcpType = resultSet.getString(ProviderInfoConstants.HMO_TYPE_CD_COLUMN_LABEL);
			if (Constants.PCP.equalsIgnoreCase(pcpType)) {
				provider.setPcpPmgIpa(Constants.PCP_TYPE_I);
			} else if (Constants.PMG.equalsIgnoreCase(pcpType)) {
				provider.setPcpPmgIpa(Constants.PCP_TYPE_P);
			}
			provider.setRgnlNtwkId(resultSet.getString(ProviderInfoConstants.RGNL_NTWK_ID_COLUMN_LABEL));
			provider.setFirstName(resultSet.getString(ProviderInfoConstants.FIRST_NAME_COLUMN_LABEL));
			provider.setMiddleName(resultSet.getString(ProviderInfoConstants.MIDDLE_NAME_COLUMN_LABEL));
			provider.setLastName(resultSet.getString(ProviderInfoConstants.LAST_NAME_COLUMN_LABEL));
			provider.setTierLvl(resultSet.getInt(ProviderInfoConstants.TIER_LVL_COLUMN_LABEL));
			provider.setGrpgRltdPadrsTrmntnDt(new java.util.Date(
					resultSet.getDate(ProviderInfoConstants.GRPG_RLTD_PADRS_TRMNTN_DT_COLUMN_LABEL).getTime()));
			provider.setGrpgRltdPadrsEfctvDt(new java.util.Date(
					resultSet.getDate(ProviderInfoConstants.GRPG_RLTD_PADRS_EFCTV_DT_COLUMN_LABEL).getTime()));
			
			String specialityDesc = resultSet.getString(ProviderInfoConstants.SPCLTY_DESC_COLUMN_LABEL);
			if(StringUtils.isNotBlank(specialityDesc)){
			provider.setSpecialityDesc(Arrays.asList(specialityDesc.split(",")));
			}
			
			provider.setMaxMbrCnt(resultSet.getInt(ProviderInfoConstants.MAX_MBR_CNT_COLUMN_LABEL));
			provider.setCurntMbrCnt(resultSet.getInt(ProviderInfoConstants.CURNT_MBR_CNT_COLUMN_LABEL));
			
			Address address = new Address();
			address.setAddressLine1(resultSet.getString(ProviderInfoConstants.ADRS_LINE_1_TXT_COLUMN_LABEL));
			address.setAddressLine2(resultSet.getString(ProviderInfoConstants.ADRS_LINE_2_TXT_COLUMN_LABEL));
			address.setCity(resultSet.getString(ProviderInfoConstants.ADRS_CITY_NM_COLUMN_LABEL));
			address.setState(resultSet.getString(ProviderInfoConstants.ADRS_ST_CD_COLUMN_LABEL));
			address.setZipCode(resultSet.getString(ProviderInfoConstants.ADRS_ZIP_CD_COLUMN_LABEL));
			address.setZipFour(resultSet.getString(ProviderInfoConstants.ADRS_ZIP_CD_PLUS_4_COLUMN_LABEL));
			address.setLatitude(resultSet.getDouble(ProviderInfoConstants.LATD_CORDNT_NBR_COLUMN_LABEL));
			address.setLongitude(resultSet.getDouble(ProviderInfoConstants.LNGTD_CORDNT_NBR_COLUMN_LABEL));
			address.setCountyCode(resultSet.getString(ProviderInfoConstants.ADRS_CNTY_CD_COLUMN_LABEL));
			provider.setPhoneNumber(resultSet.getString(ProviderInfoConstants.ADRS_PHONE_NUMBER_COLUMN_LABEL));
			provider.setAddress(address);
			String speciality = resultSet.getString(ProviderInfoConstants.SPECIALTY_MNEMONIC_COLUMN_LABEL);
			if(StringUtils.isNotBlank(speciality)){
			provider.setSpeciality(Arrays.asList(speciality.split(",")));
			}
			provider.setAcoPcpReturned(resultSet.getString(ProviderInfoConstants.PROV_PRTY_CD_COLUMN_LABEL));
			provider.setTaxId(resultSet.getString(ProviderInfoConstants.TAX_ID_COLUMN_LABEL));
			
			providerList.add(provider);
		}
		return providerList;
	}

}