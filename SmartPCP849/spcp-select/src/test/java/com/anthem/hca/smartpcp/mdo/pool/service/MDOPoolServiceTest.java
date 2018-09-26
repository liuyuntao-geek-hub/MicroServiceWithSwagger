package com.anthem.hca.smartpcp.mdo.pool.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.service.MDOPoolingProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOScoringService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.mdoscoring.helper.MDOScoreHelper;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.PCPAssignmentFlow;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.service.OperationAuditFlowService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MDOPoolServiceTest {

	@InjectMocks
	private MDOPoolService mdoPoolService;

	@Mock
	private ProviderPoolService providerPoolService;

	@Mock
	private MDOScoreHelper mdoHelper;

	@Mock
	private OperationAuditFlowService operationAuditFlowService;

	@Mock
	private MDOScoringService scoringService;

	@Mock
	private MDOPoolingProviderValidationService mdoProviderValidationService;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetValidPCP() throws Exception {
		Mockito.when(mdoProviderValidationService.getRules(any(Member.class))).thenReturn(getMDOPoolingProviderRules());
		Mockito.when(providerPoolService.poolBuilder(any(Member.class), any(MDOPoolingProviderValidationRules.class)))
				.thenReturn(getValidPool());
		doNothing().when(operationAuditFlowService).insertOperationFlowMDO(Mockito.anyInt(),
				Mockito.any(ScoringProvider.class));
		Mockito.when(scoringService.getRules(any(Member.class))).thenReturn(getMdoScoringRules());
		Mockito.when(mdoHelper.getBestPCP(Mockito.anyList(), any(MDOScoringRules.class), any(Member.class),
				any(PCPAssignmentFlow.class))).thenReturn(getBestPCP());

		ScoringProvider scoringProvider = mdoPoolService.getValidPCP(createMember());

		assertEquals("MDO123", scoringProvider.getProvPcpId());
	}

	@Test
	public void testGetValidPCP2() throws Exception {
		Mockito.when(mdoProviderValidationService.getRules(any(Member.class))).thenReturn(getMDOPoolingProviderRules());
		Mockito.when(providerPoolService.poolBuilder(any(Member.class), any(MDOPoolingProviderValidationRules.class)))
				.thenReturn(new ArrayList<ScoringProvider>());
		doNothing().when(operationAuditFlowService).insertOperationFlowMDO(Mockito.anyInt(),
				Mockito.any(ScoringProvider.class));
		Mockito.when(scoringService.getRules(any(Member.class))).thenReturn(getMdoScoringRules());
		Mockito.when(mdoHelper.getBestPCP(Mockito.anyList(), any(MDOScoringRules.class), any(Member.class),
				any(PCPAssignmentFlow.class))).thenReturn(new ScoringProvider());

		ScoringProvider scoringProvider = mdoPoolService.getValidPCP(createMember());

		assertEquals("DUMMY0", scoringProvider.getProvPcpId());
	}

	private MDOPoolingProviderValidationRules getMDOPoolingProviderRules()
			throws DroolsParseException, JsonParseException, JsonMappingException, IOException {

		MDOPoolingProviderValidationRules mdoPoolingproviderRules = new MDOPoolingProviderValidationRules(
				getPoolingRules(), getProviderValitionRules());
		return mdoPoolingproviderRules;

	}

	private MDOPoolingRules getPoolingRules() throws DroolsParseException {

		MDOPoolingRules mdoPoolingRules = new MDOPoolingRules(AgendaGroup.MDO_POOLING);
		mdoPoolingRules.setPoolSize("1000");
		mdoPoolingRules.setDummyProviderId("DUMMY0");
		return mdoPoolingRules;
	}

	private ProviderValidationRules getProviderValitionRules() throws DroolsParseException {

		ProviderValidationRules providerValidationRules = new ProviderValidationRules(
				AgendaGroup.MDO_PROVIDER_VALIDATION);

		providerValidationRules.setInvocationSystem("12");
		providerValidationRules.setPrimarySpecialties("Internal Medicine,Family Practice,Pediatric,General Practice");
		providerValidationRules.setSpecialty("Pediatric");
		providerValidationRules.setMinAgeAllowedForSpecialty("0");
		providerValidationRules.setMaxAgeAllowedForSpecialty("18");
		providerValidationRules.setSpecialty("OBGYN");
		providerValidationRules.setGenderForSpecialty("F");
		providerValidationRules.setContractCushionPeriod("90");
		providerValidationRules.setDistance("15");
		providerValidationRules.setPanelCapacityPercent("85");
		providerValidationRules.setProviderTiers("1");
		providerValidationRules.setRolloverFlag("N");
		providerValidationRules.setValidationRequired("GENDER_VALIDATION", "YES");
		providerValidationRules.setValidationRequired("SPECIALTY_VALIDATION", "YES");
		providerValidationRules.setValidationRequired("CONTRACT_VALIDATION", "YES");
		providerValidationRules.setValidationRequired("DISTANCE_VALIDATION", "YES");
		providerValidationRules.setValidationRequired("PANEL_CAPACITY_VALIDATION", "YES");
		providerValidationRules.setValidationRequired("NETWORK_VALIDATION", "YES");
		providerValidationRules.setValidationRequired("TIER_VALIDATION", "YES");
		providerValidationRules.setValidationRequired("ACCEPTING_PATIENTS_VALIDATION", "YES");
		providerValidationRules.setValidationRequired("ROLLOVER_VALIDATION", "NO");

		return providerValidationRules;
	}

	private MDOScoringRules getMdoScoringRules() throws DroolsParseException {
		MDOScoringRules mdoScoringRules = new MDOScoringRules();

		mdoScoringRules.setAgendaGroup(AgendaGroup.MDO_SCORING);

		mdoScoringRules.setLimitedTime("365");
		mdoScoringRules.setLimitedTimeScore("15");
		mdoScoringRules.setPanelCapacityPercent("25");
		mdoScoringRules.setRestrictedAgeSpecialties("GERIATRIC");
		mdoScoringRules.setProximity("0-5");
		mdoScoringRules.setProximityScore("50");
		mdoScoringRules.setProximity("5-10");
		mdoScoringRules.setProximityScore("40");
		mdoScoringRules.setProximity("10-15");
		mdoScoringRules.setProximityScore("30");
		mdoScoringRules.setProximity("15-20");
		mdoScoringRules.setProximityScore("20");
		mdoScoringRules.setProximity("20-30");
		mdoScoringRules.setProximityScore("0");
		mdoScoringRules.setProximity("30+");
		mdoScoringRules.setProximityScore("-75");

		mdoScoringRules.setAgeSpecialtyMatch("YES");
		mdoScoringRules.setAgeSpecialtyMatchScore("10");
		mdoScoringRules.setAgeSpecialtyMatch("NO");
		mdoScoringRules.setAgeSpecialtyMatchScore("0");

		mdoScoringRules.setLanguageMatch("YES");
		mdoScoringRules.setLanguageMatchScore("10");
		mdoScoringRules.setLanguageMatch("NO");
		mdoScoringRules.setLanguageMatchScore("0");

		mdoScoringRules.setVBAParticipation("YES");
		mdoScoringRules.setVBAParticipationScore("20");
		mdoScoringRules.setVBAParticipation("NO");
		mdoScoringRules.setVBAParticipationScore("0");

		mdoScoringRules.setMDORank("5");
		mdoScoringRules.setMDORankScore("75");
		mdoScoringRules.setMDORank("4");
		mdoScoringRules.setMDORankScore("65");
		mdoScoringRules.setMDORank("3");
		mdoScoringRules.setMDORankScore("30");
		mdoScoringRules.setMDORank("2");
		mdoScoringRules.setMDORankScore("20");
		mdoScoringRules.setMDORank("1");
		mdoScoringRules.setMDORankScore("10");
		mdoScoringRules.setMDORank("0");
		mdoScoringRules.setMDORankScore("30");

		return mdoScoringRules;
	}

	private Member createMember() {
		Member member = new Member();
		member.setMemberGroupId(123);
		member.setInvocationSystem("12");
		member.setSystemType("O");
		member.setRequestType("N");
		member.setMemberSubGroupId(123);
		member.setMemberEid("ASD");
		List<String> networkIds = new ArrayList<>();
		networkIds.add("HARSH01");
		member.setMemberNetworkId(networkIds);
		member.setMemberLineOfBusiness("CT0");
		member.setMemberSequenceNumber("ASD123");
		Address address = new Address();
		address.setAddressLine1("STREET");
		address.setCity("Sacramento");
		address.setState("CA");
		address.setZipCode("23005");
		address.setZipFour("1234");
		address.setLatitude(23.45);
		address.setLongitude(-164.56);
		member.setAddress(address);
		member.setMemberProduct("14EM");
		member.setMemberDob("1990-07-23");
		member.setRollOverPcpId("1234");
		member.setMemberPregnancyIndicator("Y");
		member.setMemberEffectiveDate("1989-09-12");
		member.setMemberGender("M");
		return member;
	}

	private List<ScoringProvider> getValidPool() {

		List<ScoringProvider> scoringProviderList = new ArrayList<>();
		ScoringProvider pcp = new ScoringProvider();
		pcp.setProvPcpId("MDO123");
		pcp.setRgnlNtwkId("HARSH01");
		pcp.setLastName("MDO_LAST");
		pcp.setDistance(7.0680473834351885);
		pcp.setDummyFlag(false);
		pcp.setRank(3);
		pcp.setVbpFlag("Y");
		pcp.setPcpScore(115);
		pcp.setVbpScore(20);
		pcp.setDistanceScore(40);
		pcp.setLimitedTimeBonusScore(15);
		pcp.setLanguageScore(10);
		pcp.setRankScore(30);
		List<String> langList = new ArrayList<>();
		langList.add("SPA");
		List<String> spcltyList = new ArrayList<>();
		spcltyList.add("Internal Medicine");
		pcp.setPcpLang(langList);
		pcp.setSpeciality(spcltyList);

		ScoringProvider pcp2 = new ScoringProvider();
		pcp2.setProvPcpId("MDO456");
		pcp2.setRgnlNtwkId("HARSH01");
		pcp2.setLastName("MDO_LAST");
		pcp2.setDistance(7.0680473834351885);
		pcp2.setDummyFlag(false);
		pcp2.setRank(3);
		pcp2.setVbpFlag("Y");
		pcp2.setPcpScore(160);
		pcp2.setVbpScore(20);
		pcp2.setDistanceScore(20);
		pcp2.setLimitedTimeBonusScore(15);
		pcp2.setLanguageScore(10);
		pcp2.setRankScore(20);
		List<String> langList2 = new ArrayList<>();
		langList.add("SPA");
		List<String> spcltyList2 = new ArrayList<>();
		spcltyList.add("Internal Medicine");
		pcp2.setPcpLang(langList2);
		pcp2.setSpeciality(spcltyList2);

		scoringProviderList.add(pcp);
		scoringProviderList.add(pcp2);
		return scoringProviderList;
	}

	private ScoringProvider getBestPCP() {
		ScoringProvider pcp = new ScoringProvider();
		pcp.setProvPcpId("MDO123");
		pcp.setRgnlNtwkId("HARSH01");
		pcp.setLastName("MDO_LAST");
		pcp.setDistance(7.0680473834351885);
		pcp.setDummyFlag(false);
		pcp.setRank(3);
		pcp.setVbpFlag("Y");
		pcp.setPcpScore(115);
		pcp.setVbpScore(20);
		pcp.setDistanceScore(40);
		pcp.setLimitedTimeBonusScore(15);
		pcp.setLanguageScore(10);
		pcp.setRankScore(30);
		List<String> langList = new ArrayList<>();
		langList.add("SPA");
		List<String> spcltyList = new ArrayList<>();
		spcltyList.add("Internal Medicine");
		pcp.setPcpLang(langList);
		pcp.setSpeciality(spcltyList);

		return pcp;
	}
}
