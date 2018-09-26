package com.anthem.hca.smartpcp.drools.io;

import io.swagger.annotations.ApiModelProperty;
import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;

/**
 * The MDOScoringRulesOutputPayload class encapsulates the Output Payload
 * for MDO Scoring Rules that is returned as a JSON object to the caller Service. 
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class MDOScoringRulesOutputPayload extends RulesOutputPayload {

	@ApiModelProperty(required = true, dataType = "MDOScoringRules", notes = "Contains MDO Scoring Rules")
	private MDOScoringRules rules;

	/**
	 * This method is used to get the MDOScoringRules from the Output Payload.
	 * 
	 * @param  None
	 * @return MDO Scoring Rules
	 * @see    MDOScoringRules
	 */
	public MDOScoringRules getRules() {
		return rules;
	}

	/**
	 * This method is used to set the MDOScoringRules in the Output Payload.
	 * 
	 * @param  rules MDO Scoring Rules
	 * @return None
	 * @see    MDOScoringRules
	 */
	public void setRules(MDOScoringRules rules) {
		this.rules = rules;
	}

}
