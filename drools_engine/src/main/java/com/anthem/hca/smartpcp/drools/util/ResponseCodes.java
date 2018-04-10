package com.anthem.hca.smartpcp.drools.util;

public class ResponseCodes {

	public static final Integer SERVICE_NOT_AVAILABLE = 700;
	public static final Integer JSON_VALIDATION_ERROR = 600;
	public static final Integer OTHER_EXCEPTIONS = 800;
	public static final Integer SUCCESS = 200;

	/**
	 * 
	 */
	private ResponseCodes() {
	    throw new IllegalStateException("Cannot instantiate ResponseCodes");
	}

}
