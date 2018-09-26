/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.model;

public class MDOPoolingRulesOutputPayload extends RulesOutputPayload {

	private MDOPoolingRules rules;

	public MDOPoolingRules getRules() {
		return rules;
	}

	public void setRules(MDOPoolingRules rules) {
		this.rules = rules;
	}

}
