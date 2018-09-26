/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.service;

import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.test.context.junit4.SpringRunner;

import com.anthem.hca.smartpcp.mdoscoring.helper.MDOScoreHelper;
import com.anthem.hca.smartpcp.mdoscoring.service.MDOScoreService;
import com.anthem.hca.smartpcp.mdoscoring.service.RestClientService;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.Member;
import com.anthem.hca.smartpcp.mdoscoring.vo.Address;
import com.anthem.hca.smartpcp.mdoscoring.vo.OutputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCPAssignmentFlow;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MDOScoreService.class, properties = { "spring.cloud.config.enabled:false" })
public class MdoScoringServiceTest {

	@InjectMocks
	MDOScoreService mdoScoreService;

	@MockBean
	MDOScoreHelper mdoHelper;

	@MockBean
	private RestClientService asyncClientService;

	@MockBean
	private Tracer tracer;

	@Test
	public void getServiceNotNullTest() throws Exception {

		InputPayloadInfo inputPayloadInfos = createInputPayload();
		PCP pcp = createPCP();
		Mockito.when(mdoHelper.getHighestScorePCP(
						inputPayloadInfos.getPcp(),
						inputPayloadInfos.getRules(),
						inputPayloadInfos.getMember(), 
						createPCPAssignmentFlow()))
		       .thenReturn(pcp);
		assertNotNull(pcp.getMdoScore());
	}

	public InputPayloadInfo createInputPayload() {
		InputPayloadInfo inputPayload = new InputPayloadInfo();
		Member member = new Member();
		Address adrs = new Address();
		adrs.setLatitude(23.45);
		adrs.setLongitude(123.45);
		member.setAddress(adrs);
		inputPayload.setMember(member);
		inputPayload.setPcp(createPCPList());
		Rules rules = new Rules();
		inputPayload.setRules(rules);
		return inputPayload;
	}

	public List<PCP> createPCPList() {

		PCP pcp1 = new PCP();
		PCP pcp2 = new PCP();
		PCP pcp3 = new PCP();

		pcp1.setProvPcpId("1");
		pcp2.setProvPcpId("2");
		pcp3.setProvPcpId("3");

		pcp1.setPcpLastNm("Chang");
		pcp2.setPcpLastNm("Brown");
		pcp3.setPcpLastNm("Grey");

		pcp1.setLatdCordntNbr(44.32);
		pcp2.setLatdCordntNbr(45.54);
		pcp3.setLatdCordntNbr(42.67);

		pcp1.setLngtdCordntNbr(77.43);
		pcp2.setLngtdCordntNbr(76.54);
		pcp3.setLngtdCordntNbr(111.67);

		pcp1.setDrivingDistance(11.23);
		pcp2.setDrivingDistance(11.22);
		pcp3.setDrivingDistance(9.98);

		pcp1.setVbpFlag("Y");
		pcp2.setVbpFlag("N");
		pcp3.setVbpFlag("Y");

		pcp1.setPanelCapacity(23.4f);
		pcp2.setPanelCapacity(23.4f);
		pcp3.setPanelCapacity(43.8f);

		List<PCP> pcpList = new ArrayList<>();
		pcpList.add(pcp1);
		pcpList.add(pcp2);
		pcpList.add(pcp3);

		return pcpList;

	}

	public PCP createPCP() {
		PCP pcp = new PCP();
		pcp.setProvPcpId("3");
		pcp.setDrivingDistance(12.5);
		pcp.setMdoScore(100);
		pcp.setPcpRankgId("1");;
		pcp.setRgnlNtwkId("");
		return pcp;
	}

	public OutputPayloadInfo createOutputPayload() {
		OutputPayloadInfo outputPayload = new OutputPayloadInfo();
		outputPayload.setProvPcpId("3");
		outputPayload.setDrivingDistance(12.5);
		outputPayload.setMdoScore(100);
		outputPayload.setRgnlNtwkId("");
		outputPayload.setResponseCode("200");
		outputPayload.setResponseMessage("SUCCESS");
		return outputPayload;
	}
	
	public PCPAssignmentFlow createPCPAssignmentFlow() {
		PCPAssignmentFlow pcp = new PCPAssignmentFlow();
		List<PCP> pcps = createPCPList();
		pcp.setTraceId("926b810659ab7eea");
		pcp.setCreatedTime(new Timestamp(System.currentTimeMillis()));
		pcp.setDrivingDistScoredPcps(pcps);
		pcp.setScoredSortedPoolPcps(pcps);
		pcp.setSelectedPcp(pcps.get(0));
		pcp.setTieOnDrivingPcps(pcps);
		pcp.setTieOnLastNamePcps(pcps);
		pcp.setTieOnPanelCapacityPcps(pcps);
		pcp.setTieOnVBPPcps(pcps);
		return pcp;
	}

}
