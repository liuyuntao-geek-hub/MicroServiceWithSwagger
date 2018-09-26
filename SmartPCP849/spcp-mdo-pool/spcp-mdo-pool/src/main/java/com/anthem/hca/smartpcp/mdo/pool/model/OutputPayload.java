/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.model;

import java.util.List;

import com.anthem.hca.smartpcp.common.am.vo.PCP;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "OutputPayload", description = "Output payload from spcp-mdo-pool micro service")
public class OutputPayload {

	@ApiModelProperty(required = true, notes = "Contains the list of PCPs,that will give all the provider information")
	private List<PCP> pcps;
	@ApiModelProperty(required = true, notes = "Contains the response code that will be returning from spcp mdo pool micro service")
	private int responseCode;
	@ApiModelProperty(required = true, notes = "Contains the response message that will be returning from spcp mdo pool micro service")
	private String responseMessage;
	@ApiModelProperty(required = true, notes = "will be true if returned PCP is dummy else it will be set as false")
	private boolean dummyFlag;

	public List<PCP> getPcps() {
		return pcps;
	}

	public void setPcps(List<PCP> pcps) {
		this.pcps = pcps;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public boolean isDummyFlag() {
		return dummyFlag;
	}

	public void setDummyFlag(boolean dummyFlag) {
		this.dummyFlag = dummyFlag;
	}

}
