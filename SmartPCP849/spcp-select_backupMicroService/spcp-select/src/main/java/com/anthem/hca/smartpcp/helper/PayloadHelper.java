package com.anthem.hca.smartpcp.helper;

import org.springframework.beans.BeanUtils;

import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.AffinityInputPayload;
import com.anthem.hca.smartpcp.model.MDOInputPayload;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.RulesInputPayload;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Helper class to prepare payload to call other services.
 * 
 * 
 * @author AF71111
 */
public class PayloadHelper {

	/**
	 * @param member
	 * @return AffinityInputPayload
	 */
	public AffinityInputPayload createAffinityPayload(Member member) {

		AffinityInputPayload affinityPayload = new AffinityInputPayload();
		Address address = new Address();
		BeanUtils.copyProperties(member, affinityPayload);
		BeanUtils.copyProperties(member.getAddress(), address);
		affinityPayload.setAddress(address);
		return affinityPayload;

	}

	/**
	 * @param member
	 * @return MDOInputPayload
	 */
	public MDOInputPayload createMDOPayload(Member member) {

		MDOInputPayload mdoPayload = new MDOInputPayload();
		Address address = new Address();

		BeanUtils.copyProperties(member, mdoPayload);
		BeanUtils.copyProperties(member.getAddress(), address);
		mdoPayload.setAddress(address);
		return mdoPayload;
	}
	
	/**
	 * @param member
	 * @return RulesInputPayload
	 */
	public RulesInputPayload createDroolsPayload(Member member) {

		RulesInputPayload rulesPayload = new RulesInputPayload();
		BeanUtils.copyProperties(member, rulesPayload);
		return rulesPayload;
	}

}
