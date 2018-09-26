/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.utility;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.anthem.hca.smartpcp.mdoscoring.vo.ActionPair;
import com.anthem.hca.smartpcp.mdoscoring.vo.Destination;
import com.anthem.hca.smartpcp.mdoscoring.vo.Origin;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.Resource;
import com.anthem.hca.smartpcp.mdoscoring.vo.ResourceSet;
import com.anthem.hca.smartpcp.mdoscoring.vo.Result;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MDOScoringCalculation.class, properties = { "spring.cloud.config.enabled:false" })
public class MDOScoringCalculationTest {

	private static final Logger logger = LogManager.getLogger(MDOScoringCalculationTest.class);

	MDOScoringCalculation scoringCalculation = new MDOScoringCalculation();

	@Before
	public void setup() {
		logger.info("Testing MDOScoringCalculation");
	}

	@Test
	public void getDrivingDistanceTotalScore() {
		List<ResourceSet> resourceSets = createResourceSet();
		List<PCP> pcpList = createPCPList();
		Rules rules = createRules();

		List<PCP> outputList = scoringCalculation.drivingDistanceTotalScore(resourceSets, pcpList, rules);

		assertEquals("1", outputList.get(0).getProvPcpId());

	}

	public List<ResourceSet> createResourceSet() {
		ResourceSet resourceSet = new ResourceSet();
		resourceSet.setEstimatedTotal(100);
		Resource resource = new Resource();

		Destination destination = new Destination();
		destination.setLatitude(44.89);
		destination.setLongitude(142.89);
		List<Destination> destinations = new ArrayList<Destination>();
		destinations.add(destination);
		resource.setDestinations(destinations);
		resource.setErrorMessage("Error Message");

		Origin origin = new Origin();
		origin.setLatitude(22.89);
		origin.setLongitude(122.89);
		List<Origin> origins = new ArrayList<Origin>();
		origins.add(origin);
		resource.setOrigins(origins);

		Result result = new Result();
		result.setDestinationIndex(1);
		result.setOriginIndex(0);
		result.setTotalWalkDuration(0);
		result.setTravelDistance(1.49);
		result.setTravelDuration(2.93);

		List<Result> results = new ArrayList<>();
		results.add(result);
		resource.setResults(results);
		resource.setType("Type");
		List<Resource> resources = new ArrayList<>();
		resources.add(resource);

		resourceSet.setResources(resources);
		List<ResourceSet> resourceSets = new ArrayList<>();
		resourceSets.add(resourceSet);

		return resourceSets;
	}

	public List<PCP> createPCPList() {

		PCP pcp1 = new PCP();
		
		pcp1.setProvPcpId("1");
		

		pcp1.setPcpLastNm("Chang");
		
		pcp1.setDrivingDistance(11.23);
		

		pcp1.setVbpFlag("Y");
		
		pcp1.setPanelCapacity(23.4f);
		
		pcp1.setMdoScore(35);
		
		
		pcp1.setCurntMbrCnt(500);
		pcp1.setMaxMbrCnt(2500);
		
		List<PCP> pcpList = new ArrayList<>();
		pcpList.add(pcp1);
		

		return pcpList;

	}

	public Rules createRules() {
		Rules rules = new Rules();
		rules.setPanelCapacityPercent(23);
		List<ActionPair<String,Integer>> proximityScoreList=new ArrayList<>();
		ActionPair<String,Integer> ap=new ActionPair<>();
		ap.setKey("0-10");
		ap.setValue(0);
		proximityScoreList.add(ap);
		rules.setProximityScoreList(proximityScoreList);
		return rules;
	}
}
