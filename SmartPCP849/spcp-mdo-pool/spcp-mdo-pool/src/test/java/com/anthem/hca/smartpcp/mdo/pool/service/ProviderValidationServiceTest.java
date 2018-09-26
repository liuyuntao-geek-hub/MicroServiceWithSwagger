/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.constants.ResponseCodes;
import com.anthem.hca.smartpcp.mdo.pool.model.OutputPayload;
import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProviderValidationServiceTest {

	@InjectMocks
	private ProviderValidationService providerValidationService;

	@Mock
	private RestClientService restClientService;

	@Mock
	private MDOPoolService mdoPoolService;

	@Mock
	private ProviderValidationHelper providerValidationhelper;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPCPValidation() throws JsonProcessingException, ParseException {

		Member member = createMember();

		List<PCP> pcpList = createPCPList();
		JsonNode rules = createJsonNodeRules();
		OutputPayload outputPayload = providerValidationService.getPCPValidation(member, rules, pcpList, 1, "AB123",
				true);
		assertNotNull(outputPayload);
		assertEquals(200, outputPayload.getResponseCode());
	}

	@Test
	public void testGetPCPValidationError() throws JsonProcessingException, ParseException {

		Member member = createMember();
		List<PCP> pcpList = createPCPList();
		JsonNode rules = createErrorJsonNodeRules();
		Mockito.when(mdoPoolService.createDummyProvider(any(String.class))).thenReturn(createDummyPCP());
		Mockito.when(providerValidationhelper.getPCPListMDO(Mockito.any(Member.class), Mockito.anyListOf(PCP.class),
				Mockito.any(JsonNode.class), Mockito.anyInt())).thenReturn(null);
		OutputPayload outputPayload = providerValidationService.getPCPValidation(member, rules, pcpList, 1, "AB123",
				true);
		assertNotNull(outputPayload);
		assertEquals(200, outputPayload.getResponseCode());
	}

	public Member createMember() {
		Member member = new Member();
		member.setInvocationSystem("01");
		member.setMemberGender("M");
		member.setMemberProcessingState("ALL");
		List<String> ctList = new ArrayList<String>();
		ctList.add("CC09");
		member.setMemberContractCode(ctList);
		member.setMemberDob("2001-12-12");
		member.setMemberEffectiveDate("2000-01-01");
		return member;
	}

	public OutputPayload createDummyPCP() {
		List<PCP> pcpPool = new ArrayList<>();
		OutputPayload outputPayload = new OutputPayload();
		PCP pcp = new PCP();

		pcp.setProvPcpId("DUMMY1");
		pcpPool.add(pcp);
		outputPayload.setPcps(pcpPool);
		outputPayload.setDummyFlag(true);
		outputPayload.setResponseCode(ResponseCodes.SUCCESS);
		outputPayload.setResponseMessage(MDOPoolConstants.SUCCESS);

		return outputPayload;
	}

	public List<PCP> createPCPList() throws ParseException {
		List<PCP> pcpList = new ArrayList<>();
		List<String> wgsLandCd = new ArrayList<>();
		PCP pcp1 = new PCP();
		PCP pcp2 = new PCP();

		wgsLandCd.add("ENG");
		pcp1.setProvPcpId("AD1234");
		pcp1.setLatdCordntNbr(11.1234);
		pcp1.setLngtdCordntNbr(23.1234);
		pcp1.setSpcltyDesc("Internal Medicine");
		pcp1.setPcpLang(wgsLandCd);
		pcp1.setMaxMbrCnt(123);
		pcp1.setCurntMbrCnt(12);
		pcp1.setAccNewPatientFlag("Y");
		pcp1.setTierLvl(1);
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("1990-04-26");
		pcp1.setGrpgRltdPadrsTrmntnDt(date);
		pcp1.setRgnlNtwkId("CC09");
		pcp2.setProvPcpId("AZ4456");
		pcp2.setLatdCordntNbr(21.1234);
		pcp2.setLngtdCordntNbr(32.1234);
		pcpList.add(pcp1);
		pcpList.add(pcp2);
		return pcpList;
	}

	@SuppressWarnings("deprecation")
	public JsonNode createJsonNodeRules() {

		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		ArrayNode spcltyArray = mapper.createArrayNode();
		ArrayNode tierArray = mapper.createArrayNode();
		ArrayNode specialtyGenderArray = mapper.createArrayNode();
		ArrayNode specialtyMinAgeArray = mapper.createArrayNode();
		ArrayNode specialtyMaxAgeArray = mapper.createArrayNode();

		ObjectNode child = factory.objectNode(); // the child
		ObjectNode valid1 = factory.objectNode();
		ObjectNode valid2 = factory.objectNode();
		ObjectNode valid3 = factory.objectNode();
		ObjectNode valid4 = factory.objectNode();
		ObjectNode valid5 = factory.objectNode();
		ObjectNode valid6 = factory.objectNode();
		ObjectNode valid7 = factory.objectNode();
		ObjectNode valid8 = factory.objectNode();
		ObjectNode valid9 = factory.objectNode();
		ObjectNode valid10 = factory.objectNode();

		ObjectNode genderNode = factory.objectNode();
		ObjectNode specialtyMinAge = factory.objectNode();
		ObjectNode specialtyMinAge2 = factory.objectNode();

		ObjectNode specialtyMaxAge = factory.objectNode();
		ObjectNode specialtyMaxAge2 = factory.objectNode();

		genderNode.put("key", "OBGYN");
		genderNode.put("value", "F");
		specialtyGenderArray.add(genderNode);

		specialtyMinAge.put("key", "Pediatric");
		specialtyMinAge.put("value", 0);
		specialtyMinAge2.put("key", "Geriatric");
		specialtyMinAge2.put("value", 60);
		specialtyMinAgeArray.add(specialtyMinAge);
		specialtyMinAgeArray.add(specialtyMinAge2);

		specialtyMaxAge.put("key", "Pediatric");
		specialtyMaxAge.put("value", 18);
		specialtyMaxAge2.put("key", "Geriatric");
		specialtyMaxAge2.put("value", 999);
		specialtyMaxAgeArray.add(specialtyMaxAge);
		specialtyMaxAgeArray.add(specialtyMaxAge2);

		valid1.put("key", "SPECIALTY_VALIDATION");
		valid1.put("value", false);
		valid2.put("key", "AGE_RANGE_VALIDATION");
		valid2.put("value", false);
		valid3.put("key", "GENDER_VALIDATION");
		valid3.put("value", false);
		valid4.put("key", "CONTRACT_VALIDATION");
		valid4.put("value", false);
		valid5.put("key", "DISTANCE_VALIDATION");
		valid5.put("value", false);
		valid6.put("key", "PANEL_CAPACITY_VALIDATION");
		valid6.put("value", false);
		valid7.put("key", "NETWORK_VALIDATION");
		valid7.put("value", false);
		valid8.put("key", "TIER_VALIDATION");
		valid8.put("value", true);
		valid9.put("key", "ACCEPTING_PATIENTS_VALIDATION");
		valid9.put("value", false);
		valid10.put("key", "ROLLOVER_VALIDATION");
		valid10.put("value", false);

		arrayNode.add(valid1);
		arrayNode.add(valid2);
		arrayNode.add(valid3);
		arrayNode.add(valid4);
		arrayNode.add(valid5);
		arrayNode.add(valid6);
		arrayNode.add(valid7);
		arrayNode.add(valid8);
		arrayNode.add(valid9);
		arrayNode.add(valid10);

		spcltyArray.add("Internal Medicine");
		spcltyArray.add("Family Practice");
		spcltyArray.add("Pediatric");
		spcltyArray.add("General Practice");
		spcltyArray.add("Geriatrics");
		spcltyArray.add("Nurse Practice");
		spcltyArray.add("OBGYN");

		tierArray.add(1);

		child.put("primarySpecialties", spcltyArray);
		child.put("contractEffectiveBeyond", 30);
		child.put("drivingDistance", 30);
		child.put("providerTiers", tierArray);
		child.put("rolloverFlag", "N");
		child.put("specialtyGenderList", specialtyGenderArray);
		child.put("specialtyMinAgeList", specialtyMinAgeArray);
		child.put("specialtyMaxAgeList", specialtyMaxAgeArray);
		child.put("panelCapacityPercent", 85);
		child.put("validationFlagList", arrayNode);

		return child;
	}

	@SuppressWarnings("deprecation")
	public JsonNode createErrorJsonNodeRules() {

		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		ArrayNode spcltyArray = mapper.createArrayNode();
		ArrayNode tierArray = mapper.createArrayNode();
		ArrayNode specialtyGenderArray = mapper.createArrayNode();
		ArrayNode specialtyMinAgeArray = mapper.createArrayNode();
		ArrayNode specialtyMaxAgeArray = mapper.createArrayNode();

		ObjectNode child = factory.objectNode(); // the child
		ObjectNode valid1 = factory.objectNode();
		ObjectNode valid2 = factory.objectNode();
		ObjectNode valid3 = factory.objectNode();
		ObjectNode valid4 = factory.objectNode();
		ObjectNode valid5 = factory.objectNode();
		ObjectNode valid6 = factory.objectNode();
		ObjectNode valid7 = factory.objectNode();
		ObjectNode valid8 = factory.objectNode();
		ObjectNode valid9 = factory.objectNode();
		ObjectNode valid10 = factory.objectNode();

		ObjectNode genderNode = factory.objectNode();
		ObjectNode specialtyMinAge = factory.objectNode();
		ObjectNode specialtyMinAge2 = factory.objectNode();

		ObjectNode specialtyMaxAge = factory.objectNode();
		ObjectNode specialtyMaxAge2 = factory.objectNode();

		genderNode.put("key", "OBGYN");
		genderNode.put("value", "F");
		specialtyGenderArray.add(genderNode);

		specialtyMinAge.put("key", "Pediatric");
		specialtyMinAge.put("value", 0);
		specialtyMinAge2.put("key", "Geriatric");
		specialtyMinAge2.put("value", 60);
		specialtyMinAgeArray.add(specialtyMinAge);
		specialtyMinAgeArray.add(specialtyMinAge2);

		specialtyMaxAge.put("key", "Pediatric");
		specialtyMaxAge.put("value", 18);
		specialtyMaxAge2.put("key", "Geriatric");
		specialtyMaxAge2.put("value", 999);
		specialtyMaxAgeArray.add(specialtyMaxAge);
		specialtyMaxAgeArray.add(specialtyMaxAge2);

		valid1.put("key", "SPECIALTY_VALIDATION");
		valid1.put("value", true);
		valid2.put("key", "AGE_RANGE_VALIDATION");
		valid2.put("value", true);
		valid3.put("key", "GENDER_VALIDATION");
		valid3.put("value", true);
		valid4.put("key", "CONTRACT_VALIDATION");
		valid4.put("value", true);
		valid5.put("key", "DISTANCE_VALIDATION");
		valid5.put("value", true);
		valid6.put("key", "PANEL_CAPACITY_VALIDATION");
		valid6.put("value", true);
		valid7.put("key", "NETWORK_VALIDATION");
		valid7.put("value", true);
		valid8.put("key", "TIER_VALIDATION");
		valid8.put("value", true);
		valid9.put("key", "ACCEPTING_PATIENTS_VALIDATION");
		valid9.put("value", true);
		valid10.put("key", "ROLLOVER_VALIDATION");
		valid10.put("value", true);

		arrayNode.add(valid1);
		arrayNode.add(valid2);
		arrayNode.add(valid3);
		arrayNode.add(valid4);
		arrayNode.add(valid5);
		arrayNode.add(valid6);
		arrayNode.add(valid7);
		arrayNode.add(valid8);
		arrayNode.add(valid9);
		arrayNode.add(valid10);

		spcltyArray.add("Internal Medicine");
		spcltyArray.add("Family Practice");
		spcltyArray.add("Pediatric");
		spcltyArray.add("General Practice");
		spcltyArray.add("Geriatrics");
		spcltyArray.add("Nurse Practice");
		spcltyArray.add("OBGYN");

		tierArray.add(1);

		child.put("primarySpecialties", spcltyArray);
		child.put("contractEffectiveBeyond", 30);
		child.put("drivingDistance", 30);
		child.put("providerTiers", tierArray);
		child.put("rolloverFlag", "N");
		child.put("specialtyGenderList", specialtyGenderArray);
		child.put("specialtyMinAgeList", specialtyMinAgeArray);
		child.put("specialtyMaxAgeList", specialtyMaxAgeArray);
		child.put("panelCapacityPercent", 85);
		child.put("validationFlagList", arrayNode);

		return child;
	}

}
