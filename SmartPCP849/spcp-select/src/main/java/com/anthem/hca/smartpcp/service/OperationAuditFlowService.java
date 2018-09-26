package com.anthem.hca.smartpcp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.drools.io.TransactionFlowPayload;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.util.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RefreshScope
public class OperationAuditFlowService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private Tracer tracer;

	@Value("${spcp.operations.audit.url}")
	private String transactionsServiceUrl;

	@Autowired
	private OAuthAccessTokenConfig oauthService;

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationAuditFlowService.class);

	/**
	 * This method creates a Transaction Flow Payload and forwards that to
	 * another method 'insertIntoTransactionMS' to invoke the Transaction
	 * Service Micro Service.
	 * 
	 * @param agendaGroup
	 *            Agenda Group String to append in the Message
	 * @return None
	 * @throws JsonProcessingException
	 *             When error calling the Transaction Service
	 * @see TransactionFlowPayload
	 */
	@Async
	public void insertOperationFlowDrools(String agendaGroup) {
		try {
			TransactionFlowPayload payload = new TransactionFlowPayload();
			payload.setTraceId(tracer.getCurrentSpan().traceIdString());
			payload.setServiceName(Constants.DROOLS_APP_NAME);
			payload.setOperationStatus(Constants.SUCCESS);
			payload.setOperationOutput(Constants.RULES_EXTRACTED + " | " + agendaGroup);
			payload.setResponseCode(ResponseCodes.SUCCESS);
			payload.setResponseMessage(Constants.SUCCESS);

			insertIntoTransactionMS(payload);
		} catch (Exception exception) {
			LOGGER.error("Exception occured while inserting into FLOW_OPERATION_AUDIT {} ", exception);
		}
	}

	/**
	 * @param providerList
	 *            List of PCP's fetched
	 * @param validPCP
	 *            Selected PCP
	 * @return void None
	 * @throws JsonProcessingException
	 *             Exception when creating Transaction Flow Payload
	 * 
	 *             insertOperationFlow creates a Transaction Flow Payload and
	 *             forwards that to another method insertIntoTransactionMS to
	 *             invoke the Transaction Service Microservice.
	 * 
	 */
	@Async
	public void insertOperationFlowAffinity(List<Provider> providerList, Provider validPCP, boolean mcidFlag)
			throws JsonProcessingException {

		TransactionFlowPayload transactionFlowPayload = new TransactionFlowPayload();
		try {
			transactionFlowPayload.setTraceId(tracer.getCurrentSpan().traceIdString());
			transactionFlowPayload.setServiceName(Constants.AFFINITY_APP_NAME);
			if (mcidFlag) {
				transactionFlowPayload.setResponseCode(ResponseCodes.NO_VALID_PCP);
				transactionFlowPayload.setResponseMessage("MULTIPLE MCID MEMBER");
				transactionFlowPayload.setOperationStatus(Constants.FAILURE);
				transactionFlowPayload.setOperationOutput(
						Constants.PCP_FETCHED + "=" + providerList.size() + " | " + Constants.NO_PCP_ASSIGNED_TO_MBR);
			} else {
				if (null != providerList && !providerList.isEmpty() && null != validPCP) {
					transactionFlowPayload.setResponseCode(ResponseCodes.SUCCESS);
					transactionFlowPayload.setResponseMessage(Constants.SUCCESS);
					transactionFlowPayload.setOperationStatus(Constants.SUCCESS);
					transactionFlowPayload.setOperationOutput(Constants.PCP_FETCHED + "=" + providerList.size() + " | "
							+ Constants.PCP_ASSIGNED_TO_MBR + "=" + validPCP.getProvPcpId());
				}
				if (null == providerList || providerList.isEmpty()) {
					transactionFlowPayload.setResponseCode(ResponseCodes.NO_VALID_PCP);
					transactionFlowPayload.setResponseMessage(Constants.NO_PCP_IN_MEMBER_TABLE);
					transactionFlowPayload.setOperationStatus(Constants.FAILURE);
					transactionFlowPayload.setOperationOutput(
							Constants.PCP_FETCHED + "=" + 0 + " | " + Constants.NO_PCP_ASSIGNED_TO_MBR);
				}
				if (null != providerList && !providerList.isEmpty() && null == validPCP) {
					transactionFlowPayload.setResponseCode(ResponseCodes.NO_VALID_PCP);
					transactionFlowPayload.setResponseMessage(Constants.NO_VALID_PCP_IDENTIFIED);
					transactionFlowPayload.setOperationStatus(Constants.FAILURE);
					transactionFlowPayload.setOperationOutput(Constants.PCP_FETCHED + "=" + providerList.size() + " | "
							+ Constants.NO_PCP_ASSIGNED_TO_MBR);
				}
			}
			insertIntoTransactionMS(transactionFlowPayload);

		} catch (Exception exception) {
			LOGGER.error("Exception occured while inserting into FLOW_OPERATION_AUDIT ", exception);
		}
	}

	@Async
	public void insertOperationFlowMDO(int poolSize, ScoringProvider validPCP) {
		TransactionFlowPayload transactionFlowPayload = new TransactionFlowPayload();
		try {
			transactionFlowPayload.setTraceId(tracer.getCurrentSpan().traceIdString());
			transactionFlowPayload.setServiceName(Constants.MDO_APP_NAME);

			if (null != validPCP) {
				transactionFlowPayload.setResponseCode(ResponseCodes.SUCCESS);
				transactionFlowPayload.setResponseMessage(Constants.SUCCESS);
				transactionFlowPayload.setOperationStatus(Constants.SUCCESS);
				transactionFlowPayload.setOperationOutput(Constants.VALID_PCPS + "=" + poolSize + " | "
						+ Constants.PCP_ASSIGNED + "=" + validPCP.getProvPcpId());
			} else {
				transactionFlowPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				transactionFlowPayload.setResponseMessage(Constants.FAILURE);
				transactionFlowPayload.setOperationStatus(Constants.FAILURE);
				transactionFlowPayload.setOperationOutput(Constants.VALID_PCPS + "=" + poolSize);
			}

			insertIntoTransactionMS(transactionFlowPayload);
		} catch (Exception exception) {
			LOGGER.error("Exception occured while inserting into FLOW_OPERATION_AUDIT ", exception);
		}
	}

	/**
	 * This method invokes the Transaction Service Microservice with a payload
	 * to Insert the data in FLOW_OPERATIONS_AUDIT table.
	 * 
	 * @param payload
	 *            The Transaction Service Object Payload
	 * @return None
	 * @throws JsonProcessingException
	 *             When error calling the Transaction Service
	 * @see HttpEntity
	 */
	public void insertIntoTransactionMS(TransactionFlowPayload payload) throws JsonProcessingException {
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), oauthService.getHeaders());
		restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class);
	}
}
