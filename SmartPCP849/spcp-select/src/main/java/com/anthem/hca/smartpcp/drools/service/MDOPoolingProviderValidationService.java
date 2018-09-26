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
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.RulesFactory;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.service.OperationAuditFlowService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The MDOPoolingProviderValidationService class is the Service Layer implementation of
 * MDO Pooling and Provider Validation Rules. It invokes the DroolsService to fire the
 * specific Rules and returns a Rules object containing all the parameters for MDO Pooling
 * and Provider Validation in a single Object.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.2
 */

@Service
@RefreshScope
public class MDOPoolingProviderValidationService implements IRulesService {

	@Autowired
	private DroolsService droolsService;

	@Autowired
	private RulesPreprocessor preProcessor;
	
	@Autowired
	private OperationAuditFlowService operationAuditFlowService;
	
	@Value("#{new Integer('${debugging.approach}')}")
	private int approach;

	/**
	 * This method takes a Member payload as the parameter and runs the MDO Pooling and 
	 * Provider Validation Rules on the payload by invoking Drools Rules Engine. It returns
	 * the updated MDO Pooling and Provider Validation Rules as a single Object to the caller.
	 * 
	 * @param  payload                 The Member payload
	 * @return                         The Updated MDO Pooling and Provider Validation Rules
	 * @throws DroolsParseException    When Parsing error found in Drools Rule files
	 * @throws JsonProcessingException When problem found while converting Transaction payload to JSON String
	 * @see    AbstractRules
	 * @see    MDOPoolingProviderValidationRules
	 * @see    RulesFactory
	 * @see    AgendaGroup
	 * @see    Member
	 */
	@Override
	public MDOPoolingProviderValidationRules getRules(Member payload) throws DroolsParseException, JsonProcessingException {
		// Fire MDO Pooling Rules
		AbstractRules rules = RulesFactory.createRule(AgendaGroup.MDO_POOLING);	// Create a Rule of type MDO Pooling
		rules.setMarketParams(payload);	// Initialize the Rules from the Input Payload
		RulesMatrix mat = preProcessor.getData().get(rules.getAgendaGroup().getValue());	// Get the Excel matrix for this Rule type
		new RulesUpdater(mat).updateRule(rules);	// Update the Rules (if required) by looking into the cached Matrix

		if (approach == 1) {
			droolsService.fireRulesSynchronized(rules, false);	// Fire Drools with active Fallback
		}
		else if (approach == 2) {
			droolsService.fireRulesSessionPerRequest(rules, false);	// Fire Drools with active Fallback
		}

		operationAuditFlowService.insertOperationFlowDrools(AgendaGroup.MDO_POOLING.getValue());
		
		// Fire Dummy PCP Rules
		rules.setAgendaGroup(AgendaGroup.DUMMYPCP);	// Create a Rule of type Dummy PCP
		rules.setMarketParams(payload);	// Initialize the Rules from the Input Payload

		if (approach == 1) {
			droolsService.fireRulesSynchronized(rules, true);	// Fire Drools without Fallback
		}
		else if (approach == 2) {
			droolsService.fireRulesSessionPerRequest(rules, true);	// Fire Drools without Fallback
		}

		operationAuditFlowService.insertOperationFlowDrools(AgendaGroup.DUMMYPCP.getValue());
		
		// Fire MDO Provider Validation Rules
		AbstractRules rules2 = RulesFactory.createRule(AgendaGroup.MDO_PROVIDER_VALIDATION);	// Create a Rule of type MDO Provider Validation	
		rules2.setMarketParams(payload);	// Initialize the Rules from the Input Payload
		RulesMatrix mat2 = preProcessor.getData().get(rules2.getAgendaGroup().getValue());	// Get the Excel matrix for this Rule type
		new RulesUpdater(mat2).updateRule(rules2);	// Update the Rules (if required) by looking into the cached Matrix

		if (approach == 1) {
			droolsService.fireRulesSynchronized(rules2, false);	// Fire Drools with active Fallback
		}
		else if (approach == 2) {
			droolsService.fireRulesSessionPerRequest(rules2, false);	// Fire Drools with active Fallback
		}
		
		operationAuditFlowService.insertOperationFlowDrools(AgendaGroup.MDO_PROVIDER_VALIDATION.getValue());
		
		// Fire Rollover Rules
		rules2.setAgendaGroup(AgendaGroup.INVOCATION_SYSTEM_MAPPING);	// Create a Rule of type Invocation Mapping
		rules2.setMarketParams(payload);	// Initialize the Rules from the Input Payload

		if (approach == 1) {
			droolsService.fireRulesSynchronized(rules2, true);	// Fire Drools without Fallback
		}
		else if (approach == 2) {
			droolsService.fireRulesSessionPerRequest(rules2, true);	// Fire Drools without Fallback
		}

		operationAuditFlowService.insertOperationFlowDrools(AgendaGroup.INVOCATION_SYSTEM_MAPPING.getValue());
		
		return new MDOPoolingProviderValidationRules((MDOPoolingRules)rules, (ProviderValidationRules)rules2);
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
