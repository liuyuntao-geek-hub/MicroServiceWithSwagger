package com.anthem.hca.smartpcp.drools.rules;

public enum AgendaGroup {

	SMARTPCP("SMARTPCP"),
	AFFINITY_PROVIDER_VALIDATION("AFFINITY"),
	MDO_PROVIDER_VALIDATION("MDO"),
	MDO_POOLING("MDO-POOLING"),
	DUMMYPCP("DUMMYPCP"),
	MDO_SCORING("MDO-SCORING");

	private String value;

	public String getValue() {
		return value;
	}

	private AgendaGroup(String value) {
		this.value = value;
	}

}
