package com.anthem.hca.smartpcp.drools.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import io.swagger.annotations.ApiModelProperty;
import com.anthem.hca.smartpcp.drools.util.ErrorMessages;

/**
 * The Member class encapsulates the model of a Member containing
 * all the attributes of a Member required in Drools Excel files.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.4
 */

public class Member {

	@Size(max = 2, message = ErrorMessages.INVALID_INVOCATION_SYSTEM)
	@Pattern(regexp = "[0-9]*", message = ErrorMessages.INVALID_INVOCATION_SYSTEM)
	@ApiModelProperty(dataType = "String", notes = "Contains the Invoking System Code, Max Length = 2, Regular Exp as [0-9]*")
	private String invocationSystem;

	@NotBlank(message = ErrorMessages.MISSING_MBR_LOB)
	@Size(max = 8, message = ErrorMessages.INVALID_MBR_LOB)
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = ErrorMessages.INVALID_MBR_LOB)
    @ApiModelProperty(required = true, dataType = "String", notes = "Contains Member's Line of Business, Max Length = 8, Regular Exp as [a-z-A-Z-0-9]*")
	private String memberLineOfBusiness;

	@NotBlank(message = ErrorMessages.MISSING_MBR_PROCS_STATE)
	@Size(max = 2, message = ErrorMessages.INVALID_MBR_PROCS_STATE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_MBR_PROCS_STATE)
    @ApiModelProperty(required = true, dataType = "String", notes = "Contains Processing State of the Member, Max Length = 2")
	private String memberProcessingState;

	@NotBlank(message = ErrorMessages.MISSING_PRODUCT_TYPE)
	@Size(max = 10, message = ErrorMessages.INVALID_PRODUCT_TYPE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_PRODUCT_TYPE)
    @ApiModelProperty(required = true, dataType = "String", notes = "Contains Product Type of the Member, Max Length = 10")
	private String memberProductType;

	@NotBlank(message = ErrorMessages.MISSING_MBR_TYPE)
	@Size(max = 1, message = ErrorMessages.INVALID_MBR_TYPE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_MBR_TYPE)
    @ApiModelProperty(required = true, dataType = "String", notes = "Contains Member Type (New, Existing or Re-Enrolled), Max Length = 1, Regular Exp as [a-z-A-Z]*")
	private String memberType;

	@NotBlank(message = ErrorMessages.MISSING_SYSTEM_TYPE)
	@Size(max = 1, message = ErrorMessages.INVALID_SYSTEM_TYPE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_SYSTEM_TYPE)
    @ApiModelProperty(required = true, dataType = "String", notes = "Contains Invoking System Type (Online or Batch), Max Length = 1, Regular Exp as [a-z-A-Z]*")
	private String systemType;	

	/**
	 * This method is used to get the Invocation System from the Member object.
	 * 
	 * @param  None
	 * @return The Invocation System
	 */
	public String getInvocationSystem() {
		return invocationSystem;
	}

	/**
	 * This method is used to set the Invocation System in the Member object.
	 * 
	 * @param  invocationSystem The Invocation System
	 * @return None
	 */
	public void setInvocationSystem(String invocationSystem) {
		this.invocationSystem = invocationSystem;
	}

	/**
	 * This method is used to get the Line of Business from the Member object.
	 * 
	 * @param  None
	 * @return The Line of Business
	 */
	public String getMemberLineOfBusiness() {
		return memberLineOfBusiness;
	}

	/**
	 * This method is used to set the Line of Business in the Member object.
	 * 
	 * @param  memberLineOfBusiness The Line of Business
	 * @return None
	 */
	public void setMemberLineOfBusiness(String memberLineOfBusiness) {
		this.memberLineOfBusiness = memberLineOfBusiness;
	}

	/**
	 * This method is used to get the Member Processing State from the Member object.
	 * 
	 * @param  None
	 * @return The Member Processing State
	 */
	public String getMemberProcessingState() {
		return memberProcessingState;
	}

	/**
	 * This method is used to set the Member Processing State in the Member object.
	 * 
	 * @param  memberProcessingState The Member Processing State
	 * @return None
	 */
	public void setMemberProcessingState(String memberProcessingState) {
		this.memberProcessingState = memberProcessingState;
	}

	/**
	 * This method is used to get the Member Product Type from the Member object.
	 * 
	 * @param  None
	 * @return The Member Product Type
	 */
	public String getMemberProductType() {
		return memberProductType;
	}

	/**
	 * This method is used to set the Member Product Type in the Member object.
	 * 
	 * @param  memberProductType The Member Product Type
	 * @return None
	 */
	public void setMemberProductType(String memberProductType) {
		this.memberProductType = memberProductType;
	}

	/**
	 * This method is used to get the Member Type from the Member object.
	 * 
	 * @param  None
	 * @return The Member Type
	 */
	public String getMemberType() {
		return memberType;
	}

	/**
	 * This method is used to set the Member Type in the Member object.
	 * 
	 * @param  memberType The Member Type
	 * @return None
	 */
	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	/**
	 * This method is used to get the System Type from the Member object.
	 * 
	 * @param  None
	 * @return The System Type
	 */
	public String getSystemType() {
		return systemType;
	}

	/**
	 * This method is used to set the System Type in the Member object.
	 * 
	 * @param  systemType The System Type
	 * @return None
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

}
