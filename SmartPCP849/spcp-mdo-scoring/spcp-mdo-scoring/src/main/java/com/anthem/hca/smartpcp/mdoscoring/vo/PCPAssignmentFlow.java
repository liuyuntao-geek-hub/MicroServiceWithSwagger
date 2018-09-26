package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.util.Date;
import java.util.List;

public class PCPAssignmentFlow {

	private List<PCP> scoredSortedPoolPcps;//1000
	private List<PCP> drivingDistScoredPcps;//50
	private List<PCP> tieOnDrivingPcps;
	private List<PCP> tieOnVBPPcps;
	private List<PCP> tieOnPanelCapacityPcps;
	private List<PCP> tieOnLastNamePcps;
	private PCP selectedPcp;
	private String traceId;
	private Date createdTime;

	public List<PCP> getScoredSortedPoolPcps() {
		return scoredSortedPoolPcps;
	}

	public void setScoredSortedPoolPcps(List<PCP> scoredSortedPoolPcps) {
		this.scoredSortedPoolPcps = scoredSortedPoolPcps;
	}

	public List<PCP> getDrivingDistScoredPcps() {
		return drivingDistScoredPcps;
	}

	public void setDrivingDistScoredPcps(List<PCP> drivingDistScoredPcps) {
		this.drivingDistScoredPcps = drivingDistScoredPcps;
	}

	public List<PCP> getTieOnDrivingPcps() {
		return tieOnDrivingPcps;
	}

	public void setTieOnDrivingPcps(List<PCP> tieOnDrivingPcps) {
		this.tieOnDrivingPcps = tieOnDrivingPcps;
	}

	public List<PCP> getTieOnVBPPcps() {
		return tieOnVBPPcps;
	}

	public void setTieOnVBPPcps(List<PCP> tieOnVBPPcps) {
		this.tieOnVBPPcps = tieOnVBPPcps;
	}

	public List<PCP> getTieOnPanelCapacityPcps() {
		return tieOnPanelCapacityPcps;
	}

	public void setTieOnPanelCapacityPcps(List<PCP> tieOnPanelCapacityPcps) {
		this.tieOnPanelCapacityPcps = tieOnPanelCapacityPcps;
	}

	public List<PCP> getTieOnLastNamePcps() {
		return tieOnLastNamePcps;
	}

	public void setTieOnLastNamePcps(List<PCP> tieOnLastNamePcps) {
		this.tieOnLastNamePcps = tieOnLastNamePcps;
	}

	public PCP getSelectedPcp() {
		return selectedPcp;
	}

	public void setSelectedPcp(PCP selectedPcp) {
		this.selectedPcp = selectedPcp;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

}
