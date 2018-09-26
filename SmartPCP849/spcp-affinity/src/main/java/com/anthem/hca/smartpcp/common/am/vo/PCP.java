package com.anthem.hca.smartpcp.common.am.vo;

import java.util.Date;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				PCP is used for PCP payload information. 
 * 
 * *@author AF65409
 */
public class PCP {

	private String provPcpId;
	private double latdCordntNbr;
	private double lngtdCordntNbr;
	private String rgnlNtwkId;
	private Integer tierLvl;
	private String pcpRankgId;
	private String spcltyDesc;
	private Date grpgRltdPadrsTrmntnDt;
	private Date grpgRltdPadrsEfctvDt;
	private Integer maxMbrCnt;
	private Integer curntMbrCnt;
	private String accNewPatientFlag;
	private Double drivingDistance;
	private Double aerialDistance;
	private Double distance;

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
	 * @return the latdCordntNbr
	 */
	public double getLatdCordntNbr() {
		return latdCordntNbr;
	}
	/**
	 * @param latdCordntNbr the latdCordntNbr to set
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
	 * @param lngtdCordntNbr the lngtdCordntNbr to set
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
	 * @param rgnlNtwkId the rgnlNtwkId to set
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
	 * @param tierLvl the tierLvl to set
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
	 * @param pcpRankgId the pcpRankgId to set
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
	 * @param spcltyDesc the spcltyDesc to set
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
	 * @param grpgRltdPadrsTrmntnDt the grpgRltdPadrsTrmntnDt to set
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
	 * @param grpgRltdPadrsEfctvDt the grpgRltdPadrsEfctvDt to set
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
	 * @param maxMbrCnt the maxMbrCnt to set
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
	 * @param curntMbrCnt the curntMbrCnt to set
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
	 * @param accNewPatientFlag the accNewPatientFlag to set
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
	 * @param drivingDistance the drivingDistance to set
	 */
	public void setDrivingDistance(Double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}
	/**
	 * @return the aerialDistance
	 */
	public Double getAerialDistance() {
		return aerialDistance;
	}
	/**
	 * @param aerialDistance the aerialDistance to set
	 */
	public void setAerialDistance(Double aerialDistance) {
		this.aerialDistance = aerialDistance;
	}
	/**
	 * @return the distance
	 */
	public Double getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
/*		if () {
*/			return "(provPcpId=" + provPcpId + ", rgnlNtwkId=" + rgnlNtwkId + ", drivingDistance=" + drivingDistance
					+ ", aerialDistance=" + aerialDistance + ")";
/*		} else {
			return "PCP [provPcpId=" + provPcpId + ", latdCordntNbr=" + latdCordntNbr + ", lngtdCordntNbr=" + lngtdCordntNbr
					+ ", rgnlNtwkId=" + rgnlNtwkId + ", tierLvl=" + tierLvl + ", pcpRankgId=" + pcpRankgId + ", spcltyDesc="
					+ spcltyDesc + ", grpgRltdPadrsTrmntnDt=" + grpgRltdPadrsTrmntnDt + ", grpgRltdPadrsEfctvDt="
					+ grpgRltdPadrsEfctvDt + ", maxMbrCnt=" + maxMbrCnt + ", curntMbrCnt=" + curntMbrCnt
					+ ", accNewPatientFlag=" + accNewPatientFlag + ", drivingDistance=" + drivingDistance
					+ ", aerialDistance=" + aerialDistance + ", distance=" + distance + "]";
		}
*/	}
}