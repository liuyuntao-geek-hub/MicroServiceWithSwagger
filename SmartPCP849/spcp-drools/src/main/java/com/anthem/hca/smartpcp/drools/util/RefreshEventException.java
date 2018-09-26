package com.anthem.hca.smartpcp.drools.util;

/**
 * The RefreshEventException class is an extension of the RuntimeException class.
 * It is used to throw a Runtime Exception from the Refresh Event listener if there
 * is any problem to fetch the Rule Files from spcp-business-rules MicroService.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class RefreshEventException extends RuntimeException {

	private static final long serialVersionUID = -6921996012035128792L;

	/**
	 * Constructor to build a RefreshEventException object.
	 * 
	 * @param message The Exception Message
	 */
	public RefreshEventException (String message) {
        super(message);
	}

}
