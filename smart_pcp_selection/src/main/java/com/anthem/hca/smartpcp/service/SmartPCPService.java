package com.anthem.hca.smartpcp.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.model.RulesEngineOutputPayload;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class SmartPCPService {

	@Autowired
	private RestClientService clientService;
	
	@Autowired
	private RulesEngineInputPayload payload;
	
	private static final Logger logger = LogManager.getLogger(SmartPCPService.class); 
	
	public OutputPayload getPCP(Member member,String transactionId) throws JsonProcessingException{
		OutputPayload outputPayload = new OutputPayload();
		if(null != member){
			logger.info("Forming Drools Rule Engine payload");
			payload.setMember(member);
			payload.setRequestFor("SMARTPCP");
			payload.setLogId(transactionId);
			RulesEngineOutputPayload responsePayload = clientService.getInvocationOrder(payload);
			if(null != responsePayload && (responsePayload.getResponseCode()==200)){
				outputPayload = callServices(responsePayload.getRules().getInvocationOrder());
			}else if(null != responsePayload){
				outputPayload.setResponseCode(responsePayload.getResponseCode());
				outputPayload.setResponseMessage(responsePayload.getResponseMessage());
			}
			
		}
		
		return outputPayload;
	}
	
	private OutputPayload callServices(String order) throws JsonProcessingException{
		
			logger.info("Invocation order obtained from Drools Engine "+order);
			if(null!= order && (!(order.isEmpty()))){
				if("AM".equalsIgnoreCase(order)){
					return getPCPAM();
				}else if("MA".equalsIgnoreCase(order)){
					getPCPMA();
				}else if("M".equalsIgnoreCase(order)){
					getPCPM();
					//call MDO
				}else if("A".equalsIgnoreCase(order)){
					getPCPA();
				}else{
					// throw an exception
				}
			}
		return null;
	}
	
	private OutputPayload getPCPAM() throws JsonProcessingException{
		payload.setRequestFor("Affinity");
		RulesEngineOutputPayload responsePayload = clientService.getPCPAffinity(payload);
		OutputPayload outputPayload = new OutputPayload();
		if(null != responsePayload && (responsePayload.getResponseCode()==200) ){
			//call MDO
		}else if(null != responsePayload){
			outputPayload.setResponseCode(responsePayload.getResponseCode());
			outputPayload.setResponseMessage(responsePayload.getResponseMessage());
		}
		
		return outputPayload;
	}
	
	private OutputPayload getPCPMA() throws JsonProcessingException{
		//call MDO and Affinity
		return null;
	}
	
	private OutputPayload getPCPM() throws JsonProcessingException{
		//call MDO 
		return null;
	}
	
	private OutputPayload getPCPA() throws JsonProcessingException{
		OutputPayload outputPayload = new OutputPayload();
		RulesEngineOutputPayload responsePayload =  clientService.getPCPAffinity(payload);
		if(null != responsePayload && (responsePayload.getResponseCode()==200)){
			//Logic to be done
		}else if(null != responsePayload){
			outputPayload.setResponseCode(responsePayload.getResponseCode());
			outputPayload.setResponseMessage(responsePayload.getResponseMessage());
		}
		 return outputPayload;
	}
	
	@Bean
	public RulesEngineInputPayload rulesEngineInputPayload(){
		return new RulesEngineInputPayload();
	}
}
