package com.anthem.emep.dckr.microsvc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.emep.dckr.microsvc.model.BingResponse;

@Service
public class BingService {
	
	 @Autowired
     private RestTemplate restTemplate;
	 
	 @Value("${bing.url}")
	 private String bingURL;
	 
	 @Value("${bing.key}")
	 private String bingKey;
	
	public Object getDrivingDistance(String origin,String destination){
		
		String url = bingURL + "?origins=" + origin + "&destinations=" + destination + "&travelMode=driving&key="
				+ bingKey;
		BingResponse b = restTemplate.getForObject(url, BingResponse.class);

		return b.getResourceSets().get(0).getResources().get(0).getResults();
	}

}
