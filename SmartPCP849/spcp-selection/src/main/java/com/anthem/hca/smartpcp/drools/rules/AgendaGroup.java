package com.anthem.hca.smartpcp.drools.rules;

/**
 * The AgendaGroup enumeration is used to store all the possible
 * Drools Agenda Group (or Rule types) used in Phase 1 of Smart PCP project.
 * For each Agenda Group name, there is a corresponding Value.
 * 
 * @author  Saptarshi Dey (AF66853)
 * @version 1.2
 */

public enum AgendaGroup {

	SMARTPCP("SMARTPCP"),
	AFFINITY_PROVIDER_VALIDATION("AFFINITY"),
	MDO_PROVIDER_VALIDATION("MDO"),
	INVOCATION_SYSTEM_MAPPING("ROLLOVER"),
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
