package com.anthem.hca.smartpcp.audit.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.audit.dao.FlowOprDao;
import com.anthem.hca.smartpcp.audit.model.FlowOprModel;
import com.anthem.hca.smartpcp.audit.payload.FlowOprPayload;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			OperationsAuditService is used to copy the data from flowOprPayload object to FlowOprModel and 
 * 			pass the same as a input parameter to logFlowOprtn() method
 * 
 * @author AF56159 
 */

@Service
public class OperationsAuditService {

	@Autowired
	FlowOprDao flowOprDao;

	/**
	 * 
	 * @param flowOprPayload
	 * logFlowOprtn is used to get the data value from flowOprPayload and copy the attributes to flowOprModel Object
	 * Then passing the flowOprModel object to logFlowOprtn() which inturn saves the details to database
	 */
	public void logFlowOprtn(FlowOprPayload flowOprPayload) {
		
		FlowOprModel flowOprModel = new FlowOprModel();
		
		BeanUtils.copyProperties(flowOprPayload, flowOprModel);
		
		flowOprDao.logFlowOprtn(flowOprModel);
	}
}
