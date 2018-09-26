/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - POJO class that contains the  attribute for PCP
 *   
 * @author AF71274
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.model;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;



@ApiModel(value = "PCP", description = "PCP object will give information about the providers")
public class PCP {

	@ApiModelProperty(notes = "Contains PCP Id of the provider")
	private String provPcpId;
	@ApiModelProperty(notes = "Contains last name of PCP")
	private String pcpLastNm;
	@ApiModelProperty(notes = "Contains latitude location of the provider")
	private double latdCordntNbr;
	@ApiModelProperty(notes = "Contains longitude location of the provider")
	private double lngtdCordntNbr;
	@ApiModelProperty(notes = "Contains network Id of the provider")
	private String rgnlNtwkId;
	@ApiModelProperty(notes = "Contains tier level of the provider")
	private Integer tierLvl;
	@ApiModelProperty(notes = "Contains PCP ranking Id of the provider")
	private String pcpRankgId;
	@ApiModelProperty(notes = "Contains speciality description of the provider")
	private String spcltyDesc;
	@ApiModelProperty(notes = "Contains termination date for provider")
	private Date grpgRltdPadrsTrmntnDt;
	@ApiModelProperty(notes = "Contains effective date for provider")
	private Date grpgRltdPadrsEfctvDt;
	@ApiModelProperty(notes = "Contains maximum member count")
	private Integer maxMbrCnt;
	@ApiModelProperty(notes = "Contains current member count")
	private Integer curntMbrCnt;
	@ApiModelProperty(notes = "Contains cp type code of the provider")
	private String accNewPatientFlag;
	@ApiModelProperty(notes = "Contains driving distance between provider and member")
	private Double drivingDistance;
	@ApiModelProperty(notes = "Contains language codes of  provider")
	private List<String> pcpLang;
	@ApiModelProperty(notes = "Contains VBP flag as Y or N of the provider")
	private String vbpFlag;
	@ApiModelProperty(notes = "Contains aerial distance between provider and member")
	private Double aerialDistance;
	@ApiModelProperty(notes = "Contains mdo score for provider")
	private int mdoScore;

	/**
	 * @return the provPcpId
	 */
	public String getProvPcpId() {
		return provPcpId;
	}

	/**
	 * @param provPcpId
	 *            the provPcpId to set
	 */
	public void setProvPcpId(String provPcpId) {
		this.provPcpId = provPcpId;
	}

	/**
	 * @return the pcpLastNm
	 */
	public String getPcpLastNm() {
		return pcpLastNm;
	}

	/**
	 * @param pcpLastNm
	 *            the pcpLastNm to set
	 */
	public void setPcpLastNm(String pcpLastNm) {
		this.pcpLastNm = pcpLastNm;
	}

	/**
	 * @return the latdCordntNbr
	 */
	public double getLatdCordntNbr() {
		return latdCordntNbr;
	}

	/**
	 * @param latdCordntNbr
	 *            the latdCordntNbr to set
	 */
	public void setLatdCordntNbr(double latdCordntNbr) {
		this.latdCordntNbr = latdCordntNbr;
	}

	/**
	 * @return the lngtdCordntNbr
	 */
	public double getLngtdCordntNbr() {
		return lngtdCordntNbr;
	}

	/**
	 * @param lngtdCordntNbr
	 *            the lngtdCordntNbr to set
	 */
	public void setLngtdCordntNbr(double lngtdCordntNbr) {
		this.lngtdCordntNbr = lngtdCordntNbr;
	}

	/**
	 * @return the rgnlNtwkId
	 */
	public String getRgnlNtwkId() {
		return rgnlNtwkId;
	}

	/**
	 * @param rgnlNtwkId
	 *            the rgnlNtwkId to set
	 */
	public void setRgnlNtwkId(String rgnlNtwkId) {
		this.rgnlNtwkId = rgnlNtwkId;
	}

	/**
	 * @return the tierLvl
	 */
	public Integer getTierLvl() {
		return tierLvl;
	}

	/**
	 * @param tierLvl
	 *            the tierLvl to set
	 */
	public void setTierLvl(Integer tierLvl) {
		this.tierLvl = tierLvl;
	}

	/**
	 * @return the pcpRankgId
	 */
	public String getPcpRankgId() {
		return pcpRankgId;
	}

	/**
	 * @param pcpRankgId
	 *            the pcpRankgId to set
	 */
	public void setPcpRankgId(String pcpRankgId) {
		this.pcpRankgId = pcpRankgId;
	}

	/**
	 * @return the spcltyDesc
	 */
	public String getSpcltyDesc() {
		return spcltyDesc;
	}

	/**
	 * @param spcltyDesc
	 *            the spcltyDesc to set
	 */
	public void setSpcltyDesc(String spcltyDesc) {
		this.spcltyDesc = spcltyDesc;
	}

	/**
	 * @return the grpgRltdPadrsTrmntnDt
	 */
	public Date getGrpgRltdPadrsTrmntnDt() {
		return grpgRltdPadrsTrmntnDt;
	}

	/**
	 * @param grpgRltdPadrsTrmntnDt
	 *            the grpgRltdPadrsTrmntnDt to set
	 */
	public void setGrpgRltdPadrsTrmntnDt(Date grpgRltdPadrsTrmntnDt) {
		this.grpgRltdPadrsTrmntnDt = grpgRltdPadrsTrmntnDt;
	}

	/**
	 * @return the grpgRltdPadrsEfctvDt
	 */
	public Date getGrpgRltdPadrsEfctvDt() {
		return grpgRltdPadrsEfctvDt;
	}

	/**
	 * @param grpgRltdPadrsEfctvDt
	 *            the grpgRltdPadrsEfctvDt to set
	 */
	public void setGrpgRltdPadrsEfctvDt(Date grpgRltdPadrsEfctvDt) {
		this.grpgRltdPadrsEfctvDt = grpgRltdPadrsEfctvDt;
	}

	/**
	 * @return the maxMbrCnt
	 */
	public Integer getMaxMbrCnt() {
		return maxMbrCnt;
	}

	/**
	 * @param maxMbrCnt
	 *            the maxMbrCnt to set
	 */
	public void setMaxMbrCnt(Integer maxMbrCnt) {
		this.maxMbrCnt = maxMbrCnt;
	}

	/**
	 * @return the curntMbrCnt
	 */
	public Integer getCurntMbrCnt() {
		return curntMbrCnt;
	}

	/**
	 * @param curntMbrCnt
	 *            the curntMbrCnt to set
	 */
	public void setCurntMbrCnt(Integer curntMbrCnt) {
		this.curntMbrCnt = curntMbrCnt;
	}

	/**
	 * @return the accNewPatientFlag
	 */
	public String getAccNewPatientFlag() {
		return accNewPatientFlag;
	}

	/**
	 * @param accNewPatientFlag
	 *            the accNewPatientFlag to set
	 */
	public void setAccNewPatientFlag(String accNewPatientFlag) {
		this.accNewPatientFlag = accNewPatientFlag;
	}

	/**
	 * @return the drivingDistance
	 */
	public Double getDrivingDistance() {
		return drivingDistance;
	}

	/**
	 * @param drivingDistance
	 *            the drivingDistance to set
	 */
	public void setDrivingDistance(Double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}

	/**
	 * @return the pcpLang
	 */
	public List<String> getPcpLang() {
		
		return pcpLang;
	}

	/**
	 * @param pcpLang
	 *            the pcpLang to set
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
	 * @param vbpFlag
	 *            the vbpFlag to set
	 */
	public void setVbpFlag(String vbpFlag) {
		this.vbpFlag = vbpFlag;
	}

	/**
	 * @return the aerialDistance
	 */
	public Double getAerialDistance() {
		return aerialDistance;
	}

	/**
	 * @param aerialDistance
	 *            the aerialDistance to set
	 */
	public void setAerialDistance(Double aerialDistance) {
		this.aerialDistance = aerialDistance;
	}

	/**
	 * @return the mdoScore
	 */
	public int getMdoScore() {
		return mdoScore;
	}

	/**
	 * @param mdoScore
	 *            the mdoScore to set
	 */
	public void setMdoScore(int mdoScore) {
		this.mdoScore = mdoScore;
	}
}
