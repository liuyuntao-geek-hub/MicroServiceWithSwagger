package com.anthem.hca.smartpcp.drools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.drools.preprocessor.RulesMatrix;
import com.anthem.hca.smartpcp.drools.preprocessor.RulesPreprocessor;
import com.anthem.hca.smartpcp.drools.preprocessor.RulesUpdater;
import com.anthem.hca.smartpcp.drools.rules.AbstractRules;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.RulesFactory;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.service.OperationAuditFlowService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The MDOProviderValidationService class is the Service Layer implementation of
 * Provider Validation Rules for MDO. It invokes the DroolsService to fire the
 * specific Rules and returns a Rules object containing all the parameters for MDO Provider Validation.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.6
 */

@Service
@RefreshScope
public class MDOProviderValidationService implements IRulesService {

	@Autowired
	private DroolsService droolsService;

	@Autowired
	private RulesPreprocessor preProcessor;
	
	@Autowired
	private OperationAuditFlowService operationAuditFlowService;
	
	@Value("#{new Integer('${debugging.approach}')}")
	private int approach;

	/**
	 * This method takes a Member payload as the parameter and runs the MDO
	 * Provider Validation Rules on the payload by invoking Drools Rules Engine.
	 * It returns the updated Provider Validation Rules to the caller.
	 * 
	 * @param  payload                 The Member payload
	 * @return                         The Updated Provider Validation Rules
	 * @throws DroolsParseException    When Parsing error found in Drools Rule files
	 * @throws JsonProcessingException When problem found while converting Transaction payload to JSON String
	 * @see    AbstractRules
	 * @see    ProviderValidationRules
	 * @see    RulesFactory
	 * @see    AgendaGroup
	 * @see    Member
	 */
	@Override
	public ProviderValidationRules getRules(Member payload) throws DroolsParseException, JsonProcessingException {
		AbstractRules rules = RulesFactory.createRule(AgendaGroup.MDO_PROVIDER_VALIDATION);	// Create a Rule of type MDO Provider Validation
		rules.setMarketParams(payload);	// Initialize the Rules from the Input Payload
		RulesMatrix mat = preProcessor.getData().get(rules.getAgendaGroup().getValue());	// Get the Excel matrix for this Rule type
		new RulesUpdater(mat).updateRule(rules);	// Update the Rules (if required) by looking into the cached Matrix

		if (approach == 1) {
			droolsService.fireRulesSynchronized(rules, false);	// Fire Drools with active Fallback
		}
		else if (approach == 2) {
			droolsService.fireRulesSessionPerRequest(rules, false);	// Fire Drools with active Fallback
		}

		operationAuditFlowService.insertOperationFlowDrools(AgendaGroup.MDO_PROVIDER_VALIDATION.getValue());
		
		rules.setAgendaGroup(AgendaGroup.INVOCATION_SYSTEM_MAPPING);	// Create a Rule of type Invocation Mapping
		rules.setMarketParams(payload);	// Initialize the Rules from the Input Payload

		if (approach == 1) {
			droolsService.fireRulesSynchronized(rules, true);	// Fire Drools without Fallback
		}
		else if (approach == 2) {
			droolsService.fireRulesSessionPerRequest(rules, true);	// Fire Drools without Fallback
		}
		
		operationAuditFlowService.insertOperationFlowDrools(AgendaGroup.INVOCATION_SYSTEM_MAPPING.getValue());
		
		return (ProviderValidationRules)rules;
	}

	/**
	 * This method creates a HttpHeaders Bean.
	 * 
	 * @param  None
	 * @return Http Headers
	 * @see    HttpHeaders
	 * @see    MediaType
	 */
	@Bean
	public HttpHeaders headers() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

}
