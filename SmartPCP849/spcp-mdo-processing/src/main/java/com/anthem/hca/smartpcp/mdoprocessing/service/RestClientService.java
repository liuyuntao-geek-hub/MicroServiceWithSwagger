/**
 * @author AF53723 Sends request to MDOPooling Receives response which contains
 *         PCP list.
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.service;

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
import com.anthem.hca.smartpcp.mdoprocessing.model.DroolsInputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOPoolingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOScoringOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.Member;
import com.anthem.hca.smartpcp.mdoprocessing.utils.Constants;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ErrorMessages;
import com.anthem.hca.smartpcp.mdoprocessing.utils.OAuthAccessTokenConfig;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ResponseCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;


@Service
@RefreshScope
public class RestClientService {

	private static final Logger  LOGGER = LoggerFactory.getLogger(RestClientService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;
	
	private HttpHeaders oAuthheaders;

	@Value("${spcp.mdo.pool.url}")
	private String mdoPoolingUrl;

	@Value("${spcp.drools.url}")
	private String droolsRuleEngineUrl;

	@Value("${spcp.mdo.scoring.url}")
	private String mdoScoringUrl;
	
	@Autowired
	private OAuthAccessTokenConfig oauthTokenConfig;

	/**
	 * @param member
	 * @return MDOPoolingOutputPayload
	 * @throws JsonProcessingException
	 */
	@HystrixCommand(fallbackMethod = "fallbackGetPcpList", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	public MDOPoolingOutputPayload getPCPList(Member member) throws JsonProcessingException {	
		LOGGER.debug("Calling MDO Build pool service to fetch valid pool of PCP's {}"," ");
		this.oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
        HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(member) ,this.oAuthheaders);
        ResponseEntity<MDOPoolingOutputPayload> mdoPoolingOutputPayload = restTemplate.exchange(
        			mdoPoolingUrl,
                    HttpMethod.POST,
                    request,
                    MDOPoolingOutputPayload.class
                   );
        return mdoPoolingOutputPayload.getBody();
		
	}

	public MDOPoolingOutputPayload fallbackGetPcpList(Member member) {

		LOGGER.error("MDO Build pool  is temporarily down for Member with Dob: {} " , member.getMemberDob());
		MDOPoolingOutputPayload response = new MDOPoolingOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_DOWN);
		response.setResponseMessage(ErrorMessages.MDOPOOL_DOWN);

		return response;
	}


	@HystrixCommand(fallbackMethod = "fallbackGetRules", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	public JsonNode getRules(DroolsInputPayload droolsInputPayload) throws JsonProcessingException { 
		LOGGER.debug("Calling Drools Engine to fetch scoring rules {}", "");
		
		HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(droolsInputPayload) ,this.oAuthheaders);
        ResponseEntity<JsonNode> jsonNode = restTemplate.exchange(
        		droolsRuleEngineUrl,
                    HttpMethod.POST,
                    request,
                    JsonNode.class
                   );
        return jsonNode.getBody();
	}

	public JsonNode fallbackGetRules(DroolsInputPayload droolsInputPayload)
			{
		LOGGER.error("Drools Rule Engine is temporarily down with input payload {}" , droolsInputPayload);
		ObjectMapper objMapper = new ObjectMapper();
		return objMapper.createObjectNode().put(Constants.RESPONSE_CODE, ResponseCodes.SERVICE_DOWN)
				.put(Constants.RESPONSE_MESSAGE, ErrorMessages.DROOLS_DOWN);
	}

	@HystrixCommand(fallbackMethod = "fallbackGetPCPafterScoring", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	public MDOScoringOutputPayload getPCPafterScoring(JsonNode mdoScoringInput) throws JsonProcessingException {
		LOGGER.debug("Calling MDO Scoring to retrieve the assigned PCP {}","");
		
		HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(mdoScoringInput) ,this.oAuthheaders);
        ResponseEntity<MDOScoringOutputPayload> mdoScoringOutputPayload = restTemplate.exchange(
        			mdoScoringUrl,
                    HttpMethod.POST,
                    request,
                    MDOScoringOutputPayload.class
                   );
        return mdoScoringOutputPayload.getBody();
	}

	public MDOScoringOutputPayload fallbackGetPCPafterScoring(JsonNode mdoScoringInput)
			 {

		LOGGER.error("MDO Scoring Service is temporarily down with input payload {}",mdoScoringInput);
		MDOScoringOutputPayload response = new MDOScoringOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_DOWN);
		response.setResponseMessage(ErrorMessages.MDOSCORE_DOWN);
		return response;
	}
}
