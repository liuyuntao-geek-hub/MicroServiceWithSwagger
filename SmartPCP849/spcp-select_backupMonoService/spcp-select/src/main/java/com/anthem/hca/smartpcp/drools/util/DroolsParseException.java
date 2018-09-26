package com.anthem.hca.smartpcp.drools.util;

/**
 * The DroolsParseException class is an extension of the default Exception class.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class DroolsParseException extends Exception {

	private static final long serialVersionUID = -6148608819258429994L;

	/**
	 * Constructor to build a DroolsParseException object.
	 * 
	 * @param message The Exception Message
	 */
	public DroolsParseException (String message) {
        super(message);
	}

}
