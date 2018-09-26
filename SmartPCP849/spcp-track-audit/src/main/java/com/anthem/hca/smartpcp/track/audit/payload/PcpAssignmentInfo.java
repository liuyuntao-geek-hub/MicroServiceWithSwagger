package com.anthem.hca.smartpcp.track.audit.payload;

public class PcpAssignmentInfo {
	
	private String traceId;
	private String frstNm;
	private String pcpIdAssigned;
	private String mbrNtwrkId;
	private String mbrProcessingSt;
	
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	public String getFrstNm() {
		return frstNm;
	}
	public void setFrstNm(String frstNm) {
		this.frstNm = frstNm;
	}
	public String getPcpIdAssigned() {
		return pcpIdAssigned;
	}
	public void setPcpIdAssigned(String pcpIdAssigned) {
		this.pcpIdAssigned = pcpIdAssigned;
	}
	public String getMbrNtwrkId() {
		return mbrNtwrkId;
	}
	public void setMbrNtwrkId(String mbrNtwrkId) {
		this.mbrNtwrkId = mbrNtwrkId;
	}
	public String getMbrProcessingSt() {
		return mbrProcessingSt;
	}
	public void setMbrProcessingSt(String mbrProcessingSt) {
		this.mbrProcessingSt = mbrProcessingSt;
	}
	
	

}
