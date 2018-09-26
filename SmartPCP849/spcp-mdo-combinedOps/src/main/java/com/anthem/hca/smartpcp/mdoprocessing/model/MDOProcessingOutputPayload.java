package com.anthem.hca.smartpcp.mdoprocessing.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="PCP",description="contains PCP information assigned by spcp-mdo-processing microservice")
public class MDOProcessingOutputPayload {

	@ApiModelProperty(dataType="String",required=true, notes="Contains Assigned PCP ID")
	private String pcpId;
	@ApiModelProperty(required=true, notes="Contains MDO Score for Assigned PCP ID")
	private Integer mdoScore;
	@ApiModelProperty(required=true, notes="Contains Driving Distance for Assigned PCP ID")
	private Double  drivingDistance;
	@ApiModelProperty(required=true, notes="Contains Network ID for Assigned PCP ID")
	private String pcpNtwrkId;
	@ApiModelProperty(required=true, notes="Contains Dummy Flag for Assigned PCP ID")
	private Boolean dummyFlag;
	@ApiModelProperty(required=true, notes="Contains response code for spcp-mdo-processing Microservice")
	private String responseCode;
	@ApiModelProperty(required=true, notes="Contains response message for spcp-mdo-processing Microservice")
	private String responseMessage;
	
	/**
	 * @return the pcpId
	 */
	public String getPcpId() {
		return pcpId;
	}
	/**
	 * @param pcpId the pcpId to set
	 */
	public void setPcpId(String pcpId) {
		this.pcpId = pcpId;
	}
	/**
	 * @return the mdoScore
	 */
	public Integer getMdoScore() {
		return mdoScore;
	}
	/**
	 * @param mdoScore the mdoScore to set
	 */
	public void setMdoScore(Integer mdoScore) {
		this.mdoScore = mdoScore;
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
	 * @return the pcpNtwrkId
	 */
	public String getPcpNtwrkId() {
		return pcpNtwrkId;
	}
	/**
	 * @param pcpNtwrkId the pcpNtwrkId to set
	 */
	public void setPcpNtwrkId(String pcpNtwrkId) {
		this.pcpNtwrkId = pcpNtwrkId;
	}
	/**
	 * @return the dummyFlag
	 */
	public Boolean getDummyFlag() {
		return dummyFlag;
	}
	/**
	 * @param dummyFlag the dummyFlag to set
	 */
	public void setDummyFlag(Boolean dummyFlag) {
		this.dummyFlag = dummyFlag;
	}
	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @return the responseMessage
	 */
	public String getResponseMessage() {
		return responseMessage;
	}
	/**
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	

}
