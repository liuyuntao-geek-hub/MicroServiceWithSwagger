package com.anthem.hca.smartpcp.drools.rules;

import org.junit.Before;
import org.junit.Test;

import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;

public class MDOPoolingRulesTest {

	private MDOPoolingRules rules;

	@Before
	public void init() {
		rules = new MDOPoolingRules(AgendaGroup.MDO_POOLING);
	}

	@Test
	public void testSetPoolSize() throws DroolsParseException {
		rules.setPoolSize("1000");
		assertEquals(1000, rules.getPoolSize());
	}

	@Test
	public void testSetPoolSizeThrowsException() {
		try {
			rules.setPoolSize(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setPoolSize("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setPoolSize("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetMaxRadiusToPool() throws DroolsParseException {
		rules.setMaxRadiusToPool("100");
		assertEquals(100, rules.getMaxRadiusToPool());
	}

	@Test
	public void testSetMaxRadiusToPoolThrowsException() {
		try {
			rules.setMaxRadiusToPool(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setMaxRadiusToPool("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setMaxRadiusToPool("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetDummyProviderId() throws DroolsParseException {
		rules.setDummyProviderId("DUMCT0");
		assertEquals("DUMCT0", rules.getDummyProviderId());
	}

	@Test
	public void testSetDummyProviderIdThrowsException() {
		try {
			rules.setDummyProviderId(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setDummyProviderId("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}
	}

	@Test
	public void testIsFallbackForMDOPooling() {
		rules.setMarket("ALL");
		rules.setLob("CT0");
		rules.setProduct("ALL");
		rules.setAssignmentType("New");
		rules.setAssignmentMethod("Online");
		rules.setFallbackForPooling();

		assertFalse(rules.isFallback(AgendaGroup.MDO_POOLING));

		rules.setMarket("ALL");
		rules.setLob("ALL");
		rules.setProduct("ALL");
		rules.setAssignmentType("ALL");
		rules.setAssignmentMethod("ALL");
		rules.setFallbackForPooling();

		assertTrue(rules.isFallback(AgendaGroup.MDO_POOLING));
	}

	@Test
	public void testIsFallbackForDummyPCP() {
		rules.setAgendaGroup(AgendaGroup.DUMMYPCP);

		rules.setMarket("ALL");
		rules.setLob("CT0");
		rules.setProduct("ALL");
		rules.setAssignmentType("ALL");
		rules.setAssignmentMethod("ALL");
		rules.setFallbackForDummy();

		assertFalse(rules.isFallback(AgendaGroup.DUMMYPCP));

		rules.setMarket("FOO");
		rules.setLob("ALL");
		rules.setProduct("BAR");
		rules.setAssignmentType("New");
		rules.setAssignmentMethod("Online");
		rules.setFallbackForDummy();

		assertTrue(rules.isFallback(AgendaGroup.DUMMYPCP));
	}

	@Test
	public void testSetFallbackForMDOPooling() {
		rules.setFallback();
		assertTrue(rules.isFallback());
	}

	@Test
	public void testSetFallbackForDummyPCP() {
		rules.setAgendaGroup(AgendaGroup.DUMMYPCP);
		rules.setFallback();
		assertNull(rules.getMarket());
		assertNull(rules.getProduct());
		assertNull(rules.getAssignmentType());
		assertNull(rules.getAssignmentMethod());
		assertTrue(rules.getLob().equals("ALL"));
	}

	@Test
	public void testIsFallbackRequiredForMDOPooling() throws Exception {
		assertTrue(rules.isFallbackRequired());

		rules.setDummyProviderId("DUMCT0");
		assertTrue(rules.isFallbackRequired());

		rules.setPoolSize("1000");
		rules.setMaxRadiusToPool("60");
		assertFalse(rules.isFallbackRequired());
	}

	@Test
	public void testIsFallbackRequiredForDummyPCP() throws Exception {
		rules.setAgendaGroup(AgendaGroup.DUMMYPCP);
		assertTrue(rules.isFallbackRequired());

		rules.setPoolSize("1000");
		rules.setMaxRadiusToPool("60");
		assertTrue(rules.isFallbackRequired());

		rules.setDummyProviderId("DUMCT0");
		assertFalse(rules.isFallbackRequired());
	}

}
