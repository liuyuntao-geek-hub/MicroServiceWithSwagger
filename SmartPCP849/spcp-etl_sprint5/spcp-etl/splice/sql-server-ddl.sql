CREATE TABLE DV_PDPSPCP_XM.PROVIDER_INFO_ETL (
	PROV_PCP_ID CHAR(10),
	GRPG_RLTD_PADRS_EFCTV_DT DATE,
	GRPG_RLTD_PADRS_TRMNTN_DT DATE,
	ADRS_ZIP_CD CHAR(5),
	ADRS_ZIP_PLUS_4_CD CHAR(4),
	LATD_CORDNT_NBR DECIMAL(20,10),
	LNGTD_CORDNT_NBR DECIMAL(20,10),
	PCP_RANKG_ID CHAR(10),
	RGNL_NTWK_ID CHAR(10),
	SPCLTY_CD CHAR(10),
	WGS_SPCLTY_CD CHAR(10),
	SPCLTY_DESC CHAR(50),
	SPCLTY_MNEMONICS CHAR(20),
	PRMRY_SPCLTY_IND CHAR(1),
	MAX_MBR_CNT DECIMAL(7,2),
	CURNT_MBR_CNT DECIMAL(7,2),
	CP_TYPE_CD CHAR(10),
	ACC_NEW_PATIENT_FLAG CHAR(1),
	WGS_LANG_CD CHAR(100),
	PCP_LANG VARCHAR(100),
	VBP_FLAG CHAR(1),
	TIER_LVL VARCHAR(10),
	HMO_TYPE_CD CHAR(10),
	PCP_FRST_NM VARCHAR(50),
	PCP_MID_NM VARCHAR(20),
	PCP_LAST_NM VARCHAR(50),
	ADRS_LINE_1_TXT VARCHAR(100),
	ADRS_LINE_2_TXT VARCHAR(100),
	ADRS_CITY_NM VARCHAR(40),
	ADRS_ST_CD CHAR(2),
	PA_CMNCTN_TYP_VALUE VARCHAR(40),
	PROV_PRTY_CD CHAR(10),
	NPI CHAR(10),
	TAX_ID CHAR(10),
	LAST_UPDTD_DTM Datetime
) ;