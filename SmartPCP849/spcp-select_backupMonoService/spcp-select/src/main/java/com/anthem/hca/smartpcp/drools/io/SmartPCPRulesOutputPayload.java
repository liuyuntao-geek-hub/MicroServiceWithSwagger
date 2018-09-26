package com.anthem.hca.smartpcp.drools.io;

import io.swagger.annotations.ApiModelProperty;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;

/**
 * The SmartPCPRulesOutputPayload class encapsulates the Output Payload
 * for Smart PCP Rules that is returned as a JSON object to the caller Service. 
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class SmartPCPRulesOutputPayload extends RulesOutputPayload {

	@ApiModelProperty(required = true, dataType = "SmartPCPRules", notes = "Contains SmartPCP Rules")
	private SmartPCPRules rules;

	/**
	 * This method is used to get the SmartPCPRules from the Output Payload.
	 * 
	 * @param  None
	 * @return Smart PCP Rules
	 * @see    SmartPCPRules
	 */
	public SmartPCPRules getRules() {
		return rules;
	}

	/**
	 * This method is used to set the SmartPCPRules in the Output Payload.
	 * 
	 * @param  rules Smart PCP Rule
	 * @return None
	 * @see    SmartPCPRules
	 */
	public void setRules(SmartPCPRules rules) {
		this.rules = rules;
	}

}
