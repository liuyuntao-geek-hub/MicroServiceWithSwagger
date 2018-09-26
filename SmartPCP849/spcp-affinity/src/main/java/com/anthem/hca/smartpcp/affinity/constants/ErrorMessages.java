package com.anthem.hca.smartpcp.affinity.constants;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ErrorMessages contains all error messages used across Affinity application. 
 * 
 * @author AF65409 
 */
public class ErrorMessages {
	
	/* 
	 * Default Constructor
	 */
	private ErrorMessages() {

	}

	public static final String MISSING_INVOCATION_SYSTEM = "MISSING_INVOCATION_SYSTEM";
	public static final String INVALID_INVOCATION_SYSTEM = "INVALID_INVOCATION_SYSTEM";

	public static final String MISSING_SYSTEM_TYPE= "MISSING_SYSTEM_TYPE";
	public static final String INVALID_SYSTEM_TYPE = "INVALID_SYSTEM_TYPE";

	public static final String MISSING_MBR_EID = "MISSING_MBR_EID";
	public static final String INVALID_MBR_EID = "INVALID_MBR_EID";

	public static final String MISSING_MBR_TYPE = "MISSING_MBR_TYPE";
	public static final String INVALID_MBR_TYPE = "INVALID_MBR_TYPE";

	public static final String MISSING_MBR_LOB = "MISSING_LOB";
	public static final String INVALID_MBR_LOB = "INVALID_LOB";

	public static final String MISSING_MBR_PROCS_STATE = "MISSING_PROCESSING_STATE";
	public static final String INVALID_MBR_PROCS_STATE = "INVALID_PROCESSING_STATE";

	public static final String MISSING_CONTRACTCODE_OR_NETWRKID = "MISSING_CONTRACTCODE/NETWORKID";

	public static final String MISSING_ADRS= "MISSING_ADDRESS";

	public static final String MISSING_LATITUDE = "MISSING_LATITUDE";
	
	public static final String MISSING_LONGITUDE = "MISSING_LONGITUDE";
	
	public static final String MISSING_DOB = "MISSING_MEMBER_DOB";
	public static final String INVALID_DOB = "INVALID_MEMBER_DOB";
	
	public static final String MISSING_GENDER = "MISSING_MEMBER_GENDER";
	public static final String INVALID_GENDER = "INVALID_MEMBER_GENDER";
	
	public static final String MISSING_MBR_SEQUENCE_NBR = "MISSING_MBR_SEQUENCE_NUMBER";
	public static final String INVALID_MBR_SEQUENCE_NBR = "INVALID_MBR_SEQUENCE_NUMBER";
	
	public static final String MISSING_MBR_FIRST_NAME = "MISSING_MBR_FIRST_NAME";
	public static final String INVALID_MBR_FIRST_NAME = "INVALID_MBR_FIRST_NAME";

	public static final String INVALID_ROLLOVER_PCPID = "INVALID_ROLLOVER_PCPID";

	public static final String MISSING_MBR_EFFECTIVE_DATE = "MISSING_MBR_EFFECTIVE_DATE";
	public static final String INVALID_MBR_EFFECTIVE_DATE = "INVALID_MBR_EFFECTIVE_DATE";
	
	public static final String MISSING_PRODUCT_TYPE = "MISSING_PRODUCT_TYPE";
	public static final String INVALID_PRODUCT_TYPE = "INVALID_PRODUCT_TYPE";

	public static final String DROOLS_DOWN = "SMARTPCP_DROOLS_UNAVAILABLE";
	public static final String BING_DOWN = "SMARTPCP_BING_UNAVAILABLE";

	public static final String AUDIT_TX_UPDATE_FAILURE = "AUDIT_TX_UPDATE_FAILURE";
	public static final String BING_SERVICE_ERROR = "BING_SERVICE_ERROR";
	public static final String INVALID_DROOLS_RESP = "INVALID_DROOLS_RESPONSE";
	public static final String INVALID_VALIDATION = "INVALID_RESPONSE_AFTER_VALIDATION";
	
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";

	public static final String NO_PCP_IN_PROVIDER_TABLE = "NO_PCP_IN_PROVIDER_TABLE";
	public static final String NO_PCP_IN_MEMBER_TABLE = "NO_PCP_IN_MEMBER_TABLE";
	public static final String MULTIPLE_MCID_IN_MEMBER_TABLE = "MULTIPLE_MCID_IN_MEMBER_TABLE";
	public static final String NO_VALID_PCP_IDENTIFIED = "NO_VALID_PCP_IDENTIFIED";
	
	public static final String PCP_ASSIGNED = "PCP_ASSIGNED_TO_MBR";
	public static final String NO_PCP_ASSIGNED = "NO_PCP_ASSIGNED_TO_MBR";

}