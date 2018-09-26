/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "PCP", description = "Contains PCP request information received by spcp-mdo-scoring microservice")
public class PCP {
	@ApiModelProperty( dataType = "String", notes = "Contains the provider id of PCP")
	private String provPcpId;

	@ApiModelProperty( dataType = "String", notes = "Contains the last name of the PCP")
	private String pcpLastNm;
	
	@ApiModelProperty( dataType = "Double", notes = "Contains the latitude of the PCP")
	private Double latdCordntNbr;
	
	@ApiModelProperty( dataType = "Double", notes = "Contains the longitude of the PCP")
	private Double lngtdCordntNbr;

	@ApiModelProperty( dataType = "String", notes = "Contains the regional network id of the PCP")
	private String rgnlNtwkId;

	@ApiModelProperty( dataType = "String", notes = "Contains Rank ID of the PCP")
	private String pcpRankgId;

	@ApiModelProperty( dataType = "String", notes = "Contains the specialty description of the PCP")
	private String spcltyDesc;

	
	@ApiModelProperty( dataType = "Date", notes = "Contains the effective date of the PCP")
	private Date grpgRltdPadrsEfctvDt;

	@ApiModelProperty( dataType = "int", notes = "Contains the max member count of the PCP")
	private Integer maxMbrCnt;
	
	@ApiModelProperty( dataType = "int", notes = "Contains the current member count of the PCP")
	private int curntMbrCnt;

	@ApiModelProperty( dataType = "List<String>", notes = "Contains the Lang code of the PCP")
	private List<String> pcpLang;

	@ApiModelProperty( dataType = "String", notes = "Contains the vbp flag of the PCP")
	private String vbpFlag;
	
	@ApiModelProperty( dataType = "String", notes = "Contains the aerial distance of the PCP")
	private Double aerialDistance;

	@ApiModelProperty(dataType = "int", notes = "Contains the mdo score of the PCP")
	private int mdoScore;

	@ApiModelProperty(dataType = "double", notes = "Contains the panel capacity of the PCP")
	private double panelCapacity;
	
	private Double drivingDistance;
	private int aerialDistanceScore;
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



	private int vbpScore;
	private int drivingDistanceScore;
	private int limitedTimeBonusScore;
	private int spcltyDescScore;
	private int languageScore;
	/**
	 * @return the aerialDistanceScore
	 */
	public int getAerialDistanceScore() {
		return aerialDistanceScore;
	}

	/**
	 * @param aerialDistanceScore the aerialDistanceScore to set
	 */
	public void setAerialDistanceScore(int aerialDistanceScore) {
		this.aerialDistanceScore = aerialDistanceScore;
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
	 * @return the drivingDistanceScore
	 */
	public int getDrivingDistanceScore() {
		return drivingDistanceScore;
	}

	/**
	 * @param drivingDistanceScore the drivingDistanceScore to set
	 */
	public void setDrivingDistanceScore(int drivingDistanceScore) {
		this.drivingDistanceScore = drivingDistanceScore;
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
	public int getSpcltyDescScore() {
		return spcltyDescScore;
	}

	/**
	 * @param spcltyDescScore the spcltyDescScore to set
	 */
	public void setSpcltyDescScore(int spcltyDescScore) {
		this.spcltyDescScore = spcltyDescScore;
	}

	/**
	 * @return the pcpRankgIdScore
	 */
	public int getPcpRankgIdScore() {
		return pcpRankgIdScore;
	}

	/**
	 * @param pcpRankgIdScore the pcpRankgIdScore to set
	 */
	public void setPcpRankgIdScore(int pcpRankgIdScore) {
		this.pcpRankgIdScore = pcpRankgIdScore;
	}

	/**
	 * @return the totalscore
	 */
	public static Comparator<PCP> getTotalscore() {
		return totalscore;
	}

	/**
	 * @return the drivingdistcomprtr
	 */
	public static Comparator<PCP> getDrivingdistcomprtr() {
		return drivingDistComprtr;
	}

	/**
	 * @return the panelcapacitycomprtr
	 */
	public static Comparator<PCP> getPanelcapacitycomprtr() {
		return panelCapacityComprtr;
	}

	/**
	 * @return the aerialdistancecomprtr
	 */
	public static Comparator<PCP> getAerialdistancecomprtr() {
		return aerialDistanceComprtr;
	}

	/**
	 * @return the lastnamecomparator
	 */
	public static Comparator<PCP> getLastnamecomparator() {
		return lastNameComparator;
	}



	private int pcpRankgIdScore;
	
	public List<String> getPcpLang() {
		return pcpLang;
	}

	public void setPcpLang(List<String> pcpLang) {
		this.pcpLang = pcpLang;
	}

	

	public static final Comparator<PCP> totalscore = (firstInfo, secndInfo) -> secndInfo.getMdoScore()
			- firstInfo.getMdoScore();

	public static final Comparator<PCP> drivingDistComprtr = (firstInfo,
			secndInfo) ->  Double.compare(firstInfo.getDrivingDistance(),secndInfo.getDrivingDistance());

	public static final Comparator<PCP> panelCapacityComprtr = (firstInfo,
			secndInfo) -> Double.compare(firstInfo.getPanelCapacity(),secndInfo.getPanelCapacity());

	public static final Comparator<PCP> aerialDistanceComprtr = (firstInfo,
			secndInfo) -> Double.compare(firstInfo.getAerialDistance(),secndInfo.getAerialDistance());

	public static final Comparator<PCP> lastNameComparator = (firstInfo, secndInfo) -> firstInfo.getPcpLastNm()
			.compareTo(secndInfo.getPcpLastNm());

	public String getProvPcpId() {
		return provPcpId;
	}

	public void setProvPcpId(String provPcpId) {
		this.provPcpId = provPcpId;
	}

	public String getPcpLastNm() {
		return pcpLastNm;
	}

	public void setPcpLastNm(String pcpLastNm) {
		this.pcpLastNm = pcpLastNm;
	}

	public Double getLatdCordntNbr() {
		return latdCordntNbr;
	}

	public void setLatdCordntNbr(Double latdCordntNbr) {
		this.latdCordntNbr = latdCordntNbr;
	}

	public Double getLngtdCordntNbr() {
		return lngtdCordntNbr;
	}

	public void setLngtdCordntNbr(Double lngtdCordntNbr) {
		this.lngtdCordntNbr = lngtdCordntNbr;
	}

	public String getRgnlNtwkId() {
		return rgnlNtwkId;
	}

	public void setRgnlNtwkId(String rgnlNtwkId) {
		this.rgnlNtwkId = rgnlNtwkId;
	}

	public String getPcpRankgId() {
		return pcpRankgId;
	}

	public void setPcpRankgId(String pcpRankgId) {
		this.pcpRankgId = pcpRankgId;
	}

	public String getSpcltyDesc() {
		return spcltyDesc;
	}

	public void setSpcltyDesc(String spcltyDesc) {
		this.spcltyDesc = spcltyDesc;
	}


	public Date getGrpgRltdPadrsEfctvDt() {
		return grpgRltdPadrsEfctvDt;
	}

	public void setGrpgRltdPadrsEfctvDt(Date grpgRltdPadrsEfctvDt) {
		this.grpgRltdPadrsEfctvDt = grpgRltdPadrsEfctvDt;
	}

	public Integer getMaxMbrCnt() {
		return maxMbrCnt;
	}

	public void setMaxMbrCnt(Integer maxMbrCnt) {
		this.maxMbrCnt = maxMbrCnt;
	}

	public int getCurntMbrCnt() {
		return curntMbrCnt;
	}

	public void setCurntMbrCnt(int curntMbrCnt) {
		this.curntMbrCnt = curntMbrCnt;
	}

	public Double getDrivingDistance() {
		return drivingDistance;
	}

	public void setDrivingDistance(Double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}

	public String getVbpFlag() {
		return vbpFlag;
	}

	public void setVbpFlag(String vbpFlag) {
		this.vbpFlag = vbpFlag;
	}

	public Double getAerialDistance() {
		return aerialDistance;
	}

	public void setAerialDistance(Double aerialDistance) {
		this.aerialDistance = aerialDistance;
	}

	public int getMdoScore() {
		return mdoScore;
	}

	public void setMdoScore(int mdoScore) {
		this.mdoScore = mdoScore;
	}

	public double getPanelCapacity() {
		return panelCapacity;
	}

	public void setPanelCapacity(double panelCapacity) {
		this.panelCapacity = panelCapacity;
	}

}