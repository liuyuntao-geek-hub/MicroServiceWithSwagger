package com.anthem.hca.smartpcp.helper;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateUtilsTest {
	
	DateUtils utils;

	@Before
	public void setUp() throws Exception {
		utils = new DateUtils();
	}

	@After
	public void tearDown() throws Exception {
		utils = null;
	}

	@Test
	public void testCheckFuture1() throws ParseException {
		assertTrue(utils.checkFuture("9999-12-31"));
	}
	
	@Test
	public void testCheckFuture2() throws ParseException {
		assertFalse(utils.checkFuture("1200-12-31"));
	}

}
