/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - POJO class that contains the Output attribute for MDO Scoring service 
 * 
 * @author AF71274
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value="MdoScoringOutputPayload",description="contains PCP information assigned by spcp-mdo-scoring microservice")
public class MDOScoringOutputPayload {

		@ApiModelProperty(dataType="String",required=true, notes="Contains Assigned PCP ID")
		private String provPcpId;
		@ApiModelProperty(required=true, notes="Contains Driving Distance for Assigned PCP ID")
		private Double drivingDistance;
		@ApiModelProperty(required=true, notes="Contains MDO Score for Assigned PCP ID")
		private int mdoScore;
		@ApiModelProperty(required=true, notes="Contains netwok id for Assigned PCP ID")
		private String rgnlNtwkId;
		@ApiModelProperty( dataType = "String", notes = "Contains Rank ID of the PCP")
		private String pcpRankgId;
		@ApiModelProperty(required=true, notes="Contains response code for spcp-mdo-scoring Microservice")
		private String responseCode;
		@ApiModelProperty(required=true, notes="Contains response message for spcp-mdo-scoring Microservice")
		private String responseMessage;
    
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
	 * @return the mdoScore
	 */
	public int getMdoScore() {
		return mdoScore;
	}
	/**
	 * @param mdoScore the mdoScore to set
	 */
	public void setMdoScore(int mdoScore) {
		this.mdoScore = mdoScore;
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
	public String getPcpRankgId() {
		return pcpRankgId;
	}
	public void setPcpRankgId(String pcpRankgId) {
		this.pcpRankgId = pcpRankgId;
	}
	
}
