package com.anthem.hca.smartpcp.drools.service;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.model.Rules;
import com.anthem.hca.smartpcp.drools.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author AF74173
 *
 */
@Service
public class RulesEngineService {

	@Autowired
	private DroolsService dService;
	
	@Autowired
	private DroolsRestClientService restClientService;
	
	private static final Logger logger = LogManager.getLogger(RulesEngineService.class);

	/**
	 * @param payload
	 * @return Rules
	 * @throws DroolsParseException
	 * @throws JsonProcessingException 
	 */
	public Rules getRules(RulesEngineInputPayload payload) throws DroolsParseException, JsonProcessingException {
		
		Rules rules = new Rules();
		Member member = payload.getMember();
		String requestFor = payload.getRequestFor();

		rules.setMarket(member.getMarket());
		rules.setLob(member.getLob());
		rules.setProduct(member.getProduct());
		rules.setAssignmentType(member.getAssignmentType());
		rules.setAssignmentMethod(member.getAssignmentMethod());

		Rules afRules = dService.fireRulesForActualOrFallback(rules);
		if (afRules.isFallbackRequired()) {
			logger.info("Switching to Fallback");
			afRules.setMarket("ALL");
			afRules.setLob("ALL");
			afRules.setProduct("ALL");
			afRules.setAssignmentType("ALL");
			afRules.setAssignmentMethod("ALL");
		}
		
		Rules retVal = dService.fireRulesFor(afRules, requestFor);
		restClientService.insertOperationFlow(requestFor);
		
		return  retVal;
	}
}
