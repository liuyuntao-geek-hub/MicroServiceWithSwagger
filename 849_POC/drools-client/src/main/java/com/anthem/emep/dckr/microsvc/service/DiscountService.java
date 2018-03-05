package com.anthem.emep.dckr.microsvc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiscountService {
	
	@Autowired
	public RestTemplate restTemplate;
	
	@Value("${droolsengine.url}")
	private String droolsEngineURL;
	
	public String getDetails(Integer key){
		return restTemplate.getForObject(droolsEngineURL+key, String.class);
	}
}
