package com.anthem.hca.smartpcp.track.audit.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.anthem.hca.smartpcp.track.audit.constants.ErrorMessages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="PcpAssignmentInfoPayload",description="Contains request information required to fetch PCP assignment details")
public class PcpAssignmentInfoPayload {

	@ApiModelProperty(dataType = "String", notes = "Contains first name of the assigned PCP")
	private String firstName;
	
	@ApiModelProperty(dataType = "String", notes = "PCP ID of the assigned PCP")
	private String pcpId;
	
	@NotBlank(message = ErrorMessages.MISSING_PCP_ASSIGN_DATE)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_PCP_ASSIGN_DATE)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains pcp assignment date for the member in format YYYY-MM-dd")
	private String pcpAssignmentDate;
	
	public String getPcpAssignmentDate() {
		return pcpAssignmentDate;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getPcpId() {
		return pcpId;
	}
	public void setPcpId(String pcpId) {
		this.pcpId = pcpId;
	}
	public void setPcpAssignmentDate(String pcpAssignmentDate) {
		this.pcpAssignmentDate = pcpAssignmentDate;
	}
}
