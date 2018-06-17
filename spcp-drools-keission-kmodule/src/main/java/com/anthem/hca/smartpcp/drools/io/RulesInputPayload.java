package com.anthem.hca.smartpcp.drools.io;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.anthem.hca.smartpcp.drools.model.Member;

public class RulesInputPayload {

	@NotNull(message = "Member input should not be null")
	@Valid
	private Member member;

	public Member getMember() {
		return member;
	}

	public void setMember(Member that) {
		this.member = that;
	}

}
