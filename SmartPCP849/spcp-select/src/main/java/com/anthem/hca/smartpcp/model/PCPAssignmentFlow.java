package com.anthem.hca.smartpcp.model;

import java.util.Date;
import java.util.List;



public class PCPAssignmentFlow {

	private List<ScoringProvider> scoredSortedPoolPcps;//1000
	private List<ScoringProvider> drivingDistScoredPcps;//50
	private List<ScoringProvider> tieOnDrivingPcps;
	private List<ScoringProvider> tieOnVBPPcps;
	private List<ScoringProvider> tieOnPanelCapacityPcps;
	private List<ScoringProvider> tieOnLastNamePcps;
	private ScoringProvider selectedPcp;
	private String traceId;
	private Date createdTime;
	/**
	 * @return the scoredSortedPoolPcps
	 */
	public List<ScoringProvider> getScoredSortedPoolPcps() {
		return scoredSortedPoolPcps;
	}
	/**
	 * @param scoredSortedPoolPcps the scoredSortedPoolPcps to set
	 */
	public void setScoredSortedPoolPcps(List<ScoringProvider> scoredSortedPoolPcps) {
		this.scoredSortedPoolPcps = scoredSortedPoolPcps;
	}
	/**
	 * @return the drivingDistScoredPcps
	 */
	public List<ScoringProvider> getDrivingDistScoredPcps() {
		return drivingDistScoredPcps;
	}
	/**
	 * @param drivingDistScoredPcps the drivingDistScoredPcps to set
	 */
	public void setDrivingDistScoredPcps(List<ScoringProvider> drivingDistScoredPcps) {
		this.drivingDistScoredPcps = drivingDistScoredPcps;
	}
	/**
	 * @return the tieOnDrivingPcps
	 */
	public List<ScoringProvider> getTieOnDrivingPcps() {
		return tieOnDrivingPcps;
	}
	/**
	 * @param tieOnDrivingPcps the tieOnDrivingPcps to set
	 */
	public void setTieOnDrivingPcps(List<ScoringProvider> tieOnDrivingPcps) {
		this.tieOnDrivingPcps = tieOnDrivingPcps;
	}
	/**
	 * @return the tieOnVBPPcps
	 */
	public List<ScoringProvider> getTieOnVBPPcps() {
		return tieOnVBPPcps;
	}
	/**
	 * @param tieOnVBPPcps the tieOnVBPPcps to set
	 */
	public void setTieOnVBPPcps(List<ScoringProvider> tieOnVBPPcps) {
		this.tieOnVBPPcps = tieOnVBPPcps;
	}
	/**
	 * @return the tieOnPanelCapacityPcps
	 */
	public List<ScoringProvider> getTieOnPanelCapacityPcps() {
		return tieOnPanelCapacityPcps;
	}
	/**
	 * @param tieOnPanelCapacityPcps the tieOnPanelCapacityPcps to set
	 */
	public void setTieOnPanelCapacityPcps(List<ScoringProvider> tieOnPanelCapacityPcps) {
		this.tieOnPanelCapacityPcps = tieOnPanelCapacityPcps;
	}
	/**
	 * @return the tieOnLastNamePcps
	 */
	public List<ScoringProvider> getTieOnLastNamePcps() {
		return tieOnLastNamePcps;
	}
	/**
	 * @param tieOnLastNamePcps the tieOnLastNamePcps to set
	 */
	public void setTieOnLastNamePcps(List<ScoringProvider> tieOnLastNamePcps) {
		this.tieOnLastNamePcps = tieOnLastNamePcps;
	}
	/**
	 * @return the selectedPcp
	 */
	public ScoringProvider getSelectedPcp() {
		return selectedPcp;
	}
	/**
	 * @param selectedPcp the selectedPcp to set
	 */
	public void setSelectedPcp(ScoringProvider selectedPcp) {
		this.selectedPcp = selectedPcp;
	}
	/**
	 * @return the traceId
	 */
	public String getTraceId() {
		return traceId;
	}
	/**
	 * @param traceId the traceId to set
	 */
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}
	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
	
}
