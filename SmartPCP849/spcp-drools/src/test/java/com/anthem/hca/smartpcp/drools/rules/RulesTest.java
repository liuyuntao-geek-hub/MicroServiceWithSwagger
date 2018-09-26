package com.anthem.hca.smartpcp.drools.rules;

import org.junit.Before;
import org.junit.Test;
import com.anthem.hca.smartpcp.drools.rules.AbstractRules;
import com.anthem.hca.smartpcp.drools.model.Member;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RulesTest {

	private AbstractRules rules;

	@Before
	public void init() {
		rules = new AbstractRules() {
			@Override
			public boolean isFallbackRequired() { return false; }
		};
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
	public void testIsFallback() {
		rules.setMarketParams(createMember());
		assertFalse(rules.isFallback());

		rules.setFallback();
		assertTrue(rules.isFallback());
	}

	@Test
	public void testSetMarketParams() {
		rules.setMarketParams(createMember());
		assertEquals("ALL", rules.getMarket());
		assertEquals("Commercial", rules.getLob());
		assertEquals("HMO", rules.getProduct());
		assertEquals("New", rules.getAssignmentType());
		assertEquals("Online", rules.getAssignmentMethod());
	}

	private Member createMember() {
		Member m = new Member();

		m.setInvocationSystem("06");
		m.setMemberLineOfBusiness("CT0");
		m.setMemberProcessingState("ALL");
		m.setMemberProductType("HMO");
		m.setMemberType("N");
		m.setSystemType("O");

		return m;
	}

}
