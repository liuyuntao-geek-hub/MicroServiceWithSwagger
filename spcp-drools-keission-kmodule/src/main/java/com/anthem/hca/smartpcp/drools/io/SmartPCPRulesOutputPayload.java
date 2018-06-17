package com.anthem.hca.smartpcp.drools.io;

import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;

public class SmartPCPRulesOutputPayload extends RulesOutputPayload {

	private SmartPCPRules rules;

	public SmartPCPRules getRules() {
		return rules;
	}

	public void setRules(SmartPCPRules rules) {
		this.rules = rules;
	}

}
