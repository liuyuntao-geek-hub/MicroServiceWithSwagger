package com.anthem.hca.smartpcp.drools.util;

/**
 * The ErrorMessages class is used to store all Member Payload Error Messages
 * as well as Service Down Messages.
 *
 * @author  Ibrahim Shaik (AF74173)
 * @version 1.1
 */

public class ErrorMessages {

	public static final String MISSING_INVOCATION_SYSTEM = "MISSING_INVOCATION_SYSTEM";
	public static final String INVALID_INVOCATION_SYSTEM = "INVALID_INVOCATION_SYSTEM";

	public static final String MISSING_SYSTEM_TYPE = "MISSING_SYSTEM_TYPE";
	public static final String INVALID_SYSTEM_TYPE = "INVALID_SYSTEM_TYPE";

	public static final String MISSING_REQUEST_TYPE = "MISSING_REQUEST_TYPE";
	public static final String INVALID_REQUEST_TYPE = "INVALID_REQUEST_TYPE";

	public static final String MISSING_MBR_EID = "MISSING_MBR_EID";
	public static final String INVALID_MBR_EID = "INVALID_MBR_EID";

	public static final String MISSING_MBR_TYPE = "MISSING_MBR_TYPE";
	public static final String INVALID_MBR_TYPE = "INVALID_MBR_TYPE";

	public static final String MISSING_MBR_LOB = "MISSING_LOB";
	public static final String INVALID_MBR_LOB = "INVALID_LOB";

	public static final String MISSING_MBR_PROCS_STATE = "MISSING_PROCESSING_STATE";
	public static final String INVALID_MBR_PROCS_STATE = "INVALID_PROCESSING_STATE";

	public static final String INVALID_CONTRACTCODE_OR_NETWRKID = "INVALID_CONTRACTCODE/NETWORKID";

	public static final String MISSING_ADRS = "MISSING_ADDRESS";

	public static final String MISSING_ADRS_LINE1 = "MISSING_ADDRESS_LINE_1";
	public static final String INVALID_ADRS_LINE1 = "INVALID_ADDRESS_LINE_1";

	public static final String INVALID_ADRS_LINE2 = "INVALID_ADDRESS_LINE_2";

	public static final String MISSING_CITY = "MISSING_CITY";
	public static final String INVALID_CITY = "INVALID_CITY";

	public static final String MISSING_STATE = "MISSING_STATE";
	public static final String INVALID_STATE = "INVALID_STATE";

	public static final String MISSING_ZIPCODE = "MISSING_ZIPCODE";
	public static final String INVALID_ZIPCODE = "INVALID_ZIPCODE";

	public static final String INVALID_ZIPFOUR = "INVALID_ZIPFOUR";
	public static final String INVALID_COUNTY_CODE = "INVALID_COUNTY_CODE";

	public static final String MISSING_MBR_DOB = "MISSING_MEMBER_DOB";
	public static final String INVALID_MBR_DOB = "INVALID_MEMBER_DOB";

	public static final String MISSING_LATITUDE = "MISSING_LATITUDE";
	public static final String MISSING_LONGITUDE = "MISSING_LONGITUDE";

	public static final String INVALID_MEMBER_GENDER = "INVALID_MEMBER_GENDER";

	public static final String MISSING_PRODUCT_TYPE = "MISSING_PRODUCT_TYPE";
	public static final String INVALID_PRODUCT_TYPE = "INVALID_PRODUCT_TYPE";

	public static final String INVALID_MBR_SOURCE_SYSTEM = "INVALID_SOURCE_SYSTEM";

	public static final String MISSING_MBR_PRODUCT = "MISSING_PRODUCT";
	public static final String INVALID_MBR_PRODUCT = "INVALID_PRODUCT";

	public static final String INVALID_MBR_SEQUENCE_NBR = "INVALID_MBR_SEQUENCE_NUMBER";

	public static final String INVALID_MBR_FIRST_NAME = "INVALID_MBR_FIRST_NAME";
	public static final String INVALID_MBR_MIDDLE_NAME = "INVALID_MBR_MIDDLE_NAME";
	public static final String INVALID_MBR_LAST_NAME = "INVALID_MBR_LAST_NAME";

	public static final String INVALID_ROLLOVER_PCPID = "INVALID_ROLLOVER_PCPID";
	public static final String INVALID_MBR_PREGNANCY_INDICATOR = "INVALID_PREGNANCY_INDICATOR";

	public static final String MISSING_MBR_EFFECTIVE_DATE = "MISSING_MBR_EFFECTIVE_DATE";
	public static final String INVALID_MBR_EFFECTIVE_DATE = "INVALID_MBR_EFFECTIVE_DATE";

	public static final String INVALID_MBR_GROUP_ID = "INVALID_GROUP_ID";

	public static final String INVALID_MBR_SUB_GROUP_ID = "INVALID_SUB_GROUP_ID";
	public static final String AUDIT_TX_UPDATE_FAILURE = "AUDIT_TX_UPDATE_FAILURE";
	public static final String BING_SERVICE_ERROR = "BING_SERVICE_ERROR";
	public static final String INVALID_DROOLS_RESP = "INVALID_DROOLS_RESPONSE";

	public static final String AFFINITY_DOWN = "SMARTPCP_AFFINITY_UNAVAILABLE";
	public static final String DROOLS_DOWN = "SMARTPCP_DROOLS_UNAVAILABLE";
	public static final String MDOPROC_DOWN = "SMARTPCP_MDOPROC_UNAVAILABLE";
	public static final String MDOPOOL_DOWN = "SMARTPCP_MDOPOOL_UNAVAILABLE";
	public static final String MDOSCORE_DOWN = "SMARTPCP_MDOSCORE_UNAVAILABLE";
	public static final String BING_DOWN = "SMARTPCP_BING_UNAVAILABLE";
	public static final String INVALID_VALIDATION = "INVALID_RESPONSE_AFTER_VALIDATION";
	public static final String INTERNAL_PROCS_ERR = "INTERNAL_PROCESSING_ERROR";
	public static final String INVALID_RULES_RESPONSE="INVALID_DROOLS_RESPONSE"; 

	public static final String INVALID_DROOLS_PRIMARY_SPLTY = "MISSING_DROOLS_PRIMARY_SPLTY";
	public static final String MISSING_DROOLS_SPLTY_MIN_AGE = "MISSING_DROOLS_SPLTY_MIN_AGE";
	public static final String INVALID_DROOLS_SPLTY_MIN_AGE = "INVALID_DROOLS_SPLTY_MIN_AGE";
	public static final String MISSING_DROOLS_SPLTY_MAX_AGE = "MISSING_DROOLS_SPLTY_MAX_AGE";
	public static final String INVALID_DROOLS_SPLTY_MAX_AGE = "INVALID_DROOLS_SPLTY_MAX_AGE";
	public static final String MISSING_DROOLS_SPLTY_GEN = "MISSING_DROOLS_SPLTY_GEN";
	public static final String INVALID_DROOLS_SPLTY_GEN = "INVALID_DROOLS_SPLTY_GEN";
	public static final String MISSING_DROOLS_CNTRCT_EFCTV_DAYS = "MISSING_DROOLS_CNTRCT_EFCTV_DAYS";
	public static final String INVALID_DROOLS_CNTRCT_EFCTV_DAYS = "INVALID_DROOLS_CNTRCT_EFCTV_DAYS";
	public static final String MISSING_DROOLS_DRIVING_DISTANCE = "MISSING_DROOLS_DRIVING_DISTANCE";
	public static final String MISSING_DROOLS_PANEL_CAPACITY = "MISSING_DROOLS_PANEL_CAPACITY";
	public static final String MISSING_DROOLS_VALIDATION_RULESET = "MISSING_DROOLS_VALIDATION_RULESET";
	public static final String INVALID_DROOLS_VALIDATION_RULESET = "INVALID_DROOLS_VALIDATION_RULESET";
	public static final String MISSING_DROOLS_ROLLOVER_FLAG = "MISSING_DROOLS_ROLLOVER_FLAG";
	public static final String INVALID_DROOLS_ROLLOVER_FLAG = "INVALID_DROOLS_ROLLOVER_FLAG";

	public static final String PROVIDER_VALIDATION_ERROR = "PROVIDER_VALIDATION_ERROR";
	public static final String MISSING_MEMBER_GENDER = "MISSING_MEMBER_GENDER";
	public static final String MISSING_MBR_SEQUENCE_NBR = "MISSING_MBR_SEQUENCE_NUMBER";
	public static final String MISSING_MBR_FIRST_NAME = "MISSING_MBR_FIRST_NAME";
	public static final String INVALID_PCP = "INVALID_PCP";	
	public static final String NO_PCP_IN_PROVIDER_TABLE = "NO_PCP_IN_PROVIDER_TABLE";
	public static final String NO_PCP_IN_MEMBER_TABLE = "NO_PCP_IN_MEMBER_TABLE";
	public static final String MULTIPLE_MCID_IN_MEMBER_TABLE = "MULTIPLE_MCID_IN_MEMBER_TABLE";
	public static final String NO_VALID_PCP_IDENTIFIED = "NO_VALID_PCP_IDENTIFIED";

	public static final String PCP_ASSIGNED = "PCP_ASSIGNED_TO_MBR";
	public static final String NO_PCP_ASSIGNED = "NO_PCP_ASSIGNED_TO_MBR";	

	public static final String MISSING_TRACE_ID = "MISSING_TRACE_ID";
    public static final String INVALID_TRACE_ID = "INVALID_TRACE_ID";
    public static final String MISSING_SERVICE_NAME = "MISSING_SERVICE_NAME";
    public static final String INVALID_SERVICE_NAME = "INVALID_SERVICE_NAME";

    public static final String MISSING_OPERATION_STATUS = "MISSING_OPERATION_STATUS";
    public static final String INVALID_OPERATION_STATUS = "INVALID_OPERATION_STATUS";

    public static final String INVALID_OPERATION_OUTPUT = "INVALID_OPERATION_OUTPUT";

    public static final String MISSING_RESPONSE_CODE = "MISSING_RESPONSE_CODE";
    public static final String INVALID_RESPONSE_CODE = "INVALID_RESPONSE_CODE";

    public static final String MISSING_RESPONSE_MESSAGE = "MISSING_RESPONSE_MESSAGE";
    public static final String INVALID_RESPONSE_MESSAGE = "INVALID_RESPONSE_MESSAGE";

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";

	/**
	 * Private Constructor for ErrorMessages.
	 * 
	 * @param None
	 */
	private ErrorMessages() {
		throw new IllegalStateException("Cannot instantiate ErrorMessages");
	}

}
