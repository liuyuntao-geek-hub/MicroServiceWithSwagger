/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.anthem.hca.smartpcp.mdoscoring.service.BingRestClientService;
import com.anthem.hca.smartpcp.mdoscoring.utility.MDOScoringCalculation;
import com.anthem.hca.smartpcp.mdoscoring.vo.Address;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingInputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingOutputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.Destination;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.Member;
import com.anthem.hca.smartpcp.mdoscoring.vo.Origin;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.Resource;
import com.anthem.hca.smartpcp.mdoscoring.vo.ResourceSet;
import com.anthem.hca.smartpcp.mdoscoring.vo.Result;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MDOScoreHelper.class, properties = { "spring.cloud.config.enabled:false" })
public class MDOScoreHelperTest {

	private static final Logger logger = LogManager.getLogger(MDOScoreHelperTest.class);

	MDOScoreHelper mdoScoreHelper = new MDOScoreHelper();

	@InjectMocks
	MDOScoreHelper helper;

	@MockBean
	BingRestClientService bingRestClientService;

	@Mock
	MDOScoringCalculation mdoScoreCalculator;

	@Before
	public void setup() {
		logger.info("Testing MDOScoreHelper");
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void getDrivingDistancePointsTest() throws JsonProcessingException {
		List<PCP> pcpList = createPCPList();
		InputPayloadInfo inputPayload = createInputPayload();
		BingInputPayload bingInput = createBingInputPayload();
		BingOutputPayload bingOutput = createBingOutputPayload();
		Mockito.when(bingRestClientService.getDistanceMatrix(bingInput)).thenReturn(bingOutput);
		List<ResourceSet> distMatrixResourceSet = bingOutput.getResourceSets();
		Mockito.when(
				mdoScoreCalculator.drivingDistanceTotalScore(distMatrixResourceSet, pcpList, inputPayload.getRules()))
				.thenReturn(pcpList);
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

	public BingInputPayload createBingInputPayload() throws JsonProcessingException {
		BingInputPayload bingInput = new BingInputPayload();
		Destination destination = new Destination();
		destination.setLatitude(33.89);
		destination.setLongitude(122.89);
		List<Destination> destinations = new ArrayList<Destination>();
		destinations.add(destination);
		bingInput.setDestinations(destinations);

		Origin origin = new Origin();
		origin.setLatitude(33.89);
		origin.setLongitude(122.89);
		List<Origin> origins = new ArrayList<Origin>();
		origins.add(origin);
		bingInput.setOrigins(origins);

		bingInput.setTravelMode("driving");

		return bingInput;
	}

	public BingOutputPayload createBingOutputPayload() {
		BingOutputPayload bingOutput = new BingOutputPayload();

		bingOutput.setAuthenticationResultCode("Authentication Result Code");
		bingOutput.setBrandLogoUri("Brand Logo Uri");
		bingOutput.setCopyright("Copyright");
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
		bingOutput.setResourceSets(resourceSets);
		bingOutput.setStatusCode(200);
		bingOutput.setStatusDescription("SUCCESS");
		bingOutput.setTraceId("Trace Id");
		return bingOutput;
	}

}
