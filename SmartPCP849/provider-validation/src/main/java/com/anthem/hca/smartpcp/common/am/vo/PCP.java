package com.anthem.hca.smartpcp.common.am.vo;

import java.util.Date;
import java.util.List;


/**
 * @author af74173
 *
 */
public class PCP {

	private String provPcpId;
	private String pcpLastNm;
	private double latdCordntNbr;
	private double lngtdCordntNbr;
	private String rgnlNtwkId;
	private Integer tierLvl;
	private String pcpRankgId;
	private List<String> spcltyDesc;
	private Date grpgRltdPadrsTrmntnDt;
	private Date grpgRltdPadrsEfctvDt;
	private Integer maxMbrCnt;
	private Integer curntMbrCnt;
	private String accNewPatientFlag;
	private Double drivingDistance;
	private List<String> pcpLang;
	private String vbpFlag;
	private Double aerialDistance;
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
	 * @param pcpRankgId
	 *            the pcpRankgId to set
	 */
	public void setPcpRankgId(String pcpRankgId) {
		this.pcpRankgId = pcpRankgId;
	}

	/**
	 * @return the spcltyDesc
	 */
	public List<String> getSpcltyDesc() {
		return spcltyDesc;
	}

	/**
	 * @param spcltyDesc
	 *            the spcltyDesc to set
	 */
	public void setSpcltyDesc(List<String> spcltyDesc) {
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
	
	public Integer getMaxMbrCnt() {
		return maxMbrCnt;
	}

	public void setMaxMbrCnt(Integer maxMbrCnt) {
		this.maxMbrCnt = maxMbrCnt;
	}

	public Integer getCurntMbrCnt() {
		return curntMbrCnt;
	}

	public void setCurntMbrCnt(Integer curntMbrCnt) {
		this.curntMbrCnt = curntMbrCnt;
	}
}
