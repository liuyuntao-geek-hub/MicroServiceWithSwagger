package com.anthem.hca.smartpcp.affinity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.AffinityOutputPayload;
import com.anthem.hca.smartpcp.affinity.model.ProviderPayload;
import com.anthem.hca.smartpcp.affinity.model.ProviderValidationOutPayload;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			AffinityService is used to prepare output payload for Affinity micro-service for 
 * 			member information provided from SMART PCP.
 * 
 * @author AF65409 
 */
@Service
public class AffinityService {

	@Autowired
	private ProviderService providerService;
	@Autowired 
	private RestClientPayloadService restClientPayloadService;

	/**
	 * @param member
	 * @return AffinityOutPayload
	 * 
	 *    		getAffinityOutPayload is used to prepare output payload for Affinity micro-service for 
	 *    		member information provided from SMART PCP.
	 * 
	 */
	public AffinityOutputPayload getAffinityOutPayload(Member member) {
		AffinityOutputPayload affinityOutputPayload = new AffinityOutputPayload();
		ProviderPayload providerPayload = providerService.getProviderPayload(member);

		if(ResponseCodes.SUCCESS.equalsIgnoreCase(providerPayload.getResponseCode())) {
			ProviderValidationOutPayload providerValidationOutPayload = restClientPayloadService.getProviderValidationOutputPayload(member, providerPayload.getProviderPayloadList());

			if(ResponseCodes.SUCCESS.equalsIgnoreCase(providerValidationOutPayload.getResponseCode())) {
				PCP pcpDetails = providerValidationOutPayload.getPcpInfo();
				String pcpId = null != pcpDetails? pcpDetails.getProvPcpId():null;
				if(null != pcpId) {
					affinityOutputPayload.setPcpId(pcpId);
					if(pcpDetails.getDrivingDistance() != null)
						affinityOutputPayload.setDrivingDistance(pcpDetails.getDrivingDistance());
					else
						affinityOutputPayload.setDrivingDistance(pcpDetails.getAerialDistance());
					
					affinityOutputPayload.setRgnlNtwrkId(pcpDetails.getRgnlNtwkId());
					affinityOutputPayload.setResponseCode(providerValidationOutPayload.getResponseCode());
					affinityOutputPayload.setResponseMessage(providerValidationOutPayload.getResponseMessage());
				}else {
					affinityOutputPayload.setResponseCode(providerValidationOutPayload.getResponseCode());
					affinityOutputPayload.setResponseMessage(providerValidationOutPayload.getResponseMessage());
				}
			}else {
				affinityOutputPayload.setResponseCode(providerValidationOutPayload.getResponseCode());
				affinityOutputPayload.setResponseMessage(providerValidationOutPayload.getResponseMessage());
			}
		}else {
			affinityOutputPayload.setResponseCode(providerPayload.getResponseCode());
			affinityOutputPayload.setResponseMessage(providerPayload.getResponseMessage());
		}
		return affinityOutputPayload;
	}
}