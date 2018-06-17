package com.anthem.hca.smartpcp.drools.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.anthem.hca.smartpcp.drools.rules.Rules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.io.RulesInputPayload;

public interface IRulesService {

	public Rules getRules(RulesInputPayload payload) throws DroolsParseException, JsonProcessingException; 

}
