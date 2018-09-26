package com.anthem.hca.smartpcp.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;



/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		PCP is used for to create PCP Payload for ScoringProvider information. 
 * 
 * @author Khushbu Jain AF65409 
 */
public class ScoringProvider {

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScoringProvider [provPcpId=" + provPcpId + ", rgnlNtwkId=" + rgnlNtwkId + ", lastName=" + lastName
				+ ", speciality=" + speciality + ", grpgRltdPadrsEfctvDt=" + grpgRltdPadrsEfctvDt + ", distance="
				+ distance + ", taxId=" + taxId + ", rank=" + rank + ", pcpLang=" + pcpLang + ", vbpFlag=" + vbpFlag
				+ ", pcpScore=" + pcpScore + ", panelCapacity=" + panelCapacity + ", vbpScore=" + vbpScore
				+ ", distanceScore=" + distanceScore + ", limitedTimeBonusScore=" + limitedTimeBonusScore
				+ ", specialityScore=" + specialityScore + ", languageScore=" + languageScore + ", rankScore="
				+ rankScore + ", dummyFlag=" + dummyFlag + "]";
	}

	public String provPcpId;
	public String rgnlNtwkId;
	public String lastName;
	public List<String> speciality;
	public Date grpgRltdPadrsEfctvDt;
	public double distance;
	public String taxId;
	public  int rank;
	public List<String> pcpLang;
	public String vbpFlag;
	public int pcpScore;
	public double panelCapacity;
	
	
	private int vbpScore;
	private int distanceScore;
	private int limitedTimeBonusScore;
	private int specialityScore;
	private int languageScore;
	private int rankScore;
	
	private boolean dummyFlag = false;
	/**
	 * @return the provPcpId
	 */
	public String getProvPcpId() {
		return provPcpId;
	}
	/**
	 * @param provPcpId the provPcpId to set
	 */
	public void setProvPcpId(String provPcpId) {
		this.provPcpId = provPcpId;
	}
	/**
	 * @return the rgnlNtwkId
	 */
	public String getRgnlNtwkId() {
		return rgnlNtwkId;
	}
	/**
	 * @param rgnlNtwkId the rgnlNtwkId to set
	 */
	public void setRgnlNtwkId(String rgnlNtwkId) {
		this.rgnlNtwkId = rgnlNtwkId;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the speciality
	 */
	public List<String> getSpeciality() {
		return speciality;
	}
	/**
	 * @param speciality the speciality to set
	 */
	public void setSpeciality(List<String> speciality) {
		this.speciality = speciality;
	}
	/**
	 * @return the grpgRltdPadrsEfctvDt
	 */
	public Date getGrpgRltdPadrsEfctvDt() {
		return grpgRltdPadrsEfctvDt;
	}
	/**
	 * @param grpgRltdPadrsEfctvDt the grpgRltdPadrsEfctvDt to set
	 */
	public void setGrpgRltdPadrsEfctvDt(Date grpgRltdPadrsEfctvDt) {
		this.grpgRltdPadrsEfctvDt = grpgRltdPadrsEfctvDt;
	}
	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	/**
	 * @return the taxId
	 */
	public String getTaxId() {
		return taxId;
	}
	/**
	 * @param taxId the taxId to set
	 */
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}
	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	/**
	 * @return the pcpLang
	 */
	public List<String> getPcpLang() {
		return pcpLang;
	}
	/**
	 * @param pcpLang the pcpLang to set
	 */
	public void setPcpLang(List<String> pcpLang) {
		this.pcpLang = pcpLang;
	}
	/**
	 * @return the vbpFlag
	 */
	public String getVbpFlag() {
		return vbpFlag;
	}
	/**
	 * @param vbpFlag the vbpFlag to set
	 */
	public void setVbpFlag(String vbpFlag) {
		this.vbpFlag = vbpFlag;
	}
	/**
	 * @return the pcpScore
	 */
	public int getPcpScore() {
		return pcpScore;
	}
	/**
	 * @param pcpScore the pcpScore to set
	 */
	public void setPcpScore(int pcpScore) {
		this.pcpScore = pcpScore;
	}
	/**
	 * @return the panelCapacity
	 */
	public double getPanelCapacity() {
		return panelCapacity;
	}
	/**
	 * @param panelCapacity the panelCapacity to set
	 */
	public void setPanelCapacity(double panelCapacity) {
		this.panelCapacity = panelCapacity;
	}
	
	/**
	 * @return the vbpScore
	 */
	public int getVbpScore() {
		return vbpScore;
	}
	/**
	 * @param vbpScore the vbpScore to set
	 */
	public void setVbpScore(int vbpScore) {
		this.vbpScore = vbpScore;
	}
	/**
	 * @return the distanceScore
	 */
	public int getDistanceScore() {
		return distanceScore;
	}
	/**
	 * @param distanceScore the distanceScore to set
	 */
	public void setDistanceScore(int distanceScore) {
		this.distanceScore = distanceScore;
	}
	/**
	 * @return the limitedTimeBonusScore
	 */
	public int getLimitedTimeBonusScore() {
		return limitedTimeBonusScore;
	}
	/**
	 * @param limitedTimeBonusScore the limitedTimeBonusScore to set
	 */
	public void setLimitedTimeBonusScore(int limitedTimeBonusScore) {
		this.limitedTimeBonusScore = limitedTimeBonusScore;
	}
	/**
	 * @return the spcltyDescScore
	 */
	public int getSpecialityScore() {
		return specialityScore;
	}
	/**
	 * @param spcltyDescScore the spcltyDescScore to set
	 */
	public void setSpecialityScore(int spcltyDescScore) {
		this.specialityScore = spcltyDescScore;
	}
	/**
	 * @return the languageScore
	 */
	public int getLanguageScore() {
		return languageScore;
	}
	/**
	 * @param languageScore the languageScore to set
	 */
	public void setLanguageScore(int languageScore) {
		this.languageScore = languageScore;
	}

	/**
	 * @return the rankScore
	 */
	public int getRankScore() {
		return rankScore;
	}
	/**
	 * @param rankScore the rankScore to set
	 */
	public void setRankScore(int rankScore) {
		this.rankScore = rankScore;
	}
	/**
	 * @return the dummyflag
	 */
	public boolean isDummyFlag() {
		return dummyFlag;
	}
	/**
	 * @param dummyflag the dummyflag to set
	 */
	public void setDummyFlag(boolean dummyFlag) {
		this.dummyFlag = dummyFlag;
	}

	public static final Comparator<ScoringProvider> distanceComprtr = (firstInfo,
			secndInfo) ->  Double.compare(firstInfo.getDistance(),secndInfo.getDistance());

	public static final Comparator<ScoringProvider> panelCapacityComprtr = (firstInfo,
			secndInfo) -> Double.compare(firstInfo.getPanelCapacity(),secndInfo.getPanelCapacity());

	public static final Comparator<ScoringProvider> lastNameComparator = (firstInfo, secndInfo) -> firstInfo.getLastName()
			.compareTo(secndInfo.getLastName());
	
	public static final Comparator<ScoringProvider> totalscore = (firstInfo, secndInfo) -> secndInfo.getPcpScore()
			- firstInfo.getPcpScore();

}