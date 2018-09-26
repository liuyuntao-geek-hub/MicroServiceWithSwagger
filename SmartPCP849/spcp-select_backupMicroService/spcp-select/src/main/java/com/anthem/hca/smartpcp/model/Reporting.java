package com.anthem.hca.smartpcp.model;

import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing attributes required for reporting in response.
 * 
 * 
 * @author AF71111
 */
public class Reporting {

	@ApiModelProperty(dataType = "String", notes = "Contains the module through which module pcp is assigned")
	private String reportingCode;

	@ApiModelProperty(dataType = "String", notes = "Contains Reporting Text")
	private String reportingText1;

	@ApiModelProperty(dataType = "String", notes = "Contains Reporting Text")
	private String reportingText2;

	@ApiModelProperty(dataType = "String", notes = "Contains Short Reporting text")
	private String shortRepText;

	/**
	 * @return the reportingCode
	 */
	public String getReportingCode() {
		return reportingCode;
	}

	/**
	 * @param reportingCode
	 *            the reportingCode to set
	 */
	public void setReportingCode(String reportingCode) {
		this.reportingCode = reportingCode;
	}

	/**
	 * @return the reportingText1
	 */
	public String getReportingText1() {
		return reportingText1;
	}

	/**
	 * @param reportingText1
	 *            the reportingText1 to set
	 */
	public void setReportingText1(String reportingText1) {
		this.reportingText1 = reportingText1;
	}

	/**
	 * @return the reportingText2
	 */
	public String getReportingText2() {
		return reportingText2;
	}

	/**
	 * @param reportingText2
	 *            the reportingText2 to set
	 */
	public void setReportingText2(String reportingText2) {
		this.reportingText2 = reportingText2;
	}

	/**
	 * @return the shortRepText
	 */
	public String getShortRepText() {
		return shortRepText;
	}

	/**
	 * @param shortRepText
	 *            the shortRepText to set
	 */
	public void setShortRepText(String shortRepText) {
		this.shortRepText = shortRepText;
	}

}
