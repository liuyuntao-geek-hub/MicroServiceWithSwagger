package com.anthem.hca.smartpcp.track.audit.constants;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ResponseCodes contains all response codes that are populated in different scenarios across Operations Audit application. 
 * 
 * @author AF56159 
 */

public class ResponseCodes {
	
	private ResponseCodes() {
		// Preventing Object creation using new keyword
	}

	public static final Integer SERVICE_NOT_AVAILABLE = 700;
	
	public static final Integer JSON_VALIDATION_ERROR = 600;
	
	public static final Integer OTHER_EXCEPTIONS = 600;
	
	public static final Integer SUCCESS = 200;
	
}