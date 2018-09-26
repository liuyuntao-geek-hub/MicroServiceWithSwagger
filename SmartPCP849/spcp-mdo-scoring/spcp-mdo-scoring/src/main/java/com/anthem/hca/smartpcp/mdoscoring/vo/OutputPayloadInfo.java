/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="OutputPayload",description="contains PCP information assigned by spcp-mdo-scoring microservice")
public class OutputPayloadInfo {
	@ApiModelProperty(dataType="String",required=true, notes="Contains Assigned PCP ID")
	private String provPcpId;
	@ApiModelProperty(required=true, notes="Contains Driving Distance for Assigned PCP ID")
	private Double drivingDistance;
	@ApiModelProperty(required=true, notes="Contains MDO Score for Assigned PCP ID")
	private int mdoScore;
	@ApiModelProperty( dataType = "String", notes = "Contains Rank ID of the PCP")
	private String pcpRankgId;
	@ApiModelProperty(required=true, notes="Contains netwok id for Assigned PCP ID")
	private String rgnlNtwkId;
	@ApiModelProperty(required=true, notes="Contains response code for spcp-mdo-scoring Microservice")
	private String responseCode;
	@ApiModelProperty(required=true, notes="Contains response message for spcp-mdo-scoring Microservice")
	private String responseMessage;

	public String getProvPcpId() {
		return provPcpId;
	}

	public void setProvPcpId(String provPcpId) {
		this.provPcpId = provPcpId;
	}

	public Double getDrivingDistance() {
		return drivingDistance;
	}

	public void setDrivingDistance(Double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}

	public int getMdoScore() {
		return mdoScore;
	}

	public void setMdoScore(int mdoScore) {
		this.mdoScore = mdoScore;
	}
	
	public String getPcpRankgId() {
		return pcpRankgId;
	}

	public void setPcpRankgId(String pcpRankgId) {
		this.pcpRankgId = pcpRankgId;
	}

	public String getRgnlNtwkId() {
		return rgnlNtwkId;
	}

	public void setRgnlNtwkId(String rgnlNtwkId) {
		this.rgnlNtwkId = rgnlNtwkId;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public String toString() {
		return "final= [provPcpId=" + provPcpId + ", drivingDistance=" + drivingDistance + ", mdoScore="
				+ mdoScore + ", rgnlNtwkId=" + rgnlNtwkId + ", responseCode=" + responseCode + ", responseMessage="
				+ responseMessage + "]";
	}

}
