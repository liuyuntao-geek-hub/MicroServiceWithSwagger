package com.anthem.hca.smartpcp.drools.model;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import com.anthem.hca.smartpcp.drools.model.Rules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

public class RulesTest {

	private static Rules rules;

	@BeforeClass
	public static void init() {
		rules = new Rules();
		rules.setAge("Pediatrics:0:18,Geriatrics:60:");
		rules.setGender("OB/GYN:F");
	}

	@Test
	public void testAgeRule() throws DroolsParseException {
		assertEquals( 0, rules.getMinAllowedAgeForSpeciality("Pediatrics").intValue());
		assertEquals(18, rules.getMaxAllowedAgeForSpeciality("Pediatrics").intValue());
		assertEquals(60, rules.getMinAllowedAgeForSpeciality("Geriatrics").intValue());
		assertEquals(Integer.MAX_VALUE, rules.getMaxAllowedAgeForSpeciality("Geriatrics").intValue());
	}

	@Test
	public void testGenderRule() {
		assertEquals("F", rules.getGenderForSpeciality("OB/GYN"));
	}

}

