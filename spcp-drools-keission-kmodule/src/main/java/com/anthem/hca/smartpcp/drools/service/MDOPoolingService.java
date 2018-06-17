package com.anthem.hca.smartpcp.drools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.anthem.hca.smartpcp.drools.io.RulesInputPayload;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.rules.Rules;
import com.anthem.hca.smartpcp.drools.rules.RulesFactory;
import com.anthem.hca.smartpcp.drools.service.DroolsService;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

@Service
public class MDOPoolingService implements IRulesService {

	@Autowired
	private DroolsService droolsService;

	@Autowired
	private DroolsRestClientService clientService;

	@Override
	public MDOPoolingRules getRules(RulesInputPayload payload) throws DroolsParseException, JsonProcessingException {
		Rules rules = RulesFactory.createRule(AgendaGroup.MDO_POOLING);
		rules.setMarketParams(payload.getMember());
		droolsService.fireRules(rules);
		clientService.insertOperationFlow(AgendaGroup.MDO_POOLING.getValue());
		((MDOPoolingRules)rules).setFallbackForPooling();

		rules.setAgendaGroup(AgendaGroup.DUMMYPCP);
		rules.setMarketParams(payload.getMember());
		droolsService.fireRules(rules);
		clientService.insertOperationFlow(AgendaGroup.DUMMYPCP.getValue());
		((MDOPoolingRules)rules).setFallbackForDummy();

		return (MDOPoolingRules)rules;
	}

	@Bean
	public HttpHeaders headers() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

}
