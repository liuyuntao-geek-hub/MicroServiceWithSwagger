package com.anthem.hca.smartpcp.affinity.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.anthem.hca.smartpcp.affinity.model.PcpIdWithRank;
import com.anthem.hca.smartpcp.affinity.service.ProviderService;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		ProviderServiceUtilityTest is used to test the ProviderServiceUtility
 *  
 * @author AF65409 
 */
public class ProviderServiceUtilityTest {

	ProviderService utils;
	
	@Before
	public void setUp() throws Exception {
		utils = new ProviderService();
	}

	@After
	public void tearDown() throws Exception {
		utils = null;
	}

	@Test
	public void testDuplicateValuesSameMcid() throws ParseException {
		List<PcpIdWithRank> pcpIdWithRankListSameMcid = getPcpidWitRankListWithSameMcid();
		assertTrue(utils.duplicateValues(pcpIdWithRankListSameMcid));
	}
	
	@Test
	public void testDuplicateValuesDifferentMcid() throws ParseException {
		List<PcpIdWithRank> pcpIdWithRankListDifferentMcid = getPcpidWitRankListWithDifferentMcid();
		assertFalse(utils.duplicateValues(pcpIdWithRankListDifferentMcid));
	}

	private List<PcpIdWithRank> getPcpidWitRankListWithDifferentMcid() {

		List<PcpIdWithRank> pcpIdWithRankList = new ArrayList<PcpIdWithRank>();

		PcpIdWithRank pcpIdWithRankA = new PcpIdWithRank();
		pcpIdWithRankA.setMcid("36970901");
		pcpIdWithRankList.add(pcpIdWithRankA);

		PcpIdWithRank pcpIdWithRankB = new PcpIdWithRank();
		pcpIdWithRankB.setMcid("369709015");
		pcpIdWithRankList.add(pcpIdWithRankB);

		PcpIdWithRank pcpIdWithRankC = new PcpIdWithRank();
		pcpIdWithRankC.setMcid("369709015");
		pcpIdWithRankList.add(pcpIdWithRankC);

		return pcpIdWithRankList;
	}

	private List<PcpIdWithRank> getPcpidWitRankListWithSameMcid() {

		List<PcpIdWithRank> pcpIdWithRankList = new ArrayList<PcpIdWithRank>();

		PcpIdWithRank pcpIdWithRankA = new PcpIdWithRank();
		pcpIdWithRankA.setMcid("369709015");
		pcpIdWithRankList.add(pcpIdWithRankA);

		PcpIdWithRank pcpIdWithRankB = new PcpIdWithRank();
		pcpIdWithRankB.setMcid("369709015");
		pcpIdWithRankList.add(pcpIdWithRankB);

		PcpIdWithRank pcpIdWithRankC = new PcpIdWithRank();
		pcpIdWithRankC.setMcid("369709015");
		pcpIdWithRankList.add(pcpIdWithRankC);

		return pcpIdWithRankList;
	}
}