/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.model.PCPTrackAudit;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RefreshScope
public class ScoringClientService {

	private static final Logger logger = LoggerFactory.getLogger(ScoringClientService.class);

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;

	
	@Value("${spcp.pcp.track.audit.url}")
	private String pcpAuditTrackPersistUrl;

	
	
	@Async
	public void persistPCPAssignmentFlow(PCPTrackAudit pcpTrackAudit) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(pcpTrackAudit),headers);
	        ResponseEntity<String> outputPayload = restTemplate.exchange(
	        		pcpAuditTrackPersistUrl,
	                HttpMethod.POST,
	                request,
	                String.class
	        );
		    logger.debug("Invoking the pcp track audit service to insert the data into pcp track audit table {}", outputPayload.getBody());
		} catch (Exception exception) {
			logger.error("Error occured while inserting into transaction table {}", exception);
		}
	}

}