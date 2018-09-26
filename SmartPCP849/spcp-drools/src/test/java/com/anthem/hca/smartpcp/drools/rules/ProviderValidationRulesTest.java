package com.anthem.hca.smartpcp.drools.rules;

import org.junit.Before;
import org.junit.Test;

import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidation;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;

public class ProviderValidationRulesTest {

	private ProviderValidationRules rules;

	@Before
	public void init() {
		rules = new ProviderValidationRules(AgendaGroup.AFFINITY_PROVIDER_VALIDATION);
	}

	@Test
	public void testSetPrimarySpecialties() throws DroolsParseException {
		rules.setPrimarySpecialties("A,B,C");
		assertArrayEquals(new String[] {"A", "B", "C"}, rules.getPrimarySpecialties());

		rules.setPrimarySpecialties("P, Q, R");
		assertArrayEquals(new String[] {"P", "Q", "R"}, rules.getPrimarySpecialties());

		rules.setPrimarySpecialties(" X , Y , Z ");
		assertArrayEquals(new String[] {"X", "Y", "Z"}, rules.getPrimarySpecialties());
	}

	@Test
	public void testSetPrimarySpecialtiesThrowsException() {
		try {
			rules.setPrimarySpecialties(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setPrimarySpecialties("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}
	}

	@Test
	public void testSetMinAgeAllowedForSpecialty() throws DroolsParseException {
		rules.setSpecialty("A");
		rules.setMinAgeAllowedForSpecialty("10");

		rules.setSpecialty("B");
		rules.setMinAgeAllowedForSpecialty(null);

		rules.setSpecialty("C");
		rules.setMinAgeAllowedForSpecialty("");

		assertEquals(10, rules.getMinAgeAllowedForSpecialty("A"));
		assertEquals(ProviderValidationRules.MIN_AGE, rules.getMinAgeAllowedForSpecialty("B"));
		assertEquals(ProviderValidationRules.MIN_AGE, rules.getMinAgeAllowedForSpecialty("C"));
		assertEquals(ProviderValidationRules.MIN_AGE, rules.getMinAgeAllowedForSpecialty("FOO"));
	}

	@Test
	public void testSetMinAgeAllowedForSpecialtyThrowsException() {
		try {
			rules.setSpecialty("A");
			rules.setMinAgeAllowedForSpecialty("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetMaxAgeAllowedForSpecialty() throws DroolsParseException {
		rules.setSpecialty("A");
		rules.setMaxAgeAllowedForSpecialty("18");

		rules.setSpecialty("B");
		rules.setMaxAgeAllowedForSpecialty(null);

		rules.setSpecialty("C");
		rules.setMaxAgeAllowedForSpecialty("");

		assertEquals(18, rules.getMaxAgeAllowedForSpecialty("A"));
		assertEquals(ProviderValidationRules.MAX_AGE, rules.getMaxAgeAllowedForSpecialty("B"));
		assertEquals(ProviderValidationRules.MAX_AGE, rules.getMaxAgeAllowedForSpecialty("C"));
		assertEquals(ProviderValidationRules.MAX_AGE, rules.getMaxAgeAllowedForSpecialty("FOO"));
	}

	@Test
	public void testSetMaxAgeAllowedForSpecialtyThrowsException() {
		try {
			rules.setSpecialty("A");
			rules.setMaxAgeAllowedForSpecialty("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetGenderForSpecialty() throws DroolsParseException {
		rules.setSpecialty("A");
		rules.setGenderForSpecialty("M");

		rules.setSpecialty("B");
		rules.setGenderForSpecialty("F");

		rules.setSpecialty("C");
		rules.setGenderForSpecialty("MALE");

		rules.setSpecialty("D");
		rules.setGenderForSpecialty("FEMALE");

		assertEquals("M", rules.getGenderForSpecialty("A"));
		assertEquals("F", rules.getGenderForSpecialty("B"));
		assertEquals("MALE", rules.getGenderForSpecialty("C"));
		assertEquals("FEMALE", rules.getGenderForSpecialty("D"));
		assertEquals("", rules.getGenderForSpecialty("FOO"));
	}

	@Test
	public void testSetGenderForSpecialtyThrowsException() {
		try {
			rules.setSpecialty("A");
			rules.setGenderForSpecialty(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setSpecialty("B");
			rules.setGenderForSpecialty("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setSpecialty("C");
			rules.setGenderForSpecialty("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("M/F/MALE/FEMALE"));
		}
	}

	@Test
	public void testSetContractEffectiveBeyond() throws DroolsParseException {
		rules.setContractEffectiveBeyond("30");
		assertEquals(30, rules.getContractEffectiveBeyond());
	}

	@Test
	public void testSetContractEffectiveBeyondThrowsException() {
		try {
			rules.setContractEffectiveBeyond(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setContractEffectiveBeyond("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setContractEffectiveBeyond("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetDrivingDistance() throws DroolsParseException {
		rules.setDrivingDistance("60");
		assertEquals(60, rules.getDrivingDistance());
	}

	@Test
	public void testSetDrivingDistanceThrowsException() {
		try {
			rules.setDrivingDistance(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setDrivingDistance("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setDrivingDistance("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetPanelCapacityPercent() throws DroolsParseException {
		rules.setPanelCapacityPercent("85");
		assertEquals(85, rules.getPanelCapacityPercent());
	}

	@Test
	public void testSetPanelCapacityPercentThrowsException() {
		try {
			rules.setPanelCapacityPercent(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setPanelCapacityPercent("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setPanelCapacityPercent("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetProviderTiers() throws DroolsParseException {
		rules.setProviderTiers("1");
		assertArrayEquals(new int[] {1}, rules.getProviderTiers());

		rules.setProviderTiers("1,2");
		assertArrayEquals(new int[] {1, 2}, rules.getProviderTiers());
	}

	@Test
	public void testSetProviderTiersThrowsException() {
		try {
			rules.setProviderTiers(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setProviderTiers("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setProviderTiers("Tier-1, Tier-2");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("Tier"));
		}
	}

	@Test
	public void testSetRolloverFlag() throws DroolsParseException {
		rules.setRolloverFlag("YES");
		assertEquals("YES", rules.getRolloverFlag());

		rules.setRolloverFlag("N");
		assertEquals("N", rules.getRolloverFlag());
	}

	@Test
	public void testSetRolloverFlagThrowsException() {
		try {
			rules.setRolloverFlag(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setRolloverFlag("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setRolloverFlag("Foo");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("Y/N/YES/NO"));
		}
	}

	@Test
	public void testSetValidationRequired() throws DroolsParseException {
		rules.setValidationRequired("CONTRACT_VALIDATION", "Y");
		assertTrue(rules.isValidationRequired(ProviderValidation.CONTRACT_VALIDATION));

		rules.setValidationRequired("DISTANCE_VALIDATION", "N");
		assertFalse(rules.isValidationRequired(ProviderValidation.DISTANCE_VALIDATION));
	
		rules.setValidationRequired("AGE_RANGE_VALIDATION", "YES");
		assertTrue(rules.isValidationRequired(ProviderValidation.AGE_RANGE_VALIDATION));

		rules.setValidationRequired("GENDER_VALIDATION", "NO");
		assertFalse(rules.isValidationRequired(ProviderValidation.GENDER_VALIDATION));
	}

	@Test
	public void testSetValidationRequiredThrowsException() {
		try {
			rules.setValidationRequired(null, "Y");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setValidationRequired("", "N");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setValidationRequired("DISTANCE_VALIDATION", null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setValidationRequired("CONTRACT_VALIDATION", "");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setValidationRequired(null, null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setValidationRequired("", "");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setValidationRequired("AGE_RANGE_VALIDATION", "FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("Y/N/YES/NO"));
		}
	}

	@Test
	public void testIsFallbackRequired() throws Exception {
		assertTrue(rules.isFallbackRequired());

		rules.setPrimarySpecialties("Foo,Bar");
		rules.setSpecialty("OB/GYN");
		rules.setMinAgeAllowedForSpecialty("18");
		rules.setMaxAgeAllowedForSpecialty("45");
		rules.setGenderForSpecialty("Female");
		rules.setProviderTiers("1,2,3");
		rules.setValidationRequired("ROLLOVER_VALIDATION", "YES");

		assertFalse(rules.isFallbackRequired());
	}

}
