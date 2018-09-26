package com.anthem.hca.smartpcp.mdoprocessing.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.anthem.hca.smartpcp.common.am.vo.Member;


public class DroolsInputPayload{
	
	
	

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

