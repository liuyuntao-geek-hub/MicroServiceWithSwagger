package com.anthem.hca.smartpcp.drools.util;

/**
 * The ResponseCodes class is used to store all Response Codes (Integer value)
 * that is contained in the Output Payload of various Rules objects.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class ResponseCodes {

	public static final Integer SERVICE_DOWN = 700;
	public static final Integer OTHER_EXCEPTIONS = 600;
	public static final Integer SUCCESS = 200;

	/**
	 * Private Constructor for ResponseCodes.
	 * 
	 * @param None
	 */
	private ResponseCodes() {
	    throw new IllegalStateException("Cannot instantiate ResponseCodes");
	}

}
