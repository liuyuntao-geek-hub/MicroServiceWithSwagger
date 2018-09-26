package com.anthem.hca.smartpcp.affinity.service;

import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.anthem.hca.smartpcp.affinity.repo.MemberRepo;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.service.AffinityProviderValidationService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;
import com.anthem.hca.smartpcp.service.OperationAuditFlowService;
import com.fasterxml.jackson.core.JsonProcessingException;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		AffinityServiceTest is used to test the AffinityService
 *  
 * @author AF65409 
 */
/*@RunWith(value = SpringRunner.class)*/
public class AffinityServiceTest {

	@InjectMocks
	AffinityService affinityService;
	
	@Mock
	private MemberRepo memberRepo;
	
	@Mock
	private AffinityProviderValidationService affinityProviderValidationService;
	
	@Mock
	private ProviderValidationHelper providerValidationHelper;
	
	@Mock
	private OperationAuditFlowService operationFlowService;

	@Before 
	public void setupMock() { 

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getAffinityOutPayloadTest1() throws JsonProcessingException, DroolsParseException {

		Member member = getMemberInfo();
		List<Provider> providerList = getProviderList();
		ProviderValidationRules rules = getRules();
		Provider validPCP = getValidPCP();
		
		Mockito.when(memberRepo.getPCPsForAffinity(Mockito.any(Member.class))).thenReturn(providerList);
		
		Mockito.when(affinityProviderValidationService.getRules(member)).thenReturn(rules);
		Mockito.when(providerValidationHelper.getPCPForAffinity(member, providerList, rules)).thenReturn(validPCP);
		Mockito.doNothing().when(operationFlowService).insertOperationFlowAffinity(providerList, validPCP,false);

		Provider p = affinityService.getAffinityOutPayload(member);
		assertEquals(p.getProvPcpId(),providerList.get(0).getProvPcpId());
	}
	
	@Test
	public void getAffinityOutPayloadTest2() throws JsonProcessingException, DroolsParseException {

		Member member = getMemberInfo();
		List<Provider> providerList = getProviderList();
		ProviderValidationRules rules = getRules();
		Provider validPCP = getValidPCP();
		
		Mockito.when(memberRepo.getPCPsForAffinity(Mockito.any(Member.class))).thenReturn(null);
		
		Mockito.when(affinityProviderValidationService.getRules(member)).thenReturn(rules);
		Mockito.when(providerValidationHelper.getPCPForAffinity(member, providerList, rules)).thenReturn(validPCP);
		Mockito.doNothing().when(operationFlowService).insertOperationFlowAffinity(providerList, validPCP,false);

		Provider p = affinityService.getAffinityOutPayload(member);
		assertEquals(null,p);
	}
	
	private Member getMemberInfo() {

		Member member = new Member();
		member.setInvocationSystem("06");
		member.setSystemType("O");
		member.setRequestType("S");
		member.setMemberEid("976A78568");
		member.setMemberType("N");
		member.setMemberLineOfBusiness("CT1");
		member.setMemberProcessingState("CT");
		member.setMemberContractCode(Arrays.asList("CC86"));
		member.setMemberNetworkId(Arrays.asList("CC0D"));
		Address memberAddress = new Address();
		memberAddress.setAddressLine1("525 ROBERTS LN");
		memberAddress.setAddressLine2("");
		memberAddress.setCity("BAKERSFIELD");
		memberAddress.setCountyCode("97");
		memberAddress.setState("CA");
		memberAddress.setZipCode("93308");
		memberAddress.setZipFour("4799");
		memberAddress.setLatitude(34.408630);
		memberAddress.setLongitude(-118.031217);
		member.setAddress(memberAddress);
		member.setMemberDob("1972-09-05");
		member.setMemberGender("M");
		member.setMemberLanguageCode(Arrays.asList("SPA","ENG"));
		member.setMemberProduct("Z2LK");
		member.setMemberProductType("HMO");
		member.setMemberSourceSystem("ISG");
		member.setMemberSequenceNumber("2");
		member.setMemberFirstName("KERI");
		member.setMemberMiddleName("H");
		member.setMemberLastName("GAYLE_SPCP_DEV");
		member.setRollOverPcpId("Z0Z07");
		member.setMemberEffectiveDate("2017-06-13");
		member.setMemberTerminationDate("2030-06-13");
		member.setUpdateCounter("1");
		member.setMemberGroupId(123);
		member.setMemberSubGroupId(123);
		return member;
	}
	
	private List<Provider> getProviderList() {

		List<Provider> pcpInfoDtlsList = new ArrayList<>();

		Provider pcpInfoDtlsA = new Provider();
		pcpInfoDtlsA.setProvPcpId("Z0Z017");
		pcpInfoDtlsA.setPcpPmgIpa("PCP");
		pcpInfoDtlsA.setRgnlNtwkId("CC0D");
		Address providerAddress = new Address();
		providerAddress.setAddressLine1("525 ROBERTS LN");
		providerAddress.setAddressLine2("");
		providerAddress.setCity("BAKERSFIELD");
		providerAddress.setCountyCode("029");
		providerAddress.setState("CA");
		providerAddress.setZipCode("93308");
		providerAddress.setZipFour("4799");
		providerAddress.setLatitude(35.408630);
		providerAddress.setLongitude(-119.031217);
		pcpInfoDtlsA.setAddress(providerAddress);
		pcpInfoDtlsA.setFirstName("KATHERINE");
		pcpInfoDtlsA.setMiddleName("R");
		pcpInfoDtlsA.setLastName("SCHLAERTH");
		pcpInfoDtlsA.setPhoneNumber("");
		List<String> SpcltyListA = new ArrayList<>();
		SpcltyListA.add("Fam Pra");
		pcpInfoDtlsA.setSpeciality(SpcltyListA);
		List<String> SpcltyDescListA = new ArrayList<>();
		SpcltyDescListA.add("Family Practice");
		pcpInfoDtlsA.setSpecialityDesc(SpcltyDescListA);
		pcpInfoDtlsA.setAcoPcpReturned("");
		pcpInfoDtlsA.setTaxId("954337143");
		pcpInfoDtlsA.setGrpgRltdPadrsEfctvDt(new Date(2017-01-01));
		pcpInfoDtlsA.setGrpgRltdPadrsTrmntnDt(new Date(9999-12-31));
		pcpInfoDtlsA.setMaxMbrCnt(2500);
		pcpInfoDtlsA.setCurntMbrCnt(0);
		pcpInfoDtlsA.setMemberMcid("1234");

		pcpInfoDtlsList.add(pcpInfoDtlsA);

		return pcpInfoDtlsList;

	}
	
	private ProviderValidationRules getRules() throws DroolsParseException {

		ProviderValidationRules providerValidationRules = new ProviderValidationRules(null);
		providerValidationRules.setContractCushionPeriod("90");
		providerValidationRules.setDistance("30");
		providerValidationRules.setPrimarySpecialties("Internal Medicine,Family Practice,Pediatric,General Practice,Geriatric,Nurse Practice");
		providerValidationRules.setProviderTiers("1");
		providerValidationRules.setRolloverFlag("Y");

		return providerValidationRules;
	}

	private Provider getValidPCP() {

		Provider pcp = new Provider();
		pcp.setProvPcpId("Z0Z017");
		pcp.setRgnlNtwkId("CC0D");

		return pcp;
	}
}