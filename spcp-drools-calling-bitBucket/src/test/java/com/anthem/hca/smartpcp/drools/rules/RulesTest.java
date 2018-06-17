package com.anthem.hca.smartpcp.drools.rules;

import org.junit.BeforeClass;
import org.junit.Test;
import com.anthem.hca.smartpcp.drools.rules.Rules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;

public class RulesTest {

	private static Rules rules;

	@BeforeClass
	public static void init() {
		rules = new Rules();
	}

	@Test
	public void testSetAssignmentType() {
		rules.setAssignmentType("N");
		assertEquals("New", rules.getAssignmentType());

		rules.setAssignmentType("n");
		assertEquals("New", rules.getAssignmentType());

		rules.setAssignmentType("R");
		assertEquals("Re-Enrolled", rules.getAssignmentType());

		rules.setAssignmentType("r");
		assertEquals("Re-Enrolled", rules.getAssignmentType());

		rules.setAssignmentType("E");
		assertEquals("Existing", rules.getAssignmentType());

		rules.setAssignmentType("e");
		assertEquals("Existing", rules.getAssignmentType());

		rules.setAssignmentType("ALL");
		assertEquals("ALL", rules.getAssignmentType());

		rules.setAssignmentType("New");
		assertEquals("New", rules.getAssignmentType());

		rules.setAssignmentType("Re-Enrolled");
		assertEquals("Re-Enrolled", rules.getAssignmentType());

		rules.setAssignmentType("Existing");
		assertEquals("Existing", rules.getAssignmentType());
	}

	@Test
	public void testSetAssignmentMethod() {
		rules.setAssignmentMethod("O");
		assertEquals("Online", rules.getAssignmentMethod());

		rules.setAssignmentMethod("o");
		assertEquals("Online", rules.getAssignmentMethod());

		rules.setAssignmentMethod("B");
		assertEquals("Batch", rules.getAssignmentMethod());

		rules.setAssignmentMethod("b");
		assertEquals("Batch", rules.getAssignmentMethod());

		rules.setAssignmentMethod("ALL");
		assertEquals("ALL", rules.getAssignmentMethod());

		rules.setAssignmentMethod("Online");
		assertEquals("Online", rules.getAssignmentMethod());

		rules.setAssignmentMethod("Batch");
		assertEquals("Batch", rules.getAssignmentMethod());
	}

	@Test
	public void testSetToActualOrFallback() throws DroolsParseException {
		rules.setToActualOrFallback("FALLBACK");
		assertTrue(rules.isFallbackRequired());

		rules.setToActualOrFallback("Actual");
		assertFalse(rules.isFallbackRequired());
	}

	@Test
	public void testSetToActualOrFallbackThrowsException() {
		try {
			rules.setToActualOrFallback(null);
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setToActualOrFallback("");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("cannot be empty"));
		}

		try {
			rules.setToActualOrFallback("FOO");
			fail("Expected an DroolsParseException to be thrown");
		} catch (DroolsParseException dpe) {
			assertThat(dpe.getMessage(), containsString("ACTUAL/FALLBACK"));
		}
	}

}
