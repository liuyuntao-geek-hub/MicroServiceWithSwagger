package com.anthem.hca.smartpcp.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ProviderInfoConstants;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.PCP;


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
public class ProviderInfoRowMapper implements ResultSetExtractor<PCP> {

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
	public PCP extractData(ResultSet resultSet) throws SQLException {

		PCP assignedPCP = null;
	
		while (resultSet.next()) {
			assignedPCP = new PCP();
			assignedPCP.setProvPcpId(resultSet.getString(ProviderInfoConstants.PROV_PCP_ID_COLUMN_LABEL));
			String pcpType = resultSet.getString(ProviderInfoConstants.HMO_TYPE_CD_COLUMN_LABEL);
			if (Constants.PCP.equalsIgnoreCase(pcpType)) {
				assignedPCP.setPcpPmgIpa(Constants.PCP_TYPE_I);
			} else if (Constants.PMG.equalsIgnoreCase(pcpType)) {
				assignedPCP.setPcpPmgIpa(Constants.PCP_TYPE_P);
			}
			String networkId = resultSet.getString(ProviderInfoConstants.RGNL_NTWK_ID_COLUMN_LABEL);
			if (networkId.length() == 4) {
				assignedPCP.setContractCode(networkId);
			} else {
				assignedPCP.setNetworkId(networkId);
			}
			assignedPCP.setPcpRank(resultSet.getInt(ProviderInfoConstants.PCP_RANKING_ID_COLUMN_LABEL));
			assignedPCP.setFirstName(resultSet.getString(ProviderInfoConstants.FIRST_NAME_COLUMN_LABEL));
			assignedPCP.setMiddleName(resultSet.getString(ProviderInfoConstants.MIDDLE_NAME_COLUMN_LABEL));
			assignedPCP.setLastName(resultSet.getString(ProviderInfoConstants.LAST_NAME_COLUMN_LABEL));
			Address address = new Address();
			address.setAddressLine1(resultSet.getString(ProviderInfoConstants.ADRS_LINE_1_TXT_COLUMN_LABEL));
			address.setAddressLine2(resultSet.getString(ProviderInfoConstants.ADRS_LINE_2_TXT_COLUMN_LABEL));
			address.setCity(resultSet.getString(ProviderInfoConstants.ADRS_CITY_NM_COLUMN_LABEL));
			address.setState(resultSet.getString(ProviderInfoConstants.ADRS_ST_CD_COLUMN_LABEL));
			address.setZipCode(resultSet.getString(ProviderInfoConstants.ADRS_ZIP_CD_COLUMN_LABEL));
			address.setZipFour(resultSet.getString(ProviderInfoConstants.ADRS_ZIP_CD_PLUS_4_COLUMN_LABEL));
			address.setLatitude(resultSet.getDouble(ProviderInfoConstants.LATD_CORDNT_NBR_COLUMN_LABEL));
			address.setLongitude(resultSet.getDouble(ProviderInfoConstants.LNGTD_CORDNT_NBR_COLUMN_LABEL));
			assignedPCP.setPhoneNumber(resultSet.getString(ProviderInfoConstants.ADRS_PHONE_NUMBER_COLUMN_LABEL));
			assignedPCP.setAddress(address);
			assignedPCP.setDummyPCP(Constants.FALSE);
			assignedPCP.setSpeciality(resultSet.getString(ProviderInfoConstants.SPECIALTY_MNEMONIC_COLUMN_LABEL));
			assignedPCP.setAcoPcpReturned(resultSet.getString(ProviderInfoConstants.PROV_PRTY_CD_COLUMN_LABEL));
			assignedPCP.setTaxId(resultSet.getString(ProviderInfoConstants.TAX_ID_COLUMN_LABEL));
		}
		return assignedPCP;
	}

}