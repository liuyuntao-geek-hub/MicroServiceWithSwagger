package com.anthem.hca.smartpcp.track.audit.utils;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test utility class used to mock and verify that a codec's encode method was called.
 */
class EncodingAnswer implements Answer<Void> {
	private boolean wasCalled;
	private char[] immune;
	private String message;
	
	EncodingAnswer() {
		this.wasCalled = false;
	}
	
	/**
	 * Flips the wasCalled status and stores method arguments.
	 */
	public Void answer(InvocationOnMock invocation) {
		Object[] args = invocation.getArguments();
		this.wasCalled = true;
		this.immune = (char[])args[0];
		this.message = (String)args[1];
		return null;
	}
	
	/**
	 * @return true if the codec's encode method has been called
	 */
	boolean wasCalled() {
		return this.wasCalled;
	}
	
	/**
	 * @return The immune argument passed to the encode method when it was called
	 */
	char[] getImmuneArgument() {
		return this.immune;
	}
	
	/**
	 * @return The message argument passed to the encode method when it was called
	 */
	String getMessageArgument() {
		return this.message;
	}
}
