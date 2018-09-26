/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - This java class is used for provider validation
 * 
 * 
 * @author AF69961
 */
package com.anthem.hca.smartpcp.providervalidation.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="AffinityOutputPayloadInfo",description="Contains Output payload  info of Affinity microservice")
public class AffinityOutputPayloadInfo {
	@ApiModelProperty(notes="Contains the PCP details")
	private PCP pcpInfo;
	@ApiModelProperty(notes="Contains the response code details")
	private String responseCode;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@ApiModelProperty(notes="Contains the response message details")
	private String responseMessage;

	public PCP getPcpInfo() {
		return pcpInfo;
	}

	public void setPcpInfo(PCP pcpInfo) {
		this.pcpInfo = pcpInfo;
	}

}
