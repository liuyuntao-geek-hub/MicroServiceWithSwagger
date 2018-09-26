package com.anthem.hca.smartpcp.drools.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.preprocessor.RulesMatrix;
import com.anthem.hca.smartpcp.drools.preprocessor.RulesPreprocessor;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.rules.AbstractRules;
import com.anthem.hca.smartpcp.drools.io.MDOScoringRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.service.MDOScoringService;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.Map;

public class MDOScoringServiceTest {

	@InjectMocks
	private MDOScoringService msService;

	@Mock
	private RulesPreprocessor preProcessor;

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
		Member m = createMember();

		doReturn(createData()).when(preProcessor).getData();
		doNothing().when(drService).fireRules(any(AbstractRules.class), any(Boolean.class));
		doNothing().when(rcService).insertOperationFlow(any(String.class));

		MDOScoringRulesOutputPayload out = new MDOScoringRulesOutputPayload();
		out.setRules(msService.getRules(m));

		assertEquals("Commercial", out.getRules().getLob());
		assertEquals("ALL", out.getRules().getMarket());
		assertEquals("ALL", out.getRules().getProduct());
		assertEquals("ALL", out.getRules().getAssignmentType());
		assertEquals("ALL", out.getRules().getAssignmentMethod());
		assertEquals(AgendaGroup.MDO_SCORING, out.getRules().getAgendaGroup());
	}

	public Member createMember() {
		Member m = new Member();

		m.setInvocationSystem("06");
		m.setMemberProcessingState("NY");
		m.setMemberLineOfBusiness("CT0");
		m.setMemberProductType("ALL");
		m.setMemberType("N");
		m.setSystemType("O");

		return m;
	}

	public Map<String, RulesMatrix> createData() {
		Map<String, RulesMatrix> data = new HashMap<>(5);
		RulesMatrix matrix = new RulesMatrix(5);
		data.put("MDO-SCORING", matrix);
		return data;
	}

}
