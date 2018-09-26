package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;

public class InputPayloadInfo {
	@NotNull(message = "Member object should not be null")
	@Valid
	private Member member;

	@NotNull(message = "pcp object should not be null")
	@Size(min = 1, message = "pcp should have atleast one element")
	@Valid
	private List<PCP> pcp;

	@NotNull(message = "rules object should not be null")
	@Valid
	private Rules rules;

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<PCP> getPcp() {
		return pcp;
	}

	public void setPcp(List<PCP> pcp) {
		this.pcp = pcp;
	}

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		this.rules = rules;
	}

}
