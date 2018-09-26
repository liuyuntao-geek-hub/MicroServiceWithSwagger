package com.anthem.hca.smartpcp.drools.io;

import io.swagger.annotations.ApiModelProperty;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;

/**
 * The MDOPoolingRulesOutputPayload class encapsulates the Output Payload
 * for MDO Pooling Rules that is returned as a JSON object to the caller Service. 
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class MDOPoolingRulesOutputPayload extends RulesOutputPayload {

	@ApiModelProperty(required = true, dataType = "MDOPoolingRules", notes = "Contains MDO Pooling and Dummy PCP Rules")
	private MDOPoolingRules rules;

	/**
	 * This method is used to get the MDOPoolingRules from the Output Payload.
	 * 
	 * @param  None
	 * @return MDO Pooling Rules
	 * @see    MDOPoolingRules
	 */
	public MDOPoolingRules getRules() {
		return rules;
	}

	/**
	 * This method is used to set the MDOPoolingRules in the Output Payload.
	 * 
	 * @param  rules MDO Pooling Rules
	 * @return None
	 * @see    MDOPoolingRules
	 */
	public void setRules(MDOPoolingRules rules) {
		this.rules = rules;
	}

}
