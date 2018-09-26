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
package com.anthem.hca.smartpcp.providervalidation.utility;

@SuppressWarnings("serial")
public class WrongParameterException extends Exception {
	private final String message;

	public WrongParameterException(String msg) {
		message = msg;
	}

	@Override
	public String toString() {
		return ("MyException Occurred: " + message);
	}
}
