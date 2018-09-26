/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.utility;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCPAssignmentFlow;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TieBreakerUtil.class, properties = { "spring.cloud.config.enabled:false" })

public class TieBreakerUtilTest {

	private static final Logger logger = LogManager.getLogger(TieBreakerUtilTest.class);
	TieBreakerUtil utilObject = new TieBreakerUtil();

	@Before
	public void setup() {
		logger.info("Testing TieBreakerUtil");
	}

	@Test
	public void testGetNearestPCP() throws Exception {

		List<PCP> pcpList = createPCPList();
		List<PCP> resultPcpList = utilObject.getNearestPCP(pcpList, createPCPAssignmentFlow());

		assertEquals(1, resultPcpList.size());
		Assert.assertEquals(9.98f, resultPcpList.get(0).getDrivingDistance(), 0.09);
		assertEquals("3", resultPcpList.get(0).getProvPcpId());

	}

	@Test
	public void testGetVbpPCP() throws Exception {

		List<PCP> pcpList = createPCPList();
		List<PCP> resultPcpList = utilObject.getVbpPcp(pcpList, createPCPAssignmentFlow());

		assertEquals(2, resultPcpList.size());
		assertEquals("1", resultPcpList.get(0).getProvPcpId());
		assertEquals("3", resultPcpList.get(1).getProvPcpId());

	}

	@Test
	public void testGetLeastAssignedPCP() throws Exception {

		List<PCP> pcpList = createPCPList();
		List<PCP> resultPcpList = utilObject.getleastAssignedPCP(pcpList,  createPCPAssignmentFlow());

		assertEquals(2, resultPcpList.size());
		assertEquals("1", resultPcpList.get(0).getProvPcpId());
		assertEquals("2", resultPcpList.get(1).getProvPcpId());
		Assert.assertEquals(23.4f, resultPcpList.get(0).getPanelCapacity(), 0.00);
		Assert.assertEquals(23.4f, resultPcpList.get(1).getPanelCapacity(), 0.00);

	}

	@Test
	public void testGetAlphabeticallySortedPCPList() throws Exception {

		List<PCP> pcpList = createPCPList();
		List<PCP> resultPcpList = utilObject.getAlphabeticallySortedPCPList(pcpList, createPCPAssignmentFlow());

		assertEquals(3, resultPcpList.size());
		assertEquals("2", resultPcpList.get(0).getProvPcpId());
		assertEquals("1", resultPcpList.get(1).getProvPcpId());
		assertEquals("3", resultPcpList.get(2).getProvPcpId());

		assertEquals("Brown", resultPcpList.get(0).getPcpLastNm());
		assertEquals("Chang", resultPcpList.get(1).getPcpLastNm());
		assertEquals("Grey", resultPcpList.get(2).getPcpLastNm());

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
