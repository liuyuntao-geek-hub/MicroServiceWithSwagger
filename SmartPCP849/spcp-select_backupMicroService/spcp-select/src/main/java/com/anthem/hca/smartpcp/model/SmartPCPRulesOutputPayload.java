package com.anthem.hca.smartpcp.model;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing output attributes of Drools.
 * 
 * 
 * @author AF71111
 */
public class SmartPCPRulesOutputPayload extends RulesOutputPayload {

	private SmartPCPRules rules;

	/**
	 * @return the rules
	 */
	public SmartPCPRules getRules() {
		return rules;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(SmartPCPRules rules) {
		this.rules = rules;
	}

	

}
