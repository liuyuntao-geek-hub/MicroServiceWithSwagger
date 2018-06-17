package com.anthem.hca.smartpcp.drools.rules;

import org.junit.BeforeClass;
import org.junit.Test;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;

public class SmartPCPRulesTest {

	private static SmartPCPRules rules;

	@BeforeClass
	public static void init() {
		rules = new SmartPCPRules();
	}

	@Test
	public void testSetInvocationOrder() throws DroolsParseException {
		rules.setInvocationOrder("A");
		assertEquals("A", rules.getInvocationOrder());

		rules.setInvocationOrder("M");
		assertEquals("M", rules.getInvocationOrder());

		rules.setInvocationOrder("AM");
		assertEquals("AM", rules.getInvocationOrder());

		rules.setInvocationOrder("MA");
		assertEquals("MA", rules.getInvocationOrder());
	}

	@Test
	public void testSetInvocationOrderThrowsException() {
		try {
			rules.setInvocationOrder(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setInvocationOrder("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setInvocationOrder("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("A/M/AM/MA"));
		}
	}

}
