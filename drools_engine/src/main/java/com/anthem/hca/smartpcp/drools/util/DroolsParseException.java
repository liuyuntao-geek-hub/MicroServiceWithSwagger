package com.anthem.hca.smartpcp.drools.util;

/**
 * Customized Exception for Drools Micro Service 
 *
 */
@SuppressWarnings("serial")
public class DroolsParseException  extends Exception{

	public DroolsParseException (String message) {
        super(message);
	}

}
