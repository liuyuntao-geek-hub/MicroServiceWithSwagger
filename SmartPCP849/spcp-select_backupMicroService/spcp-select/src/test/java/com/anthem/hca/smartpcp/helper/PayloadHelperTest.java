package com.anthem.hca.smartpcp.helper;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.AffinityInputPayload;
import com.anthem.hca.smartpcp.model.MDOInputPayload;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.RulesInputPayload;

public class PayloadHelperTest {

	PayloadHelper helper;

	@Before
	public void setUp() throws Exception {
		helper = new PayloadHelper();
	}

	@After
	public void tearDown() throws Exception {

		helper = null;
	}

	@Test
	public void testCreateAffinityPayload() {
		AffinityInputPayload input = helper.createAffinityPayload(createMember());
		assertEquals("01", input.getInvocationSystem());
		assertEquals("ASDFG123", input.getMemberLineOfBusiness());
		assertEquals(42.4094389380575, input.getAddress().getLatitude(), 0.01f);
		assertEquals(-71.2310786515669, input.getAddress().getLongitude(), 0.01f);
	}

	@Test
	public void testCreateMDOPayload() {

		MDOInputPayload input = helper.createMDOPayload(createMember());
		assertEquals("01", input.getInvocationSystem());
		assertEquals("ASDFG123", input.getMemberLineOfBusiness());
		assertEquals(42.4094389380575, input.getAddress().getLatitude(), 0.01f);
		assertEquals(-71.2310786515669, input.getAddress().getLongitude(), 0.01f);
	}

	@Test
	public void testCreateDroolsPayload() {
		RulesInputPayload input = helper.createDroolsPayload(createMember());
		assertEquals("ASDFG123", input.getMemberLineOfBusiness());
	}

	public Member createMember() {

		Member member = new Member();
		member.setInvocationSystem("01");
		member.setMemberLineOfBusiness("ASDFG123");
		Address add = new Address();
		add.setLatitude(42.4094389380575);
		add.setLongitude(-71.2310786515669);
		member.setAddress(add);
		return member;
	}

}
