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

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="MDOOutputPayloadInfo",description="Contains Output payload  info of MDO microservice")
public class MDOOutputPayloadInfo {
	@ApiModelProperty(notes="Contains the list of PCP details")
	private List<PCP> pcpInfo;
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

	public List<PCP> getPcpInfo() {
		return pcpInfo;
	}

	public void setPcpInfo(List<PCP> pcpInfo) {
		this.pcpInfo = pcpInfo;
	}

}
