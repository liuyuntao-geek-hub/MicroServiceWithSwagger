package com.anthem.hca.smartpcp.affinity.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.anthem.hca.smartpcp.affinity.constants.Constants;
import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.AffinityOutputPayload;
import com.anthem.hca.smartpcp.affinity.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.affinity.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.affinity.rest.RestClientService;
import com.anthem.hca.smartpcp.common.am.vo.Address;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		RestClientPayloadServiceTest is used to test the RestClientPayloadService
 *  
 * @author AF65409 
 */
public class RestClientPayloadServiceTest {

	@InjectMocks
	RestClientPayloadService restClientPayloadService;
	@Mock
	private Tracer tracer;
	@MockBean
	private Span span;
	@Mock
	private ProviderService providerService;
	@Mock
	private RestClientService restClientService;
	@Mock
	private ProviderValidationHelper pvHelper;

	@Value("${spring.application.name}")
	private String applicationName; 

	private Random random = new Random();

	@Before 
	public void setupMock() { 

		MockitoAnnotations.initMocks(this);
	}

	@Before
	public void mockSpan() {
		long id = createId();
		span = Span.builder().name("mock").traceId(id).spanId(id).build();
		doReturn(span).when(tracer).getCurrentSpan();
		doReturn(span).when(tracer).createSpan(anyString());
	}

	private long createId() {
		return random.nextLong();
	}

	@Test
	public void testInsertOperationFlowSuccess() throws JsonProcessingException {

		AffinityOutputPayload affinityOutputPayload = getFlowOprPayloadSuccess();

		TransactionFlowPayload payload = new TransactionFlowPayload();
		payload.setTraceId(tracer.getCurrentSpan().traceIdString());
		payload.setServiceName(applicationName);
		payload.setResponseCode(affinityOutputPayload.getResponseCode());
		payload.setResponseMessage(affinityOutputPayload.getResponseMessage());
		if(ResponseCodes.SUCCESS.equalsIgnoreCase(affinityOutputPayload.getResponseCode())) {
			payload.setOperationStatus(ErrorMessages.SUCCESS);
			payload.setOperationOutput(ErrorMessages.PCP_ASSIGNED + "=" + affinityOutputPayload.getPcpId());
		}

		doNothing().when(restClientService).insertOperationFlow(payload);

		restClientPayloadService.insertOperationFlow(affinityOutputPayload);
	}

	@Test
	public void testInsertOperationFlowFailure1() throws JsonProcessingException {

		AffinityOutputPayload affinityOutputPayload = getFlowOprPayloadConstraintFailure();

		TransactionFlowPayload payload = new TransactionFlowPayload();
		payload.setTraceId(tracer.getCurrentSpan().traceIdString());
		payload.setServiceName(applicationName);
		payload.setResponseCode(affinityOutputPayload.getResponseCode());
		payload.setResponseMessage(affinityOutputPayload.getResponseMessage());
		if(!ResponseCodes.SUCCESS.equalsIgnoreCase(affinityOutputPayload.getResponseCode())) {
			payload.setOperationStatus(ErrorMessages.FAILURE);
			payload.setOperationOutput(ErrorMessages.NO_PCP_ASSIGNED);
		}

		doNothing().when(restClientService).insertOperationFlow(payload);

		restClientPayloadService.insertOperationFlow(affinityOutputPayload);
	}

	@Test
	public void testInsertOperationFlowFailure2() throws JsonProcessingException {

		AffinityOutputPayload affinityOutputPayload = getFlowOprPayloadFutureDateFailure();

		TransactionFlowPayload payload = new TransactionFlowPayload();
		payload.setTraceId(tracer.getCurrentSpan().traceIdString());
		payload.setServiceName(applicationName);
		payload.setResponseCode(affinityOutputPayload.getResponseCode());
		payload.setResponseMessage(affinityOutputPayload.getResponseMessage());
		if(!ResponseCodes.SUCCESS.equalsIgnoreCase(affinityOutputPayload.getResponseCode())) {
			payload.setOperationStatus(ErrorMessages.FAILURE);
			payload.setOperationOutput(ErrorMessages.NO_PCP_ASSIGNED);
		}

		doNothing().when(restClientService).insertOperationFlow(payload);

		restClientPayloadService.insertOperationFlow(affinityOutputPayload);
	}

	@Test
	public void testInsertOperationFlowException() throws JsonProcessingException {

		AffinityOutputPayload affinityOutPayload = getFlowOprPayloadFailure();

		doThrow(new NullPointerException("Null Payload")).when(restClientService).insertOperationFlow(null);

		restClientPayloadService.insertOperationFlow(affinityOutPayload);
	}

	@Test
	public void getProviderValidRulesTest() {

		Member member = getMemberInfo();

		RulesEngineInputPayload rulesEngineInputPayload = null;
		rulesEngineInputPayload = new RulesEngineInputPayload();
		rulesEngineInputPayload.setMemberLineOfBusiness(member.getMemberLineOfBusiness());
		rulesEngineInputPayload.setMemberProcessingState(member.getMemberProcessingState());
		rulesEngineInputPayload.setMemberProductType(member.getMemberProductType());
		rulesEngineInputPayload.setMemberType(member.getMemberType());
		rulesEngineInputPayload.setSystemType(member.getSystemType());

		try {
			Mockito.when(restClientService.getProviderValidRules(rulesEngineInputPayload)).thenReturn(getDroolsOutput());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		restClientPayloadService.getProviderValidRules(member);
	}

	@Test
	public void getProviderValidRulesExceptionTest1() {

		Member member = null;

		restClientPayloadService.getProviderValidRules(member);
	}

	@Test
	public void getProviderValidRulesExceptionTest2() {
		Member member = getMemberInfo();

		RulesEngineInputPayload rulesEngineInputPayload = null;
		rulesEngineInputPayload = new RulesEngineInputPayload();
		rulesEngineInputPayload.setMemberLineOfBusiness(member.getMemberLineOfBusiness());
		rulesEngineInputPayload.setMemberProcessingState(member.getMemberProcessingState());
		rulesEngineInputPayload.setMemberProductType(member.getMemberProductType());
		rulesEngineInputPayload.setMemberType(member.getMemberType());
		rulesEngineInputPayload.setSystemType(member.getSystemType());

		try {
			Mockito.when(restClientService.getProviderValidRules(rulesEngineInputPayload)).thenReturn(null);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		restClientPayloadService.getProviderValidRules(member);
	}

	@Test
	public void getProviderValidRulesExceptionTest3() {
		Member member = getMemberInfo();
		try {
			Mockito.when(restClientService.getProviderValidRules(null)).thenReturn(null);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		restClientPayloadService.getProviderValidRules(member);
	}

	@Test
	public void getProviderValidationOutputPayloadTest1() {

		Member member = getMemberInfo();

		RulesEngineInputPayload rulesEngineInputPayload = null;
		rulesEngineInputPayload = new RulesEngineInputPayload();
		rulesEngineInputPayload.setMemberLineOfBusiness(member.getMemberLineOfBusiness());
		rulesEngineInputPayload.setMemberProcessingState(member.getMemberProcessingState());
		rulesEngineInputPayload.setMemberProductType(member.getMemberProductType());
		rulesEngineInputPayload.setMemberType(member.getMemberType());
		rulesEngineInputPayload.setSystemType(member.getSystemType());

		JsonNode droolsOutput = getDroolsOutput();
		ObjectMapper objMapper = new ObjectMapper();
		List<PCP> providerPayloadList = getPcpList();
		JsonNode providerValidationInpPayload = null;
	
		try {
			Mockito.when(restClientService.getProviderValidRules(rulesEngineInputPayload)).thenReturn(droolsOutput);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		ObjectNode memberData = objMapper.convertValue(member, ObjectNode.class);
		ArrayNode providerData = objMapper.valueToTree(providerPayloadList);
		ObjectNode rules = objMapper.convertValue(droolsOutput, ObjectNode.class);
		providerValidationInpPayload = objMapper.createObjectNode();
		((ObjectNode) providerValidationInpPayload).putObject(Constants.PROVIDER_RULES).setAll(rules);
		((ObjectNode) providerValidationInpPayload).putObject(Constants.MEMBER).setAll(memberData);
		((ObjectNode) providerValidationInpPayload).putArray(Constants.PCP_INFO).addAll(providerData);
		
		PCP pcp = getPcp();

		Mockito.when(pvHelper.getPCPForAffinity(member, providerPayloadList, droolsOutput.path(Constants.PROVIDER_RULES))).thenReturn(pcp);

		restClientPayloadService.getProviderValidationOutputPayload(getMemberInfo(), getPcpList());
	}

	@Test
	public void getProviderValidationOutputPayloadTest2() {

		JsonNode droolsRules = getDroolsOutput().path(Constants.PROVIDER_RULES);
		
		PCP pcp = getPcp();
		Member member = getMemberInfo();
		List<PCP> pcpList = getPcpList();

		Mockito.when(pvHelper.getPCPForAffinity(member, pcpList, droolsRules)).thenReturn(pcp);

		restClientPayloadService.getProviderValidationOutputPayload(getMemberInfo(), getPcpList());
	}

	@Test
	public void getProviderValidationOutputPayloadExceptionTest1() {

		JsonNode droolsRules = null;
		Member member = getMemberInfo();
		List<PCP> pcpList = getPcpList();

		Mockito.when(pvHelper.getPCPForAffinity(member, pcpList, droolsRules)).thenReturn(null);

		restClientPayloadService.getProviderValidationOutputPayload(getMemberInfo(), getPcpList());
	}
	
	@Test
	public void getProviderValidationOutputPayloadExceptionTest2() {

		JsonNode droolsRules = getDroolsOutput().path(Constants.PROVIDER_RULES);
		Member member = getMemberInfo();
		List<PCP> pcpList = getPcpList();

		Mockito.when(pvHelper.getPCPForAffinity(member, pcpList, droolsRules)).thenReturn(null);

		restClientPayloadService.getProviderValidationOutputPayload(getMemberInfo(), getPcpList());
	}

	private AffinityOutputPayload getFlowOprPayloadSuccess() {

		AffinityOutputPayload affinityOutputPayload = new AffinityOutputPayload();
		affinityOutputPayload.setPcpId("Z0Z017");
		affinityOutputPayload.setResponseCode(ErrorMessages.SUCCESS);
		affinityOutputPayload.setResponseMessage(ErrorMessages.PCP_ASSIGNED + "Z0Z017");

		return affinityOutputPayload;
	}

	private AffinityOutputPayload getFlowOprPayloadConstraintFailure() {

		AffinityOutputPayload affinityOutputPayload = new AffinityOutputPayload();
		affinityOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		affinityOutputPayload.setResponseMessage(ErrorMessages.FAILURE);

		return affinityOutputPayload;
	}

	private AffinityOutputPayload getFlowOprPayloadFutureDateFailure() {

		AffinityOutputPayload affinityOutputPayload = new AffinityOutputPayload();
		affinityOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		affinityOutputPayload.setResponseMessage(ErrorMessages.INVALID_DOB);

		return affinityOutputPayload;
	}

	private AffinityOutputPayload getFlowOprPayloadFailure() {

		AffinityOutputPayload affinityOutputPayload = new AffinityOutputPayload();
		affinityOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		affinityOutputPayload.setResponseMessage(ErrorMessages.FAILURE);

		return affinityOutputPayload;
	}

	private Member getMemberInfo() {

		Member member = new Member();
		member.setInvocationSystem("06");
		member.setSystemType("B");
		member.setMemberEid("976A78568");
		member.setMemberType("N");
		member.setMemberLineOfBusiness("CT1");
		member.setMemberProcessingState("CT");
		member.setMemberContractCode(Arrays.asList("CC86"));
		member.setMemberNetworkId(Arrays.asList("CC0D"));
		Address memberAddress = new Address();
		memberAddress.setLatitude(35.36645);
		memberAddress.setLongitude(-119.0098);
		member.setAddress(memberAddress);
		member.setMemberDob("1972-09-05");
		member.setMemberGender("M");
		member.setMemberSequenceNumber("2");
		member.setMemberFirstName("KERI");
		member.setRollOverPcpId("Z0Z07");
		member.setMemberEffectiveDate("1992-06-13");
		member.setMemberProductType("HMO");		

		return member;
	}

	private List<PCP> getPcpList() {

		List<PCP> pcpList = new ArrayList<>();
		PCP pcpA = new PCP();
		pcpA.setProvPcpId("Z0Z213");
		pcpA.setAerialDistance(52.91);
		pcpA.setDrivingDistance(52.91);
		pcpA.setRgnlNtwkId("CC0D");

		pcpList.add(pcpA);

		PCP pcpB = new PCP();
		pcpB.setProvPcpId("Z0Z017");
		pcpB.setAerialDistance(7.09);
		pcpB.setDrivingDistance(7.09);
		pcpB.setRgnlNtwkId("CC0D");

		pcpList.add(pcpB);

		return pcpList;
	}

	private JsonNode getDroolsOutput() {
		JsonNode droolsResponse = null;

		try {
			droolsResponse = new ObjectMapper().readTree("{\"responseCode\":200,\"responseMessage\":\"Affinity Provider Validation Rules successfully applied on Payload\",\"rules\":{\"primarySpecialties\":[\"Internal Medicine\",\"Family Practice\",\"Pediatric\",\"General Practice\",\"Geriatric\",\"Nurse Practice\"],\"contractEffectiveBeyond\":90,\"drivingDistance\":30,\"providerTiers\":[1],\"rolloverFlag\":\"Y\",\"specialtyMinAgeList\":[{\"key\":\"Pediatric\",\"value\":0},{\"key\":\"Geriatric\",\"value\":60}],\"validationFlagList\":[{\"key\":\"SPECIALTY_VALIDATION\",\"value\":true},{\"key\":\"AGE_RANGE_VALIDATION\",\"value\":true},{\"key\":\"GENDER_VALIDATION\",\"value\":false},{\"key\":\"CONTRACT_VALIDATION\",\"value\":true},{\"key\":\"DISTANCE_VALIDATION\",\"value\":true},{\"key\":\"PANEL_CAPACITY_VALIDATION\",\"value\":false},{\"key\":\"NETWORK_VALIDATION\",\"value\":true},{\"key\":\"TIER_VALIDATION\",\"value\":false},{\"key\":\"ACCEPTING_PATIENTS_VALIDATION\",\"value\":false},{\"key\":\"ROLLOVER_VALIDATION\",\"value\":true}],\"specialtyMaxAgeList\":[{\"key\":\"Pediatric\",\"value\":18},{\"key\":\"Geriatric\",\"value\":999}],\"specialtyGenderList\":[{\"key\":\"OBGYN\",\"value\":\"F\"}],\"panelCapacityPercent\":0}}");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return droolsResponse;
	}

	private PCP getPcp() {
		PCP pcp = new PCP();
		pcp.setProvPcpId("Z0Z017");
		pcp.setAerialDistance(7.09);
		pcp.setDrivingDistance(7.09);
		pcp.setRgnlNtwkId("CC0D");

		return pcp;
	}
}