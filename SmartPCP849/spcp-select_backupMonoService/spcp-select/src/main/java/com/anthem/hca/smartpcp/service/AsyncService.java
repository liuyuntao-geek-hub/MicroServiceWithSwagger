
package com.anthem.hca.smartpcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.repository.MemberHistRepo;
import com.anthem.hca.smartpcp.repository.PanelCapacityRepo;
import com.anthem.hca.smartpcp.repository.ProviderHistRepo;
import com.fasterxml.jackson.core.JsonProcessingException;


/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - * AsyncService contains logic to connect the sqlServer db to update panel capacity and keep
 *                 track of successful request processing is completed and member, provider data
 *                 are inserted in history table in an asynchronous way.
 * 
 * 
 * @author AF71111
 */
@Service
public class AsyncService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncService.class);

	@Autowired
	private ProviderHistRepo providerHistRepo;

	@Autowired
	private MemberHistRepo memberHistRepo;
	
	@Autowired
	private PanelCapacityRepo panelCapacityRepo;

	@Autowired
	private Tracer tracer;

	/**
	 * @param payload
	 * @throws JsonProcessingException
	 */
	@Async
	public void asyncUpdate(String assignedPcpId, String networkId) {
		try {
			panelCapacityRepo.updatePanelCapacity(assignedPcpId, networkId);
			providerHistRepo.insertPCP(tracer.getCurrentSpan().traceIdString(), assignedPcpId, networkId);
		} catch (Exception exception) {
			LOGGER.error("Error occured while inserting into transaction table {}",exception.getMessage(), exception);
		}
	}
	
	@Async
	public void asyncMemberUpdate(Member member) {
		try {
			memberHistRepo.insertMember(member, tracer.getCurrentSpan().traceIdString());
		} catch (Exception exception) {
			LOGGER.error("Error occured while inserting into Member history table {}",exception.getMessage(), exception);
		}
	}

	@Bean
	public ProviderHistRepo providerHistRepo() {
		return new ProviderHistRepo();
	}

	@Bean
	public MemberHistRepo memberHistRepo() {
		return new MemberHistRepo();
	}
	
	@Bean
	public PanelCapacityRepo panelCapacityRepo() {
		return new PanelCapacityRepo();
	}
}
