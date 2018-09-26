/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.constants;

public class MDOPoolConstants {

	private MDOPoolConstants() {

	}

	public static final Integer ZERO = 0;
	public static final String EMPTY = "";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String ERROR = "ERROR";
	public static final String SERVICE_DOWN = "Service is temporarily down. Please try after some time";

	public static final String SERVICE_NAME = "SPCP-MDO-POOL";
	public static final String TARGET_TABLE = "PROVIDER_INFO";

	public static final String RESPONSE_CODE = "responseCode";
	public static final String RESPONSE_MESSAGE = "responseMessage";
	public static final String RULES = "rules";
	public static final String MEMBER = "member";
	public static final String PEDIATRICS_SPCLTY = "Pediatric";
	public static final String OBGYN_SPCLTY = "OBGYN";

	public static final String PCP_INFO = "pcpInfo";

	public static final String INVALID_RULES = "Invalid rules returned from Drools";

	public static final String INVALID_VALIDATION = "Invalid output from Provider Validation";
	public static final String POOLBUILDER_DUMMY_MESSAGE = "No valid PCPs returned from Pool builder, Forming Dummy PCP";
	public static final String PROVIDER_DUMMY_MESSAGE = "No Valid PCPs returned from provider validation, Forming Dummy PCP";
	public static final String INVALID_DUMMY_MESSAGE = "Invalid Dummy ID returned from Drools";
}
