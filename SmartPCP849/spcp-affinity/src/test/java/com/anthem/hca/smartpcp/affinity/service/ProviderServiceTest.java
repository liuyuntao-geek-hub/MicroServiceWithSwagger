package com.anthem.hca.smartpcp.affinity.service;

import java.io.IOException;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.BingOutputPayload;
import com.anthem.hca.smartpcp.affinity.model.PcpIdWithRank;
import com.anthem.hca.smartpcp.affinity.model.PcpidListOutputPayload;
import com.anthem.hca.smartpcp.affinity.repo.MemberRepo;
import com.anthem.hca.smartpcp.affinity.repo.ProviderRepo;
import com.anthem.hca.smartpcp.affinity.rest.RestClientService;
import com.anthem.hca.smartpcp.common.am.vo.Address;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;


/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		ProviderServiceTest is used to test the ProviderService
 *  
 * @author AF65409 
 */
@RunWith(value = SpringRunner.class)
public class ProviderServiceTest {

	@InjectMocks
	ProviderService providerService;
	@Mock
	private RestClientService restClientService;
	@Mock
	private MemberRepo memRepo;
	@Mock
	private ProviderRepo providerRepo;

	@Before 
	public void setupMock() { 

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getProviderPayloadTest() {

		Member member = getMemberInfo();

		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Set<String> pcpIdSet = new HashSet<>();
		pcpIdSet.add("Z0Z017");
		pcpIdSet.add("Z0Z213");
		pcpIdSet.add("Z0Z030");

		Mockito.when(providerRepo.getPCPInfoDtlsList(member, pcpIdSet)).thenReturn(getProviderInfoDtls());

		ObjectMapper mapper = new ObjectMapper();
		BingOutputPayload bingOutput = null;
		try {
			bingOutput = mapper.readValue("{\"authenticationResultCode\":\"ValidCredentials\",\"brandLogoUri\":\"http://dev.virtualearth.net/Branding/logo_powered_by.png\",\"copyright\":\"Copyright © 2018 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.\",\"resourceSets\":[{\"estimatedTotal\":1,\"resources\":[{\"type\":null,\"destinations\":[{\"latitude\":35.399724,\"longitude\":-119.465989},{\"latitude\":35.76907,\"longitude\":-119.24565},{\"latitude\":35.40863,\"longitude\":-119.031217}],\"errorMessage\":\"Request accepted.\",\"origins\":[{\"latitude\":35.36645,\"longitude\":-119.0098}],\"results\":[{\"destinationIndex\":0,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":44.8255833333333,\"travelDuration\":29.255},{\"destinationIndex\":1,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":52.9071666666667,\"travelDuration\":30.715},{\"destinationIndex\":2,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":7.09391666666667,\"travelDuration\":6.27166666666667}]}]}],\"statusCode\":\"200\",\"statusDescription\":\"OK\",\"traceId\":\"5bf5e7ab5c7545558d6bbd9693c2618a|CH17C4A2E1|7.7.0.0\"}", BingOutputPayload.class);
			bingOutput.setStatusCode(ErrorMessages.SUCCESS);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Mockito.when(restClientService.getDistanceMatrix(Mockito.any())).thenReturn(bingOutput);

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest1() {

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest2() {

		PcpidListOutputPayload pcpidListOutputPayload = new PcpidListOutputPayload();
		pcpidListOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		pcpidListOutputPayload.setResponseMessage(ErrorMessages.NO_PCP_IN_PROVIDER_TABLE);

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(null);

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest3() {

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(new ArrayList<PcpIdWithRank>());
		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest4() {

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Set<String> pcpIdSet = new HashSet<>();
		pcpIdSet.add("Z0Z213");
		pcpIdSet.add("Z0Z017");
		pcpIdSet.add("Z0Z030");
		Mockito.when(providerRepo.getPCPInfoDtlsList(member, pcpIdSet)).thenReturn(getProviderInfoDtls());

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest5() {

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Set<String> pcpIdSet = new HashSet<>();
		pcpIdSet.add("Z0Z213");
		pcpIdSet.add("Z0Z017");
		pcpIdSet.add("Z0Z030");

		Mockito.when(providerRepo.getPCPInfoDtlsList(member, pcpIdSet)).thenReturn(null);

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest6() {

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Set<String> pcpIdSet = new HashSet<>();
		pcpIdSet.add("Z0Z213");
		pcpIdSet.add("Z0Z017");
		pcpIdSet.add("Z0Z030");
		Mockito.when(providerRepo.getPCPInfoDtlsList(member, pcpIdSet)).thenReturn(getProviderInfoDtls());

		ObjectMapper mapper = new ObjectMapper();
		BingOutputPayload bingOutput = null;
		try {
			bingOutput = mapper.readValue("{\"authenticationResultCode\":\"ValidCredentials\",\"brandLogoUri\":\"http://dev.virtualearth.net/Branding/logo_powered_by.png\",\"copyright\":\"Copyright © 2018 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.\",\"resourceSets\":[{\"estimatedTotal\":1,\"resources\":[{\"type\":null,\"destinations\":[{\"latitude\":35.399724,\"longitude\":-119.465989},{\"latitude\":35.76907,\"longitude\":-119.24565},{\"latitude\":35.40863,\"longitude\":-119.031217}],\"errorMessage\":\"Request accepted.\",\"origins\":[{\"latitude\":35.36645,\"longitude\":-119.0098}],\"results\":[{\"destinationIndex\":0,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":44.8255833333333,\"travelDuration\":29.255},{\"destinationIndex\":1,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":52.9071666666667,\"travelDuration\":30.715},{\"destinationIndex\":2,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":7.09391666666667,\"travelDuration\":6.27166666666667}]}]}],\"statusCode\":\"200\",\"statusDescription\":\"OK\",\"traceId\":\"5bf5e7ab5c7545558d6bbd9693c2618a|CH17C4A2E1|7.7.0.0\"}", BingOutputPayload.class);
			bingOutput.setStatusCode(ErrorMessages.SUCCESS);
		} catch (IOException e) {

			e.printStackTrace();
		}

		Mockito.when(restClientService.getDistanceMatrix(Mockito.any())).thenReturn(bingOutput);

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest7() {

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Set<String> pcpIdSet = new HashSet<>();
		pcpIdSet.add("Z0Z213");
		pcpIdSet.add("Z0Z017");
		pcpIdSet.add("Z0Z030");
		Mockito.when(providerRepo.getPCPInfoDtlsList(member, pcpIdSet)).thenReturn(getProviderInfoDtls());

		Mockito.when(restClientService.getDistanceMatrix(Mockito.any())).thenReturn(null);

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest8() {

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Set<String> pcpIdSet = new HashSet<>();
		pcpIdSet.add("Z0Z213");
		pcpIdSet.add("Z0Z017");
		pcpIdSet.add("Z0Z030");
		Mockito.when(providerRepo.getPCPInfoDtlsList(member, pcpIdSet)).thenReturn(getProviderInfoDtls());

		ObjectMapper mapper = new ObjectMapper();
		BingOutputPayload bingOutput = null;
		try {
			bingOutput = mapper.readValue("{\"authenticationResultCode\":\"ValidCredentials\",\"brandLogoUri\":\"http://dev.virtualearth.net/Branding/logo_powered_by.png\",\"copyright\":\"Copyright © 2018 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.\",\"resourceSets\":[{\"estimatedTotal\":1,\"resources\":[{\"type\":null,\"destinations\":[{\"latitude\":35.399724,\"longitude\":-119.465989},{\"latitude\":35.76907,\"longitude\":-119.24565},{\"latitude\":35.40863,\"longitude\":-119.031217}],\"errorMessage\":\"Request accepted.\",\"origins\":[{\"latitude\":35.36645,\"longitude\":-119.0098}],\"results\":[{\"destinationIndex\":0,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":44.8255833333333,\"travelDuration\":29.255},{\"destinationIndex\":1,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":52.9071666666667,\"travelDuration\":30.715},{\"destinationIndex\":2,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":7.09391666666667,\"travelDuration\":6.27166666666667}]}]}],\"statusCode\":\"200\",\"statusDescription\":\"OK\",\"traceId\":\"5bf5e7ab5c7545558d6bbd9693c2618a|CH17C4A2E1|7.7.0.0\"}", BingOutputPayload.class);
			bingOutput.setStatusCode(ErrorMessages.SUCCESS);
			bingOutput.setResourceSets(null);
		} catch (IOException e) {

			e.printStackTrace();
		}

		Mockito.when(restClientService.getDistanceMatrix(Mockito.any())).thenReturn(bingOutput);

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest9() {

		Member member = getMemberInfo();

		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Mockito.when(providerRepo.getPCPInfoDtlsList(member, null)).thenReturn(null);

		Mockito.when(restClientService.getDistanceMatrix(Mockito.any())).thenReturn(null);

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest10() {

		Member member = getMemberInfo();

		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Mockito.when(providerRepo.getPCPInfoDtlsList(member, new HashSet<String>())).thenReturn(null);

		Mockito.when(restClientService.getDistanceMatrix(Mockito.any())).thenReturn(null);

		providerService.getProviderPayload(member);
	}

	@Test
	public void getProviderPayloadExceptionTest11() {

		Member member = getMemberInfo();
		Mockito.when(memRepo.getPCPIdWithRankList(member)).thenReturn(getpcpidListOutputPayload().getPcpIdWithRankList());

		Set<String> pcpIdSet = new HashSet<>();
		pcpIdSet.add("Z0Z213");
		pcpIdSet.add("Z0Z017");
		pcpIdSet.add("Z0Z030");
		Mockito.when(providerRepo.getPCPInfoDtlsList(member, pcpIdSet)).thenReturn(getProviderInfoDtls());

		ObjectMapper mapper = new ObjectMapper();
		BingOutputPayload bingOutput = null;
		try {
			bingOutput = mapper.readValue("{\"authenticationResultCode\":\"ValidCredentials\",\"brandLogoUri\":\"http://dev.virtualearth.net/Branding/logo_powered_by.png\",\"copyright\":\"Copyright © 2018 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.\",\"resourceSets\":[{\"estimatedTotal\":1,\"resources\":[{\"type\":null,\"destinations\":[{\"latitude\":35.399724,\"longitude\":-119.465989},{\"latitude\":35.76907,\"longitude\":-119.24565},{\"latitude\":35.40863,\"longitude\":-119.031217}],\"errorMessage\":\"Request accepted.\",\"origins\":[{\"latitude\":35.36645,\"longitude\":-119.0098}],\"results\":[{\"destinationIndex\":0,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":44.8255833333333,\"travelDuration\":29.255},{\"destinationIndex\":1,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":52.9071666666667,\"travelDuration\":30.715},{\"destinationIndex\":2,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":7.09391666666667,\"travelDuration\":6.27166666666667}]}]}],\"statusCode\":\"200\",\"statusDescription\":\"OK\",\"traceId\":\"5bf5e7ab5c7545558d6bbd9693c2618a|CH17C4A2E1|7.7.0.0\"}", BingOutputPayload.class);
			bingOutput.setStatusCode(ResponseCodes.OTHER_EXCEPTIONS);
		} catch (IOException e) {

			e.printStackTrace();
		}

		Mockito.when(restClientService.getDistanceMatrix(Mockito.any())).thenReturn(bingOutput);

		providerService.getProviderPayload(member);
	}

	private List<PCP> getProviderInfoDtls() {

		List<PCP> pcpInfoDtlsList = new ArrayList<>();

		PCP pcpInfoDtlsA = new PCP();
		pcpInfoDtlsA.setProvPcpId("Z0Z017");
		pcpInfoDtlsA.setPcpRankgId("2");
		pcpInfoDtlsA.setLatdCordntNbr(35.408630);
		pcpInfoDtlsA.setLngtdCordntNbr(-119.031217);
		pcpInfoDtlsA.setSpcltyDesc("Family Practice");
		pcpInfoDtlsA.setRgnlNtwkId("CC0D");
		pcpInfoDtlsA.setGrpgRltdPadrsEfctvDt(new Date(2017-01-01));
		pcpInfoDtlsA.setGrpgRltdPadrsTrmntnDt(new Date(9999-12-31));
		pcpInfoDtlsA.setMaxMbrCnt(2500);
		pcpInfoDtlsA.setCurntMbrCnt(0);
		pcpInfoDtlsA.setAccNewPatientFlag("Y");
		pcpInfoDtlsA.setAerialDistance(7.09391666666667);

		pcpInfoDtlsList.add(pcpInfoDtlsA);

		PCP pcpInfoDtlsB = new PCP();
		pcpInfoDtlsB.setProvPcpId("Z0Z213");
		pcpInfoDtlsB.setPcpRankgId("3");
		pcpInfoDtlsB.setLatdCordntNbr(35.769070);
		pcpInfoDtlsB.setLngtdCordntNbr(-119.245650);
		pcpInfoDtlsB.setSpcltyDesc("Pediatric");
		pcpInfoDtlsB.setRgnlNtwkId("CC0D");
		pcpInfoDtlsB.setGrpgRltdPadrsEfctvDt(new Date(2017-05-19));
		pcpInfoDtlsB.setGrpgRltdPadrsTrmntnDt(new Date(9999-12-31));
		pcpInfoDtlsB.setMaxMbrCnt(2500);
		pcpInfoDtlsB.setCurntMbrCnt(0);
		pcpInfoDtlsB.setAccNewPatientFlag("Y");
		pcpInfoDtlsB.setAerialDistance(52.9071666666667);

		pcpInfoDtlsList.add(pcpInfoDtlsB);

		PCP pcpInfoDtlsC = new PCP();
		pcpInfoDtlsC.setProvPcpId("Z0Z030");
		pcpInfoDtlsC.setPcpRankgId("4");
		pcpInfoDtlsC.setLatdCordntNbr(35.399724);
		pcpInfoDtlsC.setLngtdCordntNbr(-119.465989);
		pcpInfoDtlsC.setSpcltyDesc("Family Practice");
		pcpInfoDtlsC.setRgnlNtwkId("CC0D");
		pcpInfoDtlsC.setGrpgRltdPadrsEfctvDt(new Date(2017-01-01));
		pcpInfoDtlsC.setGrpgRltdPadrsTrmntnDt(new Date(9999-12-31));
		pcpInfoDtlsC.setMaxMbrCnt(2500);
		pcpInfoDtlsC.setCurntMbrCnt(0);
		pcpInfoDtlsC.setAccNewPatientFlag("Y");
		pcpInfoDtlsC.setAerialDistance(44.8255833333333);

		pcpInfoDtlsList.add(pcpInfoDtlsC);

		return pcpInfoDtlsList;
	}

	private PcpidListOutputPayload getpcpidListOutputPayload() {

		PcpidListOutputPayload pcpidListOutputPayload = new PcpidListOutputPayload();

		List<PcpIdWithRank> pcpIdWithRankList = new ArrayList<PcpIdWithRank>();

		PcpIdWithRank pcpIdWithRankA = new PcpIdWithRank();
		pcpIdWithRankA.setMcid("369709015");
		pcpIdWithRankA.setPcpId("Z0Z017");
		pcpIdWithRankA.setTin("954337143"); 
		pcpIdWithRankA.setNpi("1750312294");
		pcpIdWithRankA.setPcpRank("2");
		pcpIdWithRankList.add(pcpIdWithRankA);

		PcpIdWithRank pcpIdWithRankB = new PcpIdWithRank();
		pcpIdWithRankB.setMcid("369709015");
		pcpIdWithRankB.setPcpId("Z0Z213");
		pcpIdWithRankB.setTin("954337143"); 
		pcpIdWithRankB.setNpi("1104935329");
		pcpIdWithRankB.setPcpRank("3");
		pcpIdWithRankList.add(pcpIdWithRankB);

		PcpIdWithRank pcpIdWithRankC = new PcpIdWithRank();
		pcpIdWithRankB.setMcid("369709015");
		pcpIdWithRankC.setPcpId("Z0Z030");
		pcpIdWithRankC.setTin("954337143"); 
		pcpIdWithRankC.setNpi("1699777896");
		pcpIdWithRankC.setPcpRank("4");
		pcpIdWithRankList.add(pcpIdWithRankC);

		pcpidListOutputPayload.setResponseCode(ResponseCodes.SUCCESS);
		pcpidListOutputPayload.setResponseMessage(ErrorMessages.SUCCESS);
		pcpidListOutputPayload.setPcpIdWithRankList(pcpIdWithRankList);

		return pcpidListOutputPayload;
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