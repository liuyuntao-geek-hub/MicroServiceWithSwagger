package com.anthem.hca.smartpcp.affinity.util;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		DateUtilsTest is used to test the DateUtils
 *  
 * @author AF65409 
 */
public class FutureDateUtilityTest {
	
	FutureDateUtility futureDateUtility;

	@Before
	public void setUp() throws Exception {
		futureDateUtility = new FutureDateUtility();
	}

	@After
	public void tearDown() throws Exception {
		futureDateUtility = null;
	}

	@Test
	public void testisFutureDate1() throws ParseException {
		assertTrue(futureDateUtility.isFutureDate("9999-12-31"));
	}
	
	@Test
	public void testisFutureDate2() throws ParseException {
		assertFalse(futureDateUtility.isFutureDate("1200-12-31"));
	}
}
