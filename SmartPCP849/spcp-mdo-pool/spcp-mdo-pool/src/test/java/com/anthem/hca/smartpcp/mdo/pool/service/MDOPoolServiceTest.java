/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.constants.ResponseCodes;
import com.anthem.hca.smartpcp.mdo.pool.model.MDOPoolingRules;
import com.anthem.hca.smartpcp.mdo.pool.model.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.OutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.ProviderPoolOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.RulesInputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdo.pool.util.MDOPoolUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MDOPoolServiceTest {

	@InjectMocks
	private MDOPoolService MDOPoolService;

	@Mock
	private RestClientService restClientService;

	@Mock
	private Tracer tracer;

	@Mock
	private AsyncClientService asyncClientService;

	@Mock
	private ProviderPoolService providerPoolService;

	@Mock
	private ProviderValidationService providerValidationService;

	@Mock
	private MDOPoolUtils mdoPoolUtils;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getPoolNegative() throws JsonProcessingException {
		Member member = newMember();
		MDOPoolingRulesOutputPayload response = new MDOPoolingRulesOutputPayload();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		response.setResponseCode(700);
		response.setResponseMessage("Error encountered");
		doNothing().when(asyncClientService).insertOperationFlow(any(TransactionFlowPayload.class));
		Mockito.when(restClientService.getPoolInformation(any(RulesInputPayload.class))).thenReturn(response);
		OutputPayload output = MDOPoolService.getPool(member);
		assertEquals("Error encountered", output.getResponseMessage());
	}

	@Test
	public void getPoolNullPayloadTest() throws JsonProcessingException {

		Member member = newMember();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		MDOPoolingRulesOutputPayload response = null;
		doNothing().when(asyncClientService).insertOperationFlow(any(TransactionFlowPayload.class));
		Mockito.when(restClientService.getPoolInformation(any(RulesInputPayload.class))).thenReturn(response);
		OutputPayload output = MDOPoolService.getPool(member);
		assertEquals(600, output.getResponseCode());
	}

	@Test
	public void insertOperationFlowTest() throws JsonProcessingException {

		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		OutputPayload outputPayload = createOutputPayload();
		doNothing().when(asyncClientService).insertOperationFlow(any(TransactionFlowPayload.class));
		MDOPoolService.insertOperationFlow(outputPayload, 1);
		assertEquals("SUCCESS", outputPayload.getResponseMessage());
	}

	@Test
	public void insertOperationFlowNegativeTest() throws JsonProcessingException {

		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		OutputPayload outputPayload = createErrorOutputPayload();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		doNothing().when(asyncClientService).insertOperationFlow(any(TransactionFlowPayload.class));
		MDOPoolService.insertOperationFlow(outputPayload, 1);
		assertEquals("ERROR", outputPayload.getResponseMessage());
	}

	@Test
	public void getPoolTest() throws JsonProcessingException {
		OutputPayload outputPayload = new OutputPayload();
		OutputPayload mockedOutputPayload = createMockOutput();
		JsonNode jsonNode = createJsonNode();
		ProviderPoolOutputPayload providerPoolPayload = new ProviderPoolOutputPayload();
		providerPoolPayload.setPcps(sortedPoolList());
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
	
		MDOPoolingRulesOutputPayload rulesOutputPayload = getPoolingRules();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.doReturn(rulesOutputPayload).when(restClientService)
				.getPoolInformation(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(providerPoolPayload).when(providerPoolService).poolBuilder(Mockito.anyInt(),
				Mockito.any(Member.class));
		Mockito.doReturn(jsonNode).when(restClientService)
				.getProviderValidationRules(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(mockedOutputPayload).when(providerValidationService).getPCPValidation(
				Mockito.any(Member.class), Mockito.any(JsonNode.class), Mockito.anyListOf(PCP.class), Mockito.anyInt(),
				Mockito.anyString(),Mockito.anyBoolean());
		outputPayload = MDOPoolService.getPool(newMember());

		assertEquals(200, outputPayload.getResponseCode());
	}

	@Test
	public void getPoolInvalidPCPTest() throws JsonProcessingException {
		OutputPayload outputPayload = new OutputPayload();
		OutputPayload mockedOutputPayload = createMockOutput();
		JsonNode jsonNode = createJsonNode();
		ProviderPoolOutputPayload providerPoolPayload = new ProviderPoolOutputPayload();
		providerPoolPayload.setPcps(completePCPList());
		MDOPoolingRulesOutputPayload rulesOutputPayload = getPoolingRules();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.doReturn(rulesOutputPayload).when(restClientService)
				.getPoolInformation(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(providerPoolPayload).when(providerPoolService).poolBuilder(Mockito.anyInt(),
				Mockito.any(Member.class));
		Mockito.doReturn(jsonNode).when(restClientService)
				.getProviderValidationRules(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(mockedOutputPayload).when(providerValidationService).getPCPValidation(
				Mockito.any(Member.class), Mockito.any(JsonNode.class), Mockito.anyListOf(PCP.class), Mockito.anyInt(),
				Mockito.anyString(),Mockito.anyBoolean());
		outputPayload = MDOPoolService.getPool(newMember());

		assertEquals(200, outputPayload.getResponseCode());
	}

	@Test
	public void getDummyPCPTest() throws JsonProcessingException {
		OutputPayload outputPayload = new OutputPayload();
		OutputPayload mockedOutputPayload = createMockOutput();
		JsonNode jsonNode = createJsonNode();
		List<PCP> emptyPoolList = new ArrayList<>();
		ProviderPoolOutputPayload providerPoolPayload = new ProviderPoolOutputPayload();
		providerPoolPayload.setPcps(emptyPoolList);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		MDOPoolingRulesOutputPayload rulesOutputPayload = getPoolingRules();
		Mockito.doReturn(rulesOutputPayload).when(restClientService)
				.getPoolInformation(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(providerPoolPayload).when(providerPoolService).poolBuilder(Mockito.anyInt(),
				Mockito.any(Member.class));
		Mockito.doReturn(jsonNode).when(restClientService)
				.getProviderValidationRules(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(mockedOutputPayload).when(providerValidationService).getPCPValidation(
				Mockito.any(Member.class), Mockito.any(JsonNode.class), Mockito.anyListOf(PCP.class), Mockito.anyInt(),
				Mockito.anyString(),Mockito.anyBoolean());
		outputPayload = MDOPoolService.getPool(newMember());

		assertEquals(true, outputPayload.isDummyFlag());
		assertEquals(1, outputPayload.getPcps().size());
	}
	
	@Test
	public void getInvalidDummyPCPTest() throws JsonProcessingException {
		OutputPayload outputPayload = new OutputPayload();
		OutputPayload mockedOutputPayload = createMockOutput();
		JsonNode jsonNode = createJsonNode();
		List<PCP> emptyPoolList = new ArrayList<>();
		ProviderPoolOutputPayload providerPoolPayload = new ProviderPoolOutputPayload();
		providerPoolPayload.setPcps(emptyPoolList);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		MDOPoolingRulesOutputPayload rulesOutputPayload = getnullAsDummyId();
		Mockito.doReturn(rulesOutputPayload).when(restClientService)
				.getPoolInformation(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(providerPoolPayload).when(providerPoolService).poolBuilder(Mockito.anyInt(),
				Mockito.any(Member.class));
		Mockito.doReturn(jsonNode).when(restClientService)
				.getProviderValidationRules(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(mockedOutputPayload).when(providerValidationService).getPCPValidation(
				Mockito.any(Member.class), Mockito.any(JsonNode.class), Mockito.anyListOf(PCP.class), Mockito.anyInt(),
				Mockito.anyString(),Mockito.anyBoolean());
		outputPayload = MDOPoolService.getPool(newMember());

		assertEquals(600,outputPayload.getResponseCode());
	}
	
	@Test
	public void providerRulesNegativeTest() throws JsonProcessingException {
		OutputPayload outputPayload = new OutputPayload();
		OutputPayload mockedOutputPayload = createMockOutput();
		JsonNode jsonNode = createInvalidproviderRules();
		ProviderPoolOutputPayload providerPoolPayload = new ProviderPoolOutputPayload();
		providerPoolPayload.setPcps(sortedPoolList());
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		MDOPoolingRulesOutputPayload rulesOutputPayload = getPoolingRules();
		Mockito.doReturn(rulesOutputPayload).when(restClientService)
				.getPoolInformation(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(providerPoolPayload).when(providerPoolService).poolBuilder(Mockito.anyInt(),
				Mockito.any(Member.class));
		Mockito.doReturn(jsonNode).when(restClientService)
				.getProviderValidationRules(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(mockedOutputPayload).when(providerValidationService).getPCPValidation(
				Mockito.any(Member.class), Mockito.any(JsonNode.class), Mockito.anyListOf(PCP.class), Mockito.anyInt(),
				Mockito.anyString(),Mockito.anyBoolean());
		outputPayload = MDOPoolService.getPool(newMember());

		assertEquals(MDOPoolConstants.DUMMY_MESSAGE, outputPayload.getResponseMessage());
	}

	@Test
	public void invalidProviderRulesTest() throws JsonProcessingException {
		OutputPayload outputPayload = new OutputPayload();
		OutputPayload mockedOutputPayload = createMockOutput();
		JsonNode jsonNode = createInvalidJsonNode();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		ProviderPoolOutputPayload providerPoolPayload = new ProviderPoolOutputPayload();
		providerPoolPayload.setPcps(sortedPoolList());
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		MDOPoolingRulesOutputPayload rulesOutputPayload = getPoolingRules();
		Mockito.doReturn(rulesOutputPayload).when(restClientService)
				.getPoolInformation(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(providerPoolPayload).when(providerPoolService).poolBuilder(Mockito.anyInt(),
				Mockito.any(Member.class));
		Mockito.doReturn(jsonNode).when(restClientService)
				.getProviderValidationRules(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(mockedOutputPayload).when(providerValidationService).getPCPValidation(
				Mockito.any(Member.class), Mockito.any(JsonNode.class), Mockito.anyListOf(PCP.class), Mockito.anyInt(),
				Mockito.anyString(),Mockito.anyBoolean());
		outputPayload = MDOPoolService.getPool(newMember());

		assertEquals(MDOPoolConstants.DUMMY_MESSAGE, outputPayload.getResponseMessage());
	}

	@Test
	public void getNullProviderRulesTest() throws JsonProcessingException {
		OutputPayload outputPayload = new OutputPayload();
		OutputPayload mockedOutputPayload = createMockOutput();
		JsonNode jsonNode = createNullRules();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		ProviderPoolOutputPayload providerPoolPayload = new ProviderPoolOutputPayload();
		providerPoolPayload.setPcps(sortedPoolListForOBGYN());
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		MDOPoolingRulesOutputPayload rulesOutputPayload = getPoolingRules();
		Mockito.doReturn(rulesOutputPayload).when(restClientService)
				.getPoolInformation(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(providerPoolPayload).when(providerPoolService).poolBuilder(Mockito.anyInt(),
				Mockito.any(Member.class));
		Mockito.doReturn(jsonNode).when(restClientService)
				.getProviderValidationRules(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(mockedOutputPayload).when(providerValidationService).getPCPValidation(
				Mockito.any(Member.class), Mockito.any(JsonNode.class), Mockito.anyListOf(PCP.class), Mockito.anyInt(),
				Mockito.anyString(),Mockito.anyBoolean());
		outputPayload = MDOPoolService.getPool(newMember());

		assertEquals(MDOPoolConstants.DUMMY_MESSAGE, outputPayload.getResponseMessage());
	}

	@Test
	public void getOnlyPediatricProvidersTest() throws JsonProcessingException {
		OutputPayload outputPayload = new OutputPayload();
		OutputPayload mockedOutputPayload = createMockOutput();
		JsonNode jsonNode = createJsonNode();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		ProviderPoolOutputPayload providerPoolPayload = new ProviderPoolOutputPayload();
		providerPoolPayload.setPcps(sortedPoolList());
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		MDOPoolingRulesOutputPayload rulesOutputPayload = getPoolingRules();
		Mockito.doReturn(rulesOutputPayload).when(restClientService)
				.getPoolInformation(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(providerPoolPayload).when(providerPoolService).poolBuilder(Mockito.anyInt(),
				Mockito.any(Member.class));
		Mockito.doReturn(jsonNode).when(restClientService)
				.getProviderValidationRules(Mockito.any(RulesInputPayload.class));
		Mockito.doReturn(mockedOutputPayload).when(providerValidationService).getPCPValidation(
				Mockito.any(Member.class), Mockito.any(JsonNode.class), Mockito.anyListOf(PCP.class), Mockito.anyInt(),
				Mockito.anyString(),Mockito.anyBoolean());
		Mockito.when(mdoPoolUtils.isAgeUnder18(Mockito.anyString())).thenCallRealMethod();
		outputPayload = MDOPoolService.getPool(pediatricOnlyMember());

		assertEquals(200, outputPayload.getResponseCode());
	}

	public Member newMember() {
		Member member = new Member();
		List<String> cntrctCode = new ArrayList<>();
		List<String> netwrkId = new ArrayList<>();
		List<String> memberLangCd = new ArrayList<>();

		member.setInvocationSystem("01");
		cntrctCode.add("CC86");
		member.setMemberContractCode(cntrctCode);
		member.setMemberDob("1990-04-26");
		member.setMemberGender("M");
		memberLangCd.add("ENG");
		member.setMemberLineOfBusiness("ABD00000");
		netwrkId.add("SSINHM00");
		member.setMemberNetworkId(netwrkId);
		member.setMemberProcessingState("CT");
		member.setMemberType("N");
		member.setSystemType("O");
		member.setRollOverPcpId("1234");
		member.setMemberPregnancyIndicator("Y");
		member.setMemberEffectiveDate("1989-09-12");

		return member;
	}

	public Member pediatricOnlyMember() {
		Member member = new Member();
		List<String> cntrctCode = new ArrayList<>();
		List<String> netwrkId = new ArrayList<>();
		List<String> memberLangCd = new ArrayList<>();

		member.setInvocationSystem("01");
		cntrctCode.add("CC86");
		member.setMemberContractCode(cntrctCode);
		member.setMemberDob("2015-04-26");
		member.setMemberGender("M");
		memberLangCd.add("ENG");
		member.setMemberLineOfBusiness("ABD00000");
		netwrkId.add("SSINHM00");
		member.setMemberNetworkId(netwrkId);
		member.setMemberProcessingState("CT");
		member.setMemberType("N");
		member.setSystemType("O");
		member.setRollOverPcpId("1234");
		member.setMemberPregnancyIndicator("N");
		member.setMemberEffectiveDate("2011-09-12");

		return member;
	}

	public List<PCP> createPoolList() {

		List<PCP> pcpPool = new ArrayList<>();

		PCP pcp = new PCP();
		pcp.setProvPcpId("AD1234");
		pcp.setLatdCordntNbr(11.12334);
		pcp.setLngtdCordntNbr(23.1234);

		PCP pcp2 = new PCP();
		pcp2.setProvPcpId("AZ4456");
		pcp2.setLatdCordntNbr(11.12334);
		pcp2.setLngtdCordntNbr(23.1234);

		pcpPool.add(pcp);
		pcpPool.add(pcp2);
		return pcpPool;
	}

	public OutputPayload createOutputPayload() {
		OutputPayload outputPayload = new OutputPayload();
		outputPayload.setPcps(createPoolList());
		outputPayload.setResponseCode(200);
		outputPayload.setResponseMessage("SUCCESS");
		return outputPayload;
	}

	public OutputPayload createErrorOutputPayload() {
		OutputPayload outputPayload = new OutputPayload();
		outputPayload.setPcps(createPoolList());
		outputPayload.setResponseCode(800);
		outputPayload.setResponseMessage("ERROR");
		return outputPayload;
	}

	public MDOPoolingRulesOutputPayload getPoolingRules() {

		MDOPoolingRulesOutputPayload rulesPayload = new MDOPoolingRulesOutputPayload();
		MDOPoolingRules rules = new MDOPoolingRules();
		rules.setPoolSize(1000);
		rules.setMaxRadiusToPool(10);
		rules.setDummyProviderId("DUMCT10");
		rulesPayload.setRules(rules);
		rulesPayload.setResponseCode(ResponseCodes.SUCCESS);
		rulesPayload.setResponseMessage(MDOPoolConstants.SUCCESS);

		return rulesPayload;
	}
	
	public MDOPoolingRulesOutputPayload getnullAsDummyId() {

		MDOPoolingRulesOutputPayload rulesPayload = new MDOPoolingRulesOutputPayload();
		MDOPoolingRules rules = new MDOPoolingRules();
		rules.setPoolSize(1000);
		rules.setMaxRadiusToPool(10);
		rules.setDummyProviderId(null);
		rulesPayload.setRules(rules);
		rulesPayload.setResponseCode(ResponseCodes.SUCCESS);
		rulesPayload.setResponseMessage(MDOPoolConstants.SUCCESS);

		return rulesPayload;
	}

	public List<PCP> sortedPoolList() {
		List<PCP> poolList = createPoolList();
		List<PCP> sortedPoolList = new ArrayList<>();
		PCP pcp1 = poolList.get(0);
		PCP pcp2 = poolList.get(1);
		pcp1.setAerialDistance(1.1234);
		pcp2.setAerialDistance(2.1234);
		pcp1.setSpcltyDesc(MDOPoolConstants.PEDIATRICS_SPCLTY);
		sortedPoolList.add(pcp1);
		sortedPoolList.add(pcp2);

		return sortedPoolList;
	}

	public List<PCP> sortedPoolListForOBGYN() {
		List<PCP> poolList = createPoolList();
		List<PCP> sortedPoolList = new ArrayList<>();
		PCP pcp1 = poolList.get(0);
		PCP pcp2 = poolList.get(1);
		pcp1.setAerialDistance(1.1234);
		pcp2.setAerialDistance(2.1234);
		pcp1.setSpcltyDesc(MDOPoolConstants.OBGYN_SPCLTY);
		sortedPoolList.add(pcp1);
		sortedPoolList.add(pcp2);

		return sortedPoolList;
	}

	public JsonNode createJsonNode() {

		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode ruleNode = factory.objectNode();
		ObjectNode child = factory.objectNode();

		child.put("market", "ALL");
		child.put("lob", "Commercial");
		child.put("product", "ALL");
		child.put("assignmentType", "New");
		child.put("assignmentMethod", "Batch");
		child.put("fallbackRequired", "false");

		ruleNode.set("rules", child);
		ruleNode.put("responseCode", 200);
		ruleNode.put("responseMessage", "SUCCESS");
		System.out.println(ruleNode);

		return ruleNode;
	}

	public OutputPayload createMockOutput() {

		OutputPayload mockOutputPayload = new OutputPayload();

		mockOutputPayload.setPcps(createPoolList());
		mockOutputPayload.setResponseCode(200);
		mockOutputPayload.setResponseMessage("SUCCESS");

		return mockOutputPayload;
	}

	public JsonNode createInvalidproviderRules() {
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode ruleNode = factory.objectNode();

		ruleNode.put("responseCode", 200);
		ruleNode.put("responseMessage", "Invalid rules returned from Drools");
		System.out.println(ruleNode);

		return ruleNode;
	}

	public JsonNode createInvalidJsonNode() {
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode ruleNode = factory.objectNode();

		ruleNode.put("rules", "rules");
		ruleNode.put("responseCode", 800);
		ruleNode.put("responseMessage", "Invalid Rules");

		return ruleNode;
	}

	public JsonNode createNullRules() {
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode ruleNode = factory.objectNode();

		// ruleNode.put("rules", "null");
		ruleNode.put("responseCode", 200);
		ruleNode.put("responseMessage", MDOPoolConstants.SUCCESS);

		return ruleNode;
	}

	public List<PCP> completePCPList() {

		List<PCP> pcpPool = new ArrayList<>();

		PCP pcp1 = new PCP();

		pcp1.setProvPcpId("AD1234");
		pcp1.setSpcltyDesc("OB/GYN");
		pcp1.setAccNewPatientFlag("Y");
		pcp1.setRgnlNtwkId("A1234");
		Date termDate = new Date(9999 - 12 - 31);
		pcp1.setGrpgRltdPadrsTrmntnDt(termDate);
		pcp1.setCurntMbrCnt(11);
		pcp1.setMaxMbrCnt(111);
		pcp1.setLatdCordntNbr(11.12334);
		pcp1.setLatdCordntNbr(23.1234);

		PCP pcp2 = new PCP();
		pcp2.setProvPcpId("AZ4456");
		pcp2.setSpcltyDesc("General Medicine");
		pcp2.setAccNewPatientFlag("Y");
		pcp2.setRgnlNtwkId("A12345");
		Date termDate2 = new Date(9999 - 12 - 31);
		pcp2.setGrpgRltdPadrsTrmntnDt(termDate2);
		pcp2.setCurntMbrCnt(11);
		pcp2.setMaxMbrCnt(111);
		pcp2.setLatdCordntNbr(11.12334);
		pcp2.setLngtdCordntNbr(23.1234);

		PCP pcp3 = new PCP();
		pcp3.setProvPcpId("I9999");

		pcpPool.add(pcp1);
		pcpPool.add(pcp2);
		pcpPool.add(pcp3);

		return pcpPool;
	}

}
