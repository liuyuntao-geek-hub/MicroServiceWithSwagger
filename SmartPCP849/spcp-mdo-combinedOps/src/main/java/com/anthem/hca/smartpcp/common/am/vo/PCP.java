package com.anthem.hca.smartpcp.common.am.vo;

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

	@ApiModelProperty(dataType = "String", notes = "Contains the address information of the PCP")
	private Integer tierLvl;

	@ApiModelProperty( dataType = "String", notes = "Contains Rank ID of the PCP")
	private String pcpRankgId;

	@ApiModelProperty( dataType = "String", notes = "Contains the specialty description of the PCP")
	private String spcltyDesc;

	@ApiModelProperty(dataType = "Date", notes = "Contains the termination date of the PCP")
	private Date grpgRltdPadrsTrmntnDt;
	
	@ApiModelProperty( dataType = "Date", notes = "Contains the effective date of the PCP")
	private Date grpgRltdPadrsEfctvDt;

	@ApiModelProperty( dataType = "Integer", notes = "Contains the max member count of the PCP")
	private Integer maxMbrCnt;
	
	@ApiModelProperty( dataType = "Integer", notes = "Contains the current member count of the PCP")
	private Integer curntMbrCnt;

	@ApiModelProperty(notes = "Contains the cp type code of the PCP")
	private String accNewPatientFlag;

	@ApiModelProperty(dataType = "Double", notes = "Contains the driving distance of the PCP")
	private Double drivingDistance;

	@ApiModelProperty( dataType = "List<String>", notes = "Contains the Lang code of the PCP")
	private List<String> pcpLang;

	@ApiModelProperty( dataType = "String", notes = "Contains the vbp flag of the PCP")
	private String vbpFlag;
	
	@ApiModelProperty( dataType = "String", notes = "Contains the aerial distance of the PCP")
	private Double aerialDistance;

	@ApiModelProperty(dataType = "Integer", notes = "Contains the mdo score of the PCP")
	private Integer mdoScore;

	@ApiModelProperty(dataType = "Float", notes = "Contains the panel capacity of the PCP")
	private float panelCapacity;

	public Integer getTierLvl() {
		return tierLvl;
	}

	public void setTierLvl(Integer tierLvl) {
		this.tierLvl = tierLvl;
	}

	public String getAccNewPatientFlag() {
		return accNewPatientFlag;
	}

	public void setAccNewPatientFlag(String accNewPatientFlag) {
		this.accNewPatientFlag = accNewPatientFlag;
	}

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

	public Date getGrpgRltdPadrsTrmntnDt() {
		return grpgRltdPadrsTrmntnDt;
	}

	public void setGrpgRltdPadrsTrmntnDt(Date grpgRltdPadrsTrmntnDt) {
		this.grpgRltdPadrsTrmntnDt = grpgRltdPadrsTrmntnDt;
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

	public Integer getCurntMbrCnt() {
		return curntMbrCnt;
	}

	public void setCurntMbrCnt(Integer curntMbrCnt) {
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

	public Integer getMdoScore() {
		return mdoScore;
	}

	public void setMdoScore(Integer mdoScore) {
		this.mdoScore = mdoScore;
	}

	public float getPanelCapacity() {
		return panelCapacity;
	}

	public void setPanelCapacity(float panelCapacity) {
		this.panelCapacity = panelCapacity;
	}

}