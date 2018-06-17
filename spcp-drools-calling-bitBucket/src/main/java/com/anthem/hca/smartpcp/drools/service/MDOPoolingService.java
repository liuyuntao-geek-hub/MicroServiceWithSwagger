package com.anthem.hca.smartpcp.drools.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.anthem.hca.smartpcp.drools.io.RulesInputPayload;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.service.DroolsService;
import com.anthem.hca.smartpcp.drools.service.DroolsService.AgendaGroup;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

@Service
public class MDOPoolingService implements IRulesService {

	@Autowired
	private DroolsService droolsService;

	@Autowired
	private DroolsRestClientService clientService;

	private static final Logger logger = LogManager.getLogger(MDOPoolingService.class);

	@Override
	public MDOPoolingRules getRules(RulesInputPayload payload) throws DroolsParseException, JsonProcessingException {
		boolean fallback = droolsService.isFallbackRequired(payload);
		logger.info("Fallback Required = " + fallback);

		MDOPoolingRules rules = (MDOPoolingRules)droolsService.fireRulesFor(AgendaGroup.MDO_POOLING, payload, fallback);
		clientService.insertOperationFlow(AgendaGroup.MDO_POOLING.getValue());

		return rules;
	}

	@Bean
	public HttpHeaders headers() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

}
