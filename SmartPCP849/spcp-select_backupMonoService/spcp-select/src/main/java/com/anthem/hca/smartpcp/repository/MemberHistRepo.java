package com.anthem.hca.smartpcp.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.model.Member;

/**
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information
 * 
 * Description - Repository class to insert member input details to provider
 * history table.
 * 
 * @author AF71111
 */
@RefreshScope
@Transactional
public class MemberHistRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemberHistRepo.class);

	@Value("${member.hist.insert.qry}")
	private String memberInsertQuery;

	@Autowired
	private NamedParameterJdbcTemplate jdbcNamedTemplate;

	/**
	 * @param member
	 * @return void
	 */
	public void insertMember(Member member, String traceId) {

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("traceId", traceId);
		parameters.addValue("invocationSystem", member.getInvocationSystem());
		parameters.addValue("systemType", member.getSystemType());
		parameters.addValue("requestType", member.getRequestType());
		parameters.addValue("memberEid", member.getMemberEid());
		parameters.addValue("memberType", member.getMemberType());
		parameters.addValue("memberLob", member.getMemberLineOfBusiness());
		parameters.addValue("processingState", member.getMemberProcessingState());

		parameters.addValue("contractCode", (null != member.getMemberContractCode())
				? member.getMemberContractCode().toString() : Constants.EMPTY_STRING);
		parameters.addValue("networkId", (null != member.getMemberNetworkId()) ? member.getMemberNetworkId().toString()
				: Constants.EMPTY_STRING);
		parameters.addValue("address1", member.getAddress().getAddressLine1());
		parameters.addValue("address2", member.getAddress().getAddressLine2());
		parameters.addValue("city", member.getAddress().getCity());
		parameters.addValue("state", member.getAddress().getState());
		parameters.addValue("zipCode", member.getAddress().getZipCode());
		parameters.addValue("zipFour", member.getAddress().getZipFour());
		parameters.addValue("countyCode", member.getAddress().getCountyCode());
		parameters.addValue("dob", member.getMemberDob());
		parameters.addValue("gender", member.getMemberGender());
		parameters.addValue("language", (null != member.getMemberLanguageCode())
				? member.getMemberLanguageCode().toString() : Constants.EMPTY_STRING);
		parameters.addValue("product", member.getMemberProduct());
		parameters.addValue("productType", member.getMemberProductType());
		parameters.addValue("sourceSystem", member.getMemberSourceSystem());
		parameters.addValue("sequenceNumber", member.getMemberSequenceNumber());
		parameters.addValue("firstName", member.getMemberFirstName());
		parameters.addValue("middleName", member.getMemberMiddleName());
		parameters.addValue("lastName", member.getMemberLastName());
		parameters.addValue("rollOverPcp", member.getRollOverPcpId());
		parameters.addValue("pregnanceInd", member.getMemberPregnancyIndicator());
		parameters.addValue("effectiveDate", member.getMemberEffectiveDate());
		parameters.addValue("terminationDate", member.getMemberTerminationDate());
		parameters.addValue("updateCounter", member.getUpdateCounter());
		parameters.addValue("groupId", member.getMemberGroupId());
		parameters.addValue("subGroup", member.getMemberSubGroupId());

		LOGGER.debug("Query to insert into member history tables {}", memberInsertQuery);
		jdbcNamedTemplate.update(memberInsertQuery, parameters);

	}
}
