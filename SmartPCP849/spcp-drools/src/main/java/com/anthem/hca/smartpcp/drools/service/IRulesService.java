package com.anthem.hca.smartpcp.drools.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.anthem.hca.smartpcp.drools.rules.AbstractRules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.model.Member;

/**
 * The IRulesService interface defines a getRules method which retrieves Rules from Drools Engine.
 * Each concrete class that implements this implements provides a custom definition of this method.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public interface IRulesService {

	/**
	 * This method retrieves Rules from the Drools Engine. It will be implemented
	 * for each Service and custom definition will be given there.
	 * 
	 * @param  payload                 The Member Payload
	 * @return                         The Updated Rules after firing Drools
	 * @throws DroolsParseException    When Parsing error found in Drools Rule files
	 * @throws JsonProcessingException When problem found while converting Transaction payload to JSON String
	 * @see    Member
	 */
	public AbstractRules getRules(Member payload) throws DroolsParseException, JsonProcessingException; 

}
