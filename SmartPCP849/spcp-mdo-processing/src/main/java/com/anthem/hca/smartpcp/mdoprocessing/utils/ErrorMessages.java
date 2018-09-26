package com.anthem.hca.smartpcp.mdoprocessing.utils;

public class ErrorMessages {

	public static final String MISSING_INVOCATION_SYSTEM = "MISSING_INVOCATION_SYSTEM";
	public static final String INVALID_INVOCATION_SYSTEM = "INVALID_INVOCATION_SYSTEM";

	public static final String MISSING_SYSTEM_TYPE = "MISSING_SYSTEM_TYPE";
	public static final String INVALID_SYSTEM_TYPE = "INVALID_SYSTEM_TYPE";

	public static final String MISSING_MBR_TYPE = "MISSING_MBR_TYPE";
	public static final String INVALID_MBR_TYPE = "INVALID_MBR_TYPE";

	public static final String MISSING_MBR_LOB = "MISSING_LOB";
	public static final String INVALID_MBR_LOB = "INVALID_LOB";

	public static final String MISSING_MBR_PROCS_STATE = "MISSING_PROCESSING_STATE";
	public static final String INVALID_MBR_PROCS_STATE = "INVALID_PROCESSING_STATE";

	public static final String INVALID_CONTRACTCODE_OR_NETWRKID = "MISSING_CONTRACTCODE/NETWORKID";

	public static final String MISSING_ADRS = "MISSING_ADDRESS";

	public static final String MISSING_MBR_DOB = "MISSING_MEMBER_DOB";
	public static final String INVALID_MBR_DOB = "INVALID_MEMBER_DOB";

	public static final String MISSING_LATITUDE = "MISSING_LATITUDE";
	public static final String MISSING_LONGITUDE = "MISSING_LONGITUDE";
	
	public static final String MISSING_MEMBER_GENDER = "MISSING_MEMBER_GENDER";
	public static final String INVALID_MEMBER_GENDER = "INVALID_MEMBER_GENDER";

	public static final String MISSING_PRODUCT_TYPE = "MISSING_PRODUCT_TYPE";
	public static final String INVALID_PRODUCT_TYPE = "INVALID_PRODUCT_TYPE";

	public static final String INVALID_MBR_SEQUENCE_NBR = "INVALID_MBR_SEQUENCE_NUMBER";

	public static final String INVALID_ROLLOVER_PCPID = "INVALID_ROLLOVER_PCPID";
	public static final String INVALID_MBR_PREGNANCY_INDICATOR = "INVALID_PREGNANCY_INDICATOR";

	public static final String MISSING_MBR_EFFECTIVE_DATE = "MISSING_MBR_EFFECTIVE_DATE";
	public static final String INVALID_MBR_EFFECTIVE_DATE = "INVALID_MBR_EFFECTIVE_DATE";
	
    public static final String DROOLS_DOWN = "SMARTPCP_DROOLS_UNAVAILABLE";
    public static final String MDOPOOL_DOWN = "SMARTPCP_MDOPOOL_UNAVAILABLE";
    public static final String MDOSCORE_DOWN = "SMARTPCP_MDOSCORE_UNAVAILABLE";
    public static final String INVALID_DROOLS_RESPONSE="INVALID_DROOLS_RESPONSE";
    public static final String INVALID_VALIDATION = "INVALID_RESPONSE_AFTER_VALIDATION";
    public static final String INTERNAL_PROCS_ERR = "INTERNAL_PROCESSING_ERROR";
    
	private ErrorMessages() {

	}
}
