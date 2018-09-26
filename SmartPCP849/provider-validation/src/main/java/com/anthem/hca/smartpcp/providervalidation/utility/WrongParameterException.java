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
