/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.anthem.hca.smartpcp.mdo.pool.constants.ErrorMessages;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.constants.ResponseCodes;
import com.anthem.hca.smartpcp.mdo.pool.model.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.RulesInputPayload;
import com.anthem.hca.smartpcp.mdo.pool.util.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

/**
 * RestClientService contains logic to connect to other services and get the
 * required parameters. Contains Circuit Breaker pattern implemented to continue
 * operating when a related service fails, preventing the failure from cascading
 * and giving the failing service time to recover.
 * 
 * @author AF71111
 * 
 */
@Service
@RefreshScope
public class RestClientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private OAuthAccessTokenConfig oauthTokenConfig;

	@Value("${spcp.drools.mdo.url}")
	private String mdoPoolingRulesDroolsEngineUrl;

	@Value("${spcp.drools.provider.validation.url}")
	private String providerRulesDroolsUrl;

	/**
	 * @param payload
	 * @return RulesOutputPayload
	 * @throws JsonProcessingException
	 */
	@HystrixCommand(fallbackMethod = "getFallBackPoolInformation", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "6000") })
	public MDOPoolingRulesOutputPayload getPoolInformation(RulesInputPayload payload) throws JsonProcessingException {
		HttpHeaders oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), oAuthheaders);
		ResponseEntity<MDOPoolingRulesOutputPayload> mdoPoolingRulesOutputPayload = restTemplate
				.exchange(mdoPoolingRulesDroolsEngineUrl, HttpMethod.POST, request, MDOPoolingRulesOutputPayload.class);

		return mdoPoolingRulesOutputPayload.getBody();

	}

	/**
	 * Circuit Breaker fall back method for getPoolInformation.
	 * 
	 * @param payload
	 * @returnRulesOutputPayload
	 * @throws JsonProcessingException
	 */
	public MDOPoolingRulesOutputPayload getFallBackPoolInformation(RulesInputPayload payload) {
		LOGGER.error("Drools Rule Service is temporarily down while fetching MDO Pool rules {}", "");
		MDOPoolingRulesOutputPayload response = new MDOPoolingRulesOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_DOWN);
		response.setResponseMessage(ErrorMessages.DROOLS_DOWN);
		return response;
	}

	/**
	 * @param payload
	 * @return RulesOutputPayload
	 * @throws JsonProcessingException
	 */
	@HystrixCommand(fallbackMethod = "getFallBackProviderValidationRules", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") })
	public JsonNode getProviderValidationRules(RulesInputPayload payload) throws JsonProcessingException {
		HttpHeaders oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), oAuthheaders);
		ResponseEntity<JsonNode> providerValidationRulesOutputPayload = restTemplate
				.exchange(providerRulesDroolsUrl, HttpMethod.POST, request, JsonNode.class);

		return providerValidationRulesOutputPayload.getBody();
	}

	/**
	 * Circuit Breaker fall back method for getPoolInformation.
	 * 
	 * @param payload
	 * @returnRulesOutputPayload
	 * @throws JsonProcessingException
	 */
	public JsonNode getFallBackProviderValidationRules(RulesInputPayload payload) {
		LOGGER.error("Drools Rule Service is temporarily down while fetching ProviderValidation rules {}", "");
		ObjectMapper objMapper = new ObjectMapper();
		return objMapper.createObjectNode().put(MDOPoolConstants.RESPONSE_CODE, ResponseCodes.SERVICE_DOWN)
				.put(MDOPoolConstants.RESPONSE_MESSAGE, ErrorMessages.DROOLS_DOWN);
	}
}
