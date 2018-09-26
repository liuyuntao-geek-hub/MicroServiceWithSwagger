package com.anthem.hca.smartpcp.affinity.constants;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ResponseCodes contains all response codes that are populated in different scenarios across Affinity application.
 * 
 * @author AF65409
 */

public class ResponseCodes {

	/* 
	 * Default Constructor
	 */
	private ResponseCodes() {
		throw new IllegalStateException("Utility class");
	}

	public static final String SUCCESS = "200";
	public static final String OTHER_EXCEPTIONS = "600";
	public static final String SERVICE_NOT_AVAILABLE = "700";

}
