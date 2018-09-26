package com.anthem.hca.smartpcp.track.audit.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.BeforeClass;
import org.junit.Test;

public class DateUtilityTest {
	private static DateUtility dateUtility;
	
	@BeforeClass
	public static void setupClass() {
		dateUtility = new DateUtility();
	}
	
	@Test
	public void checkFutureForEarlierDate() {
		assertFalse(dateUtility.checkFuture(earlierDate()));
	}
	
	@Test
	public void checkFutureForLaterDate() {
		assertTrue(dateUtility.checkFuture(laterDate()));
	}
	
	@Test
	public void checkSame() {
		assertTrue(dateUtility.checkAfterOrSame(earlierDate(), earlierDate()));
	}
	
	@Test
	public void checkAfter() {
		assertTrue(dateUtility.checkAfterOrSame(earlierDate(), laterDate()));
	}
	
	@Test
	public void checkBefore() {
		assertFalse(dateUtility.checkAfterOrSame(laterDate(), earlierDate()));
	}
	
	private String earlierDate() {
		return LocalDate.now().minusDays(1).toString();
	}
	
	private String laterDate() {
		return LocalDate.now().plusDays(1).toString();
	}
}
