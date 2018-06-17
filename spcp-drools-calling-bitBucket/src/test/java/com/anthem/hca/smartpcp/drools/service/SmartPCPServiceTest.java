package com.anthem.hca.smartpcp.drools.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.io.RulesInputPayload;
import com.anthem.hca.smartpcp.drools.io.SmartPCPRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.service.SmartPCPService;
import com.anthem.hca.smartpcp.drools.service.DroolsService.AgendaGroup;

import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

public class SmartPCPServiceTest {

	@InjectMocks
	private SmartPCPService spService;

	@Mock
	private DroolsService drService;

	@Mock
	private DroolsRestClientService rcService;

	@Mock
	private RestTemplate restTemplate;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetRules() throws Exception {
		RulesInputPayload ipPayload = new RulesInputPayload();
		ipPayload.setMember(createMember());

		SmartPCPRules r = createSmartPCPRule(ipPayload.getMember());
		r.setFallbackRequired(false);

		doReturn(false).when(drService).isFallbackRequired(any(RulesInputPayload.class));
		doReturn(r).when(drService).fireRulesFor(any(AgendaGroup.class), any(RulesInputPayload.class), any(Boolean.class));
		doNothing().when(rcService).insertOperationFlow(any(String.class));

		SmartPCPRulesOutputPayload out = new SmartPCPRulesOutputPayload();
		out.setRules(spService.getRules(ipPayload));

		assertEquals(r.getMarket(), out.getRules().getMarket());
		assertEquals(r.getLob(), out.getRules().getLob());
		assertEquals(r.getProduct(), out.getRules().getProduct());
		assertEquals(r.getAssignmentType(), out.getRules().getAssignmentType());
		assertEquals(r.getAssignmentMethod(), out.getRules().getAssignmentMethod());
		assertFalse(out.getRules().isFallbackRequired());
	}

	@Test
	public void testGetFallbackRules() throws Exception {
		RulesInputPayload ipPayload = new RulesInputPayload();
		ipPayload.setMember(createMember());

		SmartPCPRules r = createDefaultRule();
		r.setFallbackRequired(true);

		doReturn(true).when(drService).isFallbackRequired(any(RulesInputPayload.class));
		doReturn(r).when(drService).fireRulesFor(any(AgendaGroup.class), any(RulesInputPayload.class), any(Boolean.class));
		doNothing().when(rcService).insertOperationFlow(any(String.class));

		SmartPCPRulesOutputPayload out = new SmartPCPRulesOutputPayload();
		out.setRules(spService.getRules(ipPayload));

		assertEquals(r.getMarket(), out.getRules().getMarket());
		assertEquals(r.getLob(), out.getRules().getLob());
		assertEquals(r.getProduct(), out.getRules().getProduct());
		assertEquals(r.getAssignmentType(), out.getRules().getAssignmentType());
		assertEquals(r.getAssignmentMethod(), out.getRules().getAssignmentMethod());
		assertTrue(out.getRules().isFallbackRequired());
	}

	public Member createMember() {
		Member member = new Member();
		member.setMemberProcessingState("ALL");
		member.setMemberLineOfBusiness("Commercial");
		member.setMemberISGProductGroup("ALL");
		member.setMemberType("New");
		member.setSystemType("Batch");
		return member;
	}

	public SmartPCPRules createSmartPCPRule(Member member) {
		SmartPCPRules r = new SmartPCPRules();
		r.setMarket(member.getMemberProcessingState());
		r.setLob(member.getMemberLineOfBusiness());
		r.setProduct(member.getMemberISGProductGroup());
		r.setAssignmentType(member.getMemberType());
		r.setAssignmentMethod(member.getSystemType());
		return r;
	}

	public SmartPCPRules createDefaultRule() {
		SmartPCPRules r = new SmartPCPRules();
		r.setMarket("ALL");
		r.setLob("ALL");
		r.setProduct("ALL");
		r.setAssignmentType("ALL");
		r.setAssignmentMethod("ALL");
		return r;
	}

}
