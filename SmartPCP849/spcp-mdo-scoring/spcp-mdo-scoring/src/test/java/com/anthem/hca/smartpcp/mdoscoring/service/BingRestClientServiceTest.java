/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.mdoscoring.vo.BingInputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingOutputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.Destination;
import com.anthem.hca.smartpcp.mdoscoring.vo.Origin;
import com.anthem.hca.smartpcp.mdoscoring.vo.Resource;
import com.anthem.hca.smartpcp.mdoscoring.vo.ResourceSet;
import com.anthem.hca.smartpcp.mdoscoring.vo.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BingRestClientService.class, properties = { "spring.cloud.config.enabled:false" })
public class BingRestClientServiceTest {

	@InjectMocks
	private BingRestClientService bingRestClientService;
	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private ObjectMapper mapper;

	@MockBean
	private HttpHeaders headers;

	@Value("${bing.url}")
	private String bingUrl;

	@Value("${bing.key}")
	private String bingKey;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	private static final Logger logger = LogManager.getLogger(BingRestClientServiceTest.class);

	@Test
	public void getDistanceMatrixTest() throws JsonProcessingException {

		BingInputPayload bingInput = createBingInputPayload();
		String finalBingUrl = bingUrl + "=" + bingKey;
		logger.info("Final Bing URL: " + finalBingUrl);

		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(bingInput), headers);

		BingOutputPayload response = createBingOutputPayload();
		Mockito.when(restTemplate.postForObject("null=null", request, BingOutputPayload.class)).thenReturn(response);
		BingOutputPayload bingOutput = bingRestClientService.getDistanceMatrix(bingInput);
		assertEquals(200, bingOutput.getStatusCode());
		assertEquals("SUCCESS", bingOutput.getStatusDescription());

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
