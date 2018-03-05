package com.anthem.emep.dckr.microsvc.service;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NominatimService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${nominatim.url}")
	private String nominatimURL;

	@Value("${aerial.distance.url}")
	private String aerialDistanceURL;

	public Object getGeocodes(String houseNo, String street, String city, String state, String pincode)
			throws Exception {
		String queryString = houseNo + " " + street + " " + city + " " + state + " " + pincode;
		String url = nominatimURL + URLEncoder.encode(queryString, "UTF-8") + "&format=json&ploygon=1&addressdetails=1";
		ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);

		JsonNode arrNode = new ObjectMapper().readTree(res.getBody());

		if (!arrNode.isNull()) {

			double lat = Double.parseDouble(arrNode.get(0).get("lat").toString().replace("\"", ""));
			double lon = Double.parseDouble(arrNode.get(0).get("lon").toString().replace("\"", ""));

			String aerialUrl = aerialDistanceURL + lat + "&lon=" + lon;
			ResponseEntity<String> response = restTemplate.getForEntity(aerialUrl, String.class);
			return response;
		}

		return res;

	}

}
