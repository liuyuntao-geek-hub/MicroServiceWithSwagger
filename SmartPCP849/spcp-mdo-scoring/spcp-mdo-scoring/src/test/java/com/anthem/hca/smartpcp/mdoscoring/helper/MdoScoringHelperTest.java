/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.helper;


import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.anthem.hca.smartpcp.mdoscoring.InputOutputPayloadDtls;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCPAssignmentFlow;

@RunWith(SpringRunner.class) 
public class MdoScoringHelperTest {
	
	
	MDOScoreHelper mdoHelper=new MDOScoreHelper();
	
	
	InputOutputPayloadDtls inputDtls=new InputOutputPayloadDtls();
	@Test
	public void getRequestTest() throws Exception {
	InputPayloadInfo inputPayloadInfos = inputDtls.createInputPayload();

	List<PCP> finalPayload= mdoHelper.getAllPCPScore(inputPayloadInfos.getPcp(),inputPayloadInfos.getRules(),
			inputPayloadInfos.getMember(), createPCPAssignmentFlow());
	assertNotNull(finalPayload);
	
	
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
