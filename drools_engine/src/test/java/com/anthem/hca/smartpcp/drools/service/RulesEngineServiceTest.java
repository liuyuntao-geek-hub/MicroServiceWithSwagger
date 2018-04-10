package com.anthem.hca.smartpcp.drools.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.model.Rules;
import com.anthem.hca.smartpcp.drools.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.drools.model.RulesEngineOutputPayload; 

public class RulesEngineServiceTest {

	@InjectMocks
	private RulesEngineService rService;

	@Mock
    private DroolsService dService;
	
	@Mock
	private DroolsRestClientService tservice;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMarketLevelParametersRemainsSameForActual() throws Exception {
		Member m = createMember();

		RulesEngineInputPayload ipPayload = new RulesEngineInputPayload();
		ipPayload.setMember(m);

		Rules r = createBasicRule(m);

		r.setToActualOrFallback("ACTUAL");
		doReturn(r).when(dService).fireRulesForActualOrFallback(any(Rules.class));
		doReturn(r).when(dService).fireRulesFor(any(Rules.class), any(String.class));
		doNothing().when(tservice).insertOperationFlow(any(String.class));
		
		RulesEngineOutputPayload out = new RulesEngineOutputPayload();
		out.setRules(rService.getRules(ipPayload));

		assertEquals(r.getMarket(), out.getRules().getMarket());
		assertEquals(r.getLob(), out.getRules().getLob());
		assertEquals(r.getProduct(), out.getRules().getProduct());
		assertEquals(r.getAssignmentType(), out.getRules().getAssignmentType());
		assertEquals(r.getAssignmentMethod(), out.getRules().getAssignmentMethod());
	}

	@Test
	public void testMarketLevelParametersUpdatesForFallback() throws Exception {
		Member m = createMember();

		RulesEngineInputPayload ipPayload = new RulesEngineInputPayload();
		ipPayload.setMember(m);

		Rules r = createBasicRule(m);

		doReturn(r).when(dService).fireRulesForActualOrFallback(any(Rules.class));
		doReturn(r).when(dService).fireRulesFor(any(Rules.class), any(String.class));
		doNothing().when(tservice).insertOperationFlow(any(String.class));
		
		RulesEngineOutputPayload out = new RulesEngineOutputPayload();
		out.setRules(rService.getRules(ipPayload));

		assertEquals("ALL", out.getRules().getMarket());
		assertEquals("ALL", out.getRules().getLob());
		assertEquals("ALL", out.getRules().getProduct());
		assertEquals("ALL", out.getRules().getAssignmentType());
		assertEquals("ALL", out.getRules().getAssignmentMethod());		
	}

	public Member createMember() {
		Member member = new Member();
		member.setMarket("ALL");
		member.setLob("Commercial");
		member.setProduct("ALL");
		member.setAssignmentType("New");
		member.setAssignmentMethod("Batch");

		return member;
	}

	public Rules createBasicRule(Member member) {
		Rules r = new Rules();
		r.setMarket(member.getMarket());
		r.setLob(member.getLob());
		r.setProduct(member.getProduct());
		r.setAssignmentType(member.getAssignmentType());
		r.setAssignmentMethod(member.getAssignmentMethod());

		return r;
	}

}
