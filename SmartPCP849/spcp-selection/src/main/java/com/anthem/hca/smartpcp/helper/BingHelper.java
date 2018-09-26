package com.anthem.hca.smartpcp.helper;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Helper class to prepare dynamic url for Bing and parse its response.
 * 
 * 
 * @author AF71111
 */
public class BingHelper {

	@Autowired
	private ObjectMapper mapper;

	/**
	 * @param address
	 * @return String
	 */
	public String prepareDynamicURL(Address address, String bingUrl, String bingKey) {

		String street = address.getAddressLine1();
		StringBuilder sb = new StringBuilder();
		sb.append(bingUrl).append(Constants.QUESTION_MARK);
		sb.append(Constants.BING_PARAM_COUNTRY).append(Constants.EQUAL).append(Constants.COUNTRY_US)
				.append(Constants.AND);
		sb.append(Constants.BING_PARAM_STATE).append(Constants.EQUAL).append(address.getState()).append(Constants.AND);
		sb.append(Constants.BING_PARAM_CITY).append(Constants.EQUAL).append(address.getCity()).append(Constants.AND);
		sb.append(Constants.BING_PARAM_ZIP).append(Constants.EQUAL).append(address.getZipCode()).append(Constants.AND);
		if (StringUtils.isNotBlank(address.getAddressLine2())) {
			street = street.concat(" " + address.getAddressLine2());
		}
		sb.append(Constants.BING_PARAM_ADDRESS).append(Constants.EQUAL)
				.append(street.replaceAll(Constants.REGEX_SPECIAL_CHAR, Constants.EMPTY_STRING)).append(Constants.AND);
		sb.append(Constants.BING_PARAM_KEY).append(Constants.EQUAL).append(bingKey).append(Constants.AND);
		sb.append(Constants.BING_PARAM_OUTPUT).append(Constants.EQUAL).append(Constants.BING_OUTPUT_JSON);
		return sb.toString();
	}

	public Member retrieveGeocode(String bingResponse, Member member) throws IOException {
		if (null != bingResponse) {
			JsonNode arrNode = mapper.readTree(bingResponse);
			if (null != arrNode && arrNode.get(Constants.BING_RESOURCE_SETS).size() > 0
					&& arrNode.get(Constants.BING_RESOURCE_SETS).get(0).get(Constants.BING_RESOURCES).size() > 0) {
				Address memberAddress = member.getAddress();
				JsonNode node = arrNode.get(Constants.BING_RESOURCE_SETS).get(0).get(Constants.BING_RESOURCES).get(0)
						.get(Constants.BING_POINT).get(Constants.BING_COORDINATES);
				memberAddress.setLatitude(node.get(0).doubleValue());
				memberAddress.setLongitude(node.get(1).doubleValue());
			}
		}
		return member;
	}

}
