package com.anthem.hca.smartpcp.drools.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.rules.Rules;
import com.anthem.hca.smartpcp.drools.io.RulesInputPayload;
import com.anthem.hca.smartpcp.drools.io.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.service.MDOPoolingService;

import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

public class MDOPoolingServiceTest {

	@InjectMocks
	private MDOPoolingService mpService;

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

		doNothing().when(drService).fireRules(any(Rules.class));
		doNothing().when(rcService).insertOperationFlow(any(String.class));

		MDOPoolingRulesOutputPayload out = new MDOPoolingRulesOutputPayload();
		out.setRules(mpService.getRules(ipPayload));

		assertEquals(ipPayload.getMember().getMemberProcessingState(), out.getRules().getMarket());
		assertEquals("CT0", out.getRules().getLob());
		assertEquals(ipPayload.getMember().getMemberISGProductGroup(), out.getRules().getProduct());
		assertEquals(ipPayload.getMember().getMemberType(), out.getRules().getAssignmentType());
		assertEquals(ipPayload.getMember().getSystemType(), out.getRules().getAssignmentMethod());
		assertEquals(AgendaGroup.DUMMYPCP, out.getRules().getAgendaGroup());
		assertFalse(out.getRules().isFallback(AgendaGroup.MDO_POOLING));
		assertFalse(out.getRules().isFallback(AgendaGroup.DUMMYPCP));
	}

	public Member createMember() {
		Member member = new Member();
		member.setMemberProcessingState("ALL");
		member.setMemberLineOfBusiness("CT0");
		member.setMemberSourceSystem("ISG");
		member.setMemberISGProductGroup("ALL");
		member.setMemberWGSGroup("ALL");
		member.setMemberType("New");
		member.setSystemType("Batch");
		return member;
	}

}
