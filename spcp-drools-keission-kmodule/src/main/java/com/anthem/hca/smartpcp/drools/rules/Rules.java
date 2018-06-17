package com.anthem.hca.smartpcp.drools.rules;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.anthem.hca.smartpcp.drools.model.Member;

public abstract class Rules {

	private String market;
	private String lob;
	private String product;
	private String assignmentType;
	private String assignmentMethod;
	private AgendaGroup agendaGroup;

	public static final String FALLBACK_PARAM = "ALL";
	public static final String ISG_SOURCE_SYSTEM = "ISG";
	public static final String WGS_SOURCE_SYSTEM = "WGS";
	public static final String DEFAULT_LOB = "Commercial";

	@JsonIgnore
	public String getMarket() {
		return market;
	}

	public void setMarket(String param) {
		market = param.trim();
	}

	@JsonIgnore
	public String getLob() {
		return lob;
	}

	public void setLob(String param) {
		lob = param.trim();
	}

	@JsonIgnore
	public String getProduct() {
		return product;
	}

	public void setProduct(String param) {
		product = param.trim();
	}

	@JsonIgnore
	public String getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(String param) {
		assignmentType = param.trim();
		assignmentType = assignmentType.replaceFirst("^n|N$", "New").replaceFirst("^r|R$", "Re-Enrolled").replaceFirst("^e|E$", "Existing"); 
	}

	@JsonIgnore
	public String getAssignmentMethod() {
		return assignmentMethod;
	}

	public void setAssignmentMethod(String param) {
		assignmentMethod = param.trim();
		assignmentMethod = assignmentMethod.replaceFirst("^o|O$", "Online").replaceFirst("^b|B$", "Batch");
	}

	@JsonIgnore
	public AgendaGroup getAgendaGroup() {
		return agendaGroup;
	}

	public void setAgendaGroup(AgendaGroup agendaGroup) {
		this.agendaGroup = agendaGroup;
	}

	@JsonIgnore
	public boolean isFallback() {
		return
			getMarket().equals(FALLBACK_PARAM)
			&& getLob().equals(FALLBACK_PARAM)
			&& getProduct().equals(FALLBACK_PARAM)
			&& getAssignmentType().equals(FALLBACK_PARAM)
			&& getAssignmentMethod().equals(FALLBACK_PARAM)
		;
	}

	public void setFallback() {
		setLob(FALLBACK_PARAM);
		setMarket(FALLBACK_PARAM);
		setProduct(FALLBACK_PARAM);
		setAssignmentType(FALLBACK_PARAM);
		setAssignmentMethod(FALLBACK_PARAM);
	}

	public void setMarketParams(Member m) {
		setMarket(m.getMemberProcessingState());
		setLob(DEFAULT_LOB);  // Ignore the LOB in the Input Payload. Use 'Commercial' for Phase-1

		if (ISG_SOURCE_SYSTEM.equalsIgnoreCase(m.getMemberSourceSystem())) {
			setProduct(m.getMemberISGProductGroup());
		}
		else if (WGS_SOURCE_SYSTEM.equalsIgnoreCase(m.getMemberSourceSystem())) {
			setProduct(m.getMemberWGSGroup());
		}
		setAssignmentType(m.getMemberType());
		setAssignmentMethod(m.getSystemType());
	}

	// Since each sub-class has it's own way to determine whether rules have been fired or not this is marked abstract
	// IT will be implemented by the respective sub-classes as per their requirement
	public abstract boolean isFallbackRequired();

}
