package com.anthem.hca.smartpcp.drools.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * POJO class for input payload for Rules
 *
 */
public class RulesEngineInputPayload {
	
	@NotNull(message="requestFor should not be null")
	private String requestFor;
	@NotNull
	@Valid
	private Member member;

	public String getRequestFor() {
		return requestFor;
	}

	public void setRequestFor(String requestFor) {
		this.requestFor = requestFor;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member that) {
		this.member = that;
	}

}
