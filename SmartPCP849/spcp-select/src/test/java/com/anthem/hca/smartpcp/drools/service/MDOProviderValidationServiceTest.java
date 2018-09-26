package com.anthem.hca.smartpcp.drools.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.drools.io.ProviderValidationRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.preprocessor.RulesMatrix;
import com.anthem.hca.smartpcp.drools.preprocessor.RulesPreprocessor;
import com.anthem.hca.smartpcp.drools.rules.AbstractRules;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.service.OperationAuditFlowService;

public class MDOProviderValidationServiceTest {

	@InjectMocks
	private MDOProviderValidationService pvService;

	@Mock
	private RulesPreprocessor preProcessor;

	@Mock
	private DroolsService drService;

	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private OperationAuditFlowService operationFlowService;
	
	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(pvService, "approach", 1);
	}

	@Test
	public void testGetRules() throws Exception {
		Member m = createMember();
		pvService.headers();
		doReturn(createData()).when(preProcessor).getData();
		doNothing().when(drService).fireRules(any(AbstractRules.class), any(Boolean.class));
		doNothing().when(operationFlowService).insertOperationFlowDrools(any(String.class));
		
		ProviderValidationRulesOutputPayload out = new ProviderValidationRulesOutputPayload();
		out.setRules(pvService.getRules(m));

		assertEquals(m.getInvocationSystem(), out.getRules().getInvocationSystem());
		assertEquals("Commercial", out.getRules().getLob());
		assertEquals("ALL", out.getRules().getMarket());
		assertEquals("ALL", out.getRules().getProduct());
		assertEquals("ALL", out.getRules().getAssignmentType());
		assertEquals("ALL", out.getRules().getAssignmentMethod());
		assertEquals(AgendaGroup.INVOCATION_SYSTEM_MAPPING, out.getRules().getAgendaGroup());
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
		data.put("MDO", matrix);
		return data;
	}

}
