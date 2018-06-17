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
import com.anthem.hca.smartpcp.drools.io.ProviderValidationRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.service.MDOProviderValidationService;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

public class MDOProviderValidationServiceTest {

	@InjectMocks
	private MDOProviderValidationService pvService;

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

		ProviderValidationRulesOutputPayload out = new ProviderValidationRulesOutputPayload();
		out.setRules(pvService.getRules(ipPayload));

		assertEquals(ipPayload.getMember().getMemberProcessingState(), out.getRules().getMarket());
		assertEquals("Commercial", out.getRules().getLob());
		assertEquals(ipPayload.getMember().getMemberISGProductGroup(), out.getRules().getProduct());
		assertEquals(ipPayload.getMember().getMemberType(), out.getRules().getAssignmentType());
		assertEquals(ipPayload.getMember().getSystemType(), out.getRules().getAssignmentMethod());
		assertEquals(AgendaGroup.MDO_PROVIDER_VALIDATION, out.getRules().getAgendaGroup());
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
