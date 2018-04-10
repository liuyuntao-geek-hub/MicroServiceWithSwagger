package com.anthem.hca.smartpcp.model;

public class RulesEngineInputPayload {

	private String requestFor;
	private Member member;
	private String logId;

	public String getRequestFor() {
		return requestFor;
	}
	public void setRequestFor(String requestFor) {
		this.requestFor = requestFor;
	}

	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
}

