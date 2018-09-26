package com.anthem.hca.smartpcp.mdo.pool.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

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
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.mdo.pool.repository.PoolingRepo;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ProviderPoolServiceTest {

	@InjectMocks
	private ProviderPoolService providerPoolService;

	@Mock
	private PoolingRepo providerInfoRepo;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPoolBuilder() throws JsonParseException, JsonMappingException, DroolsParseException, IOException {
		Mockito.when(providerInfoRepo.getPCPDtlsAsList(Mockito.anyList(), any(Member.class),
				any(MDOPoolingProviderValidationRules.class), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(getValidPool());
		List<ScoringProvider> providerList = providerPoolService.poolBuilder(createMember(),
				getMDOPoolingProviderRules());
		assertEquals(2, providerList.size());
	}

	@Test
	public void testPoolBuilder2() throws JsonParseException, JsonMappingException, DroolsParseException, IOException {
		Mockito.when(providerInfoRepo.getPCPDtlsAsList(Mockito.anyList(), any(Member.class),
				any(MDOPoolingProviderValidationRules.class), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(new ArrayList<ScoringProvider>());
		List<ScoringProvider> providerList = providerPoolService.poolBuilder(createMember(),
				getMDOPoolingProviderRules());
		assertEquals(0, providerList.size());
	}

	@Test
	public void testPoolBuilder3() throws JsonParseException, JsonMappingException, DroolsParseException, IOException {
		Mockito.when(providerInfoRepo.getPCPDtlsAsList(Mockito.anyList(), any(Member.class),
				any(MDOPoolingProviderValidationRules.class), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(getValidPool());
		List<ScoringProvider> providerList = providerPoolService.poolBuilder(createMember2(),
				getMDOPoolingProviderRules());
		assertEquals(2, providerList.size());
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
		List<String> cccodes = new ArrayList<>();
		networkIds.add("HARSH01");
		cccodes.add("CC01");
		member.setMemberNetworkId(networkIds);
		member.setMemberContractCode(cccodes);
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
		member.setMemberDob("2010-07-23");
		member.setRollOverPcpId("1234");
		member.setMemberPregnancyIndicator("Y");
		member.setMemberEffectiveDate("1989-09-12");
		member.setMemberGender("M");
		return member;
	}

	private Member createMember2() {
		Member member = new Member();
		member.setMemberGroupId(123);
		member.setInvocationSystem("12");
		member.setSystemType("O");
		member.setRequestType("N");
		member.setMemberSubGroupId(123);
		member.setMemberEid("ASD");
		List<String> networkIds = new ArrayList<>();
		List<String> cccodes = new ArrayList<>();
		networkIds.add("HARSH01");
		cccodes.add("CC01");
		member.setMemberNetworkId(networkIds);
		member.setMemberContractCode(cccodes);
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
}
