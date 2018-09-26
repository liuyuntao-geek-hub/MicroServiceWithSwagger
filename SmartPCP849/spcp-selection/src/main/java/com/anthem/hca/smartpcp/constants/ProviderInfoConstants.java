package com.anthem.hca.smartpcp.constants;



/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Contains all the column names required for fetching PCP attributes.
 * 
 * 
 * @author AF71111
 */
public class ProviderInfoConstants {

	public static final String PROV_PCP_ID_COLUMN_LABEL = "PROV_PCP_ID";
	public static final String HMO_TYPE_CD_COLUMN_LABEL = "HMO_TYPE_CD";
	public static final String RGNL_NTWK_ID_COLUMN_LABEL = "RGNL_NTWK_ID";
	public static final String FIRST_NAME_COLUMN_LABEL = "PCP_FRST_NM";
	public static final String MIDDLE_NAME_COLUMN_LABEL = "PCP_MID_NM";
	public static final String LAST_NAME_COLUMN_LABEL = "PCP_LAST_NM";

	public static final String ADRS_LINE_1_TXT_COLUMN_LABEL = "ADRS_LINE_1_TXT";
	public static final String ADRS_LINE_2_TXT_COLUMN_LABEL = "ADRS_LINE_2_TXT";
	public static final String ADRS_CITY_NM_COLUMN_LABEL = "ADRS_CITY_NM";
	public static final String ADRS_ST_CD_COLUMN_LABEL = "ADRS_ST_CD";
	public static final String ADRS_ZIP_CD_COLUMN_LABEL = "ADRS_ZIP_CD";
	public static final String ADRS_ZIP_CD_PLUS_4_COLUMN_LABEL = "ADRS_ZIP_PLUS_4_CD";
	public static final String ADRS_PHONE_NUMBER_COLUMN_LABEL = "PA_CMNCTN_TYP_VALUE";
	public static final String SPECIALTY_MNEMONIC_COLUMN_LABEL = "SPCLTY_MNEMONICS";
	public static final String PROV_PRTY_CD_COLUMN_LABEL = "PROV_PRTY_CD";
	public static final String PCP_RANKING_ID_COLUMN_LABEL = "PCP_RANKG_ID";

	public static final String CURNT_MBR_CNT_COLUMN_LABEL = "CURNT_MBR_CNT";

	// --Added Fields For Hist Table---//
	public static final String GRPG_RLTD_PADRS_EFCTV_DT_COLUMN_LABEL = "GRPG_RLTD_PADRS_EFCTV_DT";
	public static final String GRPG_RLTD_PADRS_TRMNTN_DT_COLUMN_LABEL = "GRPG_RLTD_PADRS_TRMNTN_DT";
	public static final String LATD_CORDNT_NBR_COLUMN_LABEL = "LATD_CORDNT_NBR";
	public static final String LNGTD_CORDNT_NBR_COLUMN_LABEL = "LNGTD_CORDNT_NBR";
	public static final String SPCLTY_CD_COLUMN_LABEL = "SPCLTY_CD";
	public static final String WGS_SPCLTY_CD_COLUMN_LABEL = "WGS_SPCLTY_CD";
	public static final String SPCLTY_DESC_COLUMN_LABEL = "SPCLTY_DESC";
	public static final String PRMRY_SPCLTY_IND_COLUMN_LABEL = "PRMRY_SPCLTY_IND";
	public static final String MAX_MBR_CNT_COLUMN_LABEL = "MAX_MBR_CNT";
	public static final String CP_TYPE_CD_COLUMN_LABEL = "CP_TYPE_CD";
	public static final String ACC_NEW_PATIENT_FLAG_COLUMN_LABEL = "ACC_NEW_PATIENT_FLAG";
	public static final String PCP_LANG_COLUMN_LABEL = "PCP_LANG";
	public static final String ISO_3_CODE_LABEL = "ISO_3_CD";
	public static final String VBP_FLAG_COLUMN_LABEL = "VBP_FLAG";
	public static final String TIER_LVL_COLUMN_LABEL = "TIER_LEVEL";
	public static final String NPI_COLUMN_LABEL = "NPI";
	public static final String TAX_ID_COLUMN_LABEL = "TAX_ID";
	public static final String LAST_UPDTD_DTM_COLUMN_LABEL = "LAST_UPDTD_DTM";
	public static final String ADRS_CNTY_CD_COLUMN_LABEL = "ADRS_CNTY_CD";

	private ProviderInfoConstants() {

	}

}
