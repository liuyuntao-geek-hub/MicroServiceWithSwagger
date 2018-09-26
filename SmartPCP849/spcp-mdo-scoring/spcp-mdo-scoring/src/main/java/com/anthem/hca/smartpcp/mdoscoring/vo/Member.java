/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * Description - Member POJO
 *  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.anthem.hca.smartpcp.mdoscoring.utility.ErrorMessages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Member", description = "Contains member request information received by spcp-mdo-scoring microservice")
public class Member {

	@NotNull(message = ErrorMessages.MISSING_ADRS)
	@Valid
	@ApiModelProperty(required = true, notes = "Contains the address information of the member")
	private Address address;
	
	@ApiModelProperty(notes = "Contains the language code of the member")
	private List<String> memberLanguageCode;
	
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public List<String> getMemberLanguageCode() {
		return memberLanguageCode;
	}
	public void setMemberLanguageCode(List<String> memberLanguageCode) {
		this.memberLanguageCode = memberLanguageCode;
	}

}
