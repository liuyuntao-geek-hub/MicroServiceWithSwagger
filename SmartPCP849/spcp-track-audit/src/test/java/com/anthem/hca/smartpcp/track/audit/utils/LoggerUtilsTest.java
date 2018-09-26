package com.anthem.hca.smartpcp.track.audit.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class LoggerUtilsTest {
	private static final String CLEAN_MESSAGE = "ABCDEFHI";
	
	private static final String DIRTY_MESSAGE = "AB" + LoggerUtils.NEWLINE +
			"CD" + LoggerUtils.CARRIAGE_RETURN +
			"EF" + LoggerUtils.NEWLINE +
			"HI" + LoggerUtils.CARRIAGE_RETURN;
	
	private static final String CLEANED_MESSAGE = "AB" + LoggerUtils.LINE_WRAP_REPLACE +
			"CD" + LoggerUtils.LINE_WRAP_REPLACE +
			"EF" + LoggerUtils.LINE_WRAP_REPLACE +
			"HI" + LoggerUtils.LINE_WRAP_REPLACE +
			LoggerUtils.SUFFIX;
	
//	@Mock
//	private HTMLEntityCodec codec;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void cleanMessageIsUnchanged() {
		assertEquals(CLEAN_MESSAGE, LoggerUtils.cleanMessage(CLEAN_MESSAGE));
	}
	
	@Test
	public void cleanDirtyMessage() {
		assertEquals(CLEANED_MESSAGE, LoggerUtils.cleanMessage(DIRTY_MESSAGE));
	}

// LoggerUtils needs to be made instantiable to properly test this
//	@Test
//	public void codecIsCalled() {
//		EncodingAnswer encodeMethod = new EncodingAnswer();
//		
//		Mockito.when(codec.encode(LoggerUtils.IMMUNE_SLF4J_HTML, DIRTY_MESSAGE))
//			.thenAnswer(encodeMethod);
//		
//		LoggerUtils.cleanMessage(DIRTY_MESSAGE);
//		
//		assertTrue(encodeMethod.wasCalled());
//		assertEquals(LoggerUtils.IMMUNE_SLF4J_HTML, encodeMethod.getImmuneArgument());
//		assertEquals(CLEANED_MESSAGE, encodeMethod.getMessageArgument());
//	}
	
	@Test(expected = IllegalStateException.class)
	public void constructorThrowsException() {
		new LoggerUtils();
	}
}
