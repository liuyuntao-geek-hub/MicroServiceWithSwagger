/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - This java class is used for provider validation
 * 
 * 
 * @author AF69961
 */
package com.anthem.hca.smartpcp.providervalidation.vo;

public class Constant {
	private Constant() {
	}

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final Integer TIER_LEVEL_1 = 1;
	public static final String REQUEST_TYPE="TRUE";
	public static final String INVOCATION_SYSTEM="Y|YES";
	public static final String ACCEPT_NEW_PATIENT="Y";
	public static final String OPERATION_OUTPUT="Validated list of PCP's=";
	public static final String OPERATION_OUTPUT_EMPTY_PCP="No PCP has Passed all the provider validations";
	
}
