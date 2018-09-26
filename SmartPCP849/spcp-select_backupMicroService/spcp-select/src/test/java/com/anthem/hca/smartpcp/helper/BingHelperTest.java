package com.anthem.hca.smartpcp.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BingHelperTest {

	@InjectMocks
	private BingHelper bingHelper;
	@Spy
	private ObjectMapper mapper;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPrepareDynamicURL() {
		String bingResponse = bingHelper.prepareDynamicURL(createMember().getAddress(), "url", "key");
		assertNotNull(bingResponse);
		assertNotSame(bingResponse, "");
	}

	@Test
	public void testRetrieveGeocode() throws IOException {
		String bingResponse = "{\"authenticationResultCode\": \"ValidCredentials\",\"copyright\": \"Copyright © 2018 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.\",\"resourceSets\": [{\"estimatedTotal\": 1,\"resources\": [{\"bbox\": [42.4055762204868, -71.2450287107871, 42.4133016556282, -71.2310786515669],\"name\": \"3301 Stearns Hill Rd, Waltham, MA 02451\",\"point\": {\"type\": \"Point\",\"coordinates\": [42.4094389380575, -71.238053681177]},\"address\": {\"addressLine\": \"3301 Stearns Hill Rd\",\"adminDistrict\": \"MA\",\"adminDistrict2\": \"Middlesex County\",\"countryRegion\": \"United States\",\"formattedAddress\": \"3301 Stearns Hill Rd, Waltham, MA 02451\",\"locality\": \"Waltham\",\"postalCode\": \"02451\"},\"confidence\": \"High\",\"entityType\": \"Address\",\"geocodePoints\": [{\"type\": \"Point\",\"coordinates\": [42.4094389380575, -71.238053681177],\"calculationMethod\": \"InterpolationOffset\",\"usageTypes\": [\"Display\"]}, {\"type\": \"Point\",\"coordinates\": [42.4094607214689, -71.2381069589736],\"calculationMethod\": \"Interpolation\",\"usageTypes\": [\"Route\"]}],\"matchCodes\": [\"Good\"]}]}],\"statusCode\": 200,\"statusDescription\": \"OK\",\"traceId\": \"8df64f16c4b8428590b93b0b6012d719|CH12F221B8|7.7.0.0|Ref A: F7EEBA06DFF9437F8E39666AE08B52A6 Ref B: CH1EDGE0121 Ref C: 2018-04-23T07:38:37Z\"}";
		Member out = bingHelper.retrieveGeocode(bingResponse, createMember());
		assertEquals(42.4094389380575, out.getAddress().getLatitude(), 0.01f);
		assertEquals(-71.2310786515669, out.getAddress().getLongitude(), 0.01f);
	}

	public Member createMember() {
		
		Member member = new Member();
		Address add = new Address();
		add.setAddressLine1("street");
		add.setAddressLine2("Street 2");
		add.setState("CA");
		add.setCity("Sacramento");
		add.setZipCode("1234");
		member.setAddress(add);

		return member;
	}

}
