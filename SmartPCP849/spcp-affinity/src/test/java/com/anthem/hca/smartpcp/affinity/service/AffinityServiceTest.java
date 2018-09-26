package com.anthem.hca.smartpcp.affinity.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.sleuth.Tracer;

import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.ProviderPayload;
import com.anthem.hca.smartpcp.affinity.model.ProviderValidationOutPayload;
import com.anthem.hca.smartpcp.common.am.vo.Address;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
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
	private Tracer tracer;
	@Mock
	private ProviderService providerService;
	@Mock
	private RestClientPayloadService restClientPayloadService;
	@Before 
	public void setupMock() { 

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getAffinityOutPayloadTest() throws JsonProcessingException {

		Member member = getMemberInfo();
		List<PCP> pcpList = getPcpList();

		ProviderPayload providerPayload = new ProviderPayload();
		providerPayload.setProviderPayloadList(pcpList);
		providerPayload.setResponseCode(ResponseCodes.SUCCESS);
		providerPayload.setResponseMessage(ErrorMessages.SUCCESS);

		Mockito.when(providerService.getProviderPayload(member)).thenReturn(providerPayload);

		ProviderValidationOutPayload providerValidationOutPayload  = new ProviderValidationOutPayload();
		providerValidationOutPayload.setPcpInfo(getPcp());
		providerValidationOutPayload.setResponseCode(ResponseCodes.SUCCESS);
		providerValidationOutPayload.setResponseMessage(ErrorMessages.SUCCESS);
		
		Mockito.when(restClientPayloadService.getProviderValidationOutputPayload(member, pcpList)).thenReturn(providerValidationOutPayload);

		affinityService.getAffinityOutPayload(member);
	}
	
	@Test
	public void getAffinityOutPayloadExceptionTest1() throws JsonProcessingException {

		Member member = getMemberInfo();

		ProviderPayload providerPayload = new ProviderPayload();
		providerPayload.setProviderPayloadList(null);
		providerPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		providerPayload.setResponseMessage(ErrorMessages.NO_PCP_IN_PROVIDER_TABLE);
		
		Mockito.when(providerService.getProviderPayload(member)).thenReturn(providerPayload);

		affinityService.getAffinityOutPayload(member);
	}
	
	@Test
	public void getAffinityOutPayloadExceptionTest3() throws JsonProcessingException {

		Member member = getMemberInfo();
		List<PCP> pcpList = getPcpList();

		ProviderPayload providerPayload = new ProviderPayload();
		providerPayload.setProviderPayloadList(pcpList);
		providerPayload.setResponseCode(ResponseCodes.SUCCESS);
		providerPayload.setResponseMessage(ErrorMessages.SUCCESS);
		
		Mockito.when(providerService.getProviderPayload(member)).thenReturn(providerPayload);

		ProviderValidationOutPayload providerValidationOutPayload  = new ProviderValidationOutPayload();
		providerValidationOutPayload.setPcpInfo(null);
		providerValidationOutPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		providerValidationOutPayload.setResponseMessage(ErrorMessages.NO_VALID_PCP_IDENTIFIED);
		
		Mockito.when(restClientPayloadService.getProviderValidationOutputPayload(member, pcpList)).thenReturn(providerValidationOutPayload);

		affinityService.getAffinityOutPayload(member);
	}

	private List<PCP> getPcpList() {
		
		List<PCP> pcpList = new ArrayList<>();
		
		PCP pcpA = new PCP();
		pcpA.setProvPcpId("Z0Z017");
		pcpA.setAerialDistance(7.09);
		pcpA.setDrivingDistance(7.09);
		pcpA.setRgnlNtwkId("CC0D");
		pcpList.add(pcpA);
		
		PCP pcpB = new PCP();
		pcpB.setProvPcpId("Z0Z030");
		pcpB.setAerialDistance(44.83);
		pcpB.setDrivingDistance(44.83);
		pcpB.setRgnlNtwkId("CC0D");
		pcpList.add(pcpB);

		PCP pcpC = new PCP();
		pcpC.setProvPcpId("Z0Z213");
		pcpC.setAerialDistance(52.91);
		pcpC.setDrivingDistance(52.91);
		pcpC.setRgnlNtwkId("CC0D");
		pcpList.add(pcpC);

		return pcpList;
	}

	private PCP getPcp() {
		PCP pcp = new PCP();
		pcp.setProvPcpId("Z0Z017");
		pcp.setAerialDistance(7.09);
		pcp.setDrivingDistance(7.09);
		pcp.setRgnlNtwkId("CC0D");

		return pcp;
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
}