package com.anthem.hca.smartpcp.drools.rules;

import org.junit.Before;
import org.junit.Test;

import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;

public class MDOScoringRulesTest {

	private MDOScoringRules rules;

	@Before
	public void init() {
		rules = new MDOScoringRules();
	}

	@Test
	public void testSetMDORankThrowsException() {
		try {
			rules.setMDORank(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setMDORank("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setMDORank("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetMDORankScore() throws DroolsParseException {
		rules.setMDORank("1");
		rules.setMDORankScore("50");

		rules.setMDORank("2");
		rules.setMDORankScore("30");

		assertEquals(50, rules.getMDORankScore(1));
		assertEquals(30, rules.getMDORankScore(2));
	}

	@Test
	public void testSetMDORankScoreThrowsException() {
		try {
			rules.setMDORank("1");
			rules.setMDORankScore(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setMDORank("2");
			rules.setMDORankScore("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setMDORank("3");
			rules.setMDORankScore("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetProximityThrowsException() {
		try {
			rules.setProximity(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setProximity("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setProximity("5 to 10");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("[A-B] or [C+]"));
		}

		try {
			rules.setProximity("> 50");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("[A-B] or [C+]"));
		}
	}

	@Test
	public void testSetProximityScore() throws DroolsParseException {
		rules.setProximity("0 - 10");
		rules.setProximityScore("25");

		rules.setProximity("10+");
		rules.setProximityScore("5");

		assertEquals(25, rules.getProximityScore(7.5f));
		assertEquals(25, rules.getProximityScore(10.0f));
		assertEquals(5, rules.getProximityScore(10.1f));
		assertEquals(5, rules.getProximityScore(15.9f));
	}

	@Test
	public void testSetProximityScoreThrowsException() {
		try {
			rules.setProximityScore(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setProximityScore("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setProximityScore("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetLanguageMatchThrowsException() {
		try {
			rules.setLanguageMatch(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setLanguageMatch("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setLanguageMatch("Match");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("Y/N/YES/NO"));
		}
	}

	@Test
	public void testSetLanguageMatchScore() throws DroolsParseException {
		rules.setLanguageMatch("Y");
		rules.setLanguageMatchScore("10");

		rules.setLanguageMatch("NO");
		rules.setLanguageMatchScore("0");

		assertEquals(10, rules.getLanguageMatchScore(true));
		assertEquals(0, rules.getLanguageMatchScore(false));
	}

	@Test
	public void testSetLanguageMatchScoreThrowsException() {
		try {
			rules.setLanguageMatch("YES");
			rules.setLanguageMatchScore(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setLanguageMatch("NO");
			rules.setLanguageMatchScore("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setLanguageMatch("Y");
			rules.setLanguageMatchScore("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetRestrictedAgeSpecialties() throws DroolsParseException {
		rules.setRestrictedAgeSpecialties("A,B,C");
		assertArrayEquals(new String[] {"A", "B", "C"}, rules.getRestrictedAgeSpecialties());

		rules.setRestrictedAgeSpecialties("P, Q, R");
		assertArrayEquals(new String[] {"P", "Q", "R"}, rules.getRestrictedAgeSpecialties());

		rules.setRestrictedAgeSpecialties(" X , Y , Z ");
		assertArrayEquals(new String[] {"X", "Y", "Z"}, rules.getRestrictedAgeSpecialties());
	}

	@Test
	public void testSetRestrictedAgeSpecialtiesThrowsException() {
		try {
			rules.setRestrictedAgeSpecialties(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setRestrictedAgeSpecialties("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}
	}

	@Test
	public void testSetAgeSpecialtyMatchThrowsException() {
		try {
			rules.setAgeSpecialtyMatch(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setAgeSpecialtyMatch("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setAgeSpecialtyMatch("Match");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("Y/N/YES/NO"));
		}
	}

	@Test
	public void testSetAgeSpecialtyMatchScore() throws DroolsParseException {
		rules.setRestrictedAgeSpecialties("Pediatric,Gediatric");

		rules.setAgeSpecialtyMatch("YES");
		rules.setAgeSpecialtyMatchScore("10");

		rules.setAgeSpecialtyMatch("N");
		rules.setAgeSpecialtyMatchScore("0");

		assertEquals(10, rules.getAgeSpecialtyMatchScore("Pediatric"));
		assertEquals(10, rules.getAgeSpecialtyMatchScore("gediatric"));
		assertEquals(0, rules.getAgeSpecialtyMatchScore("OB/GYN"));
	}

	@Test
	public void testSetAgeSpecialtyMatchScoreThrowsException() {
		try {
			rules.setAgeSpecialtyMatch("YES");
			rules.setAgeSpecialtyMatchScore(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setAgeSpecialtyMatch("NO");
			rules.setAgeSpecialtyMatchScore("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setAgeSpecialtyMatch("N");
			rules.setAgeSpecialtyMatchScore("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetVBAParticipationThrowsException() {
		try {
			rules.setVBAParticipation(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setVBAParticipation("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setVBAParticipation("Ok");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("Y/N/YES/NO"));
		}
	}

	@Test
	public void testSetVBAParticipationScore() throws DroolsParseException {
		rules.setVBAParticipation(" y ");
		rules.setVBAParticipationScore("20");

		rules.setVBAParticipation("n ");
		rules.setVBAParticipationScore("10");

		assertEquals(20, rules.getVBAParticipationScore(true));
		assertEquals(10, rules.getVBAParticipationScore(false));
	}

	@Test
	public void testSetVBAParticipationScoreThrowsException() {
		try {
			rules.setVBAParticipation("Yes");
			rules.setVBAParticipationScore(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setVBAParticipation("No");
			rules.setVBAParticipationScore("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setVBAParticipation("n");
			rules.setVBAParticipationScore("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetLimitedTime() throws DroolsParseException {
		rules.setLimitedTime("365");
		assertEquals(365, rules.getLimitedTime());
	}

	@Test
	public void testSetLimitedTimeThrowsException() {
		try {
			rules.setLimitedTime(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setLimitedTime("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setLimitedTime("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testSetLimitedTimeScore() throws DroolsParseException {
		rules.setLimitedTimeScore("15");
		assertEquals(15, rules.getLimitedTimeScore());
	}

	@Test
	public void testSetLimitedTimeScoreThrowsException() {
		try {
			rules.setLimitedTimeScore(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setLimitedTimeScore("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setLimitedTimeScore("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("FOO"));
		}
	}

	@Test
	public void testIsFallbackRequired() throws Exception {
		assertTrue(rules.isFallbackRequired());

		rules.setMDORank("1");
		rules.setMDORankScore("50");
		rules.setProximity("10-20");
		rules.setProximityScore("25");
		rules.setLanguageMatch("YES");
		rules.setLanguageMatchScore("10");
		rules.setRestrictedAgeSpecialties("Foo,Bar");
		rules.setAgeSpecialtyMatch("YES");
		rules.setAgeSpecialtyMatchScore("10");
		rules.setVBAParticipation("YES");
		rules.setVBAParticipationScore("10");

		assertFalse(rules.isFallbackRequired());
	}

}
