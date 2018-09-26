package com.anthem.hca.smartpcp.mdoscoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.anthem.hca.smartpcp.mdoscoring.utility.Constant;
import com.anthem.hca.smartpcp.mdoscoring.utility.ResponseCodes;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingInputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingOutputPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Service
@RefreshScope
public class BingRestClientService {

	private static final Logger logger = LoggerFactory.getLogger(BingRestClientService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	private HttpHeaders headers;

	@Value("${bing.url}")
	private String bingUrl;

	@Value("${bing.key}")
	private String bingKey;

	public BingRestClientService() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.headers = httpHeaders;
	}

	@HystrixCommand(fallbackMethod = "fallbackGetDistanceMatrix", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100000") })
	public BingOutputPayload getDistanceMatrix(BingInputPayload bingInput) throws JsonProcessingException {
		String finalBingURL = bingUrl + "=" + bingKey;
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(bingInput), headers);
		return restTemplate.postForObject(finalBingURL, request, BingOutputPayload.class);
		
	}

	public BingOutputPayload fallbackGetDistanceMatrix(BingInputPayload bingInput) throws JsonProcessingException {
		logger.error("Bing Response not received {}", " ");
		BingOutputPayload response = new BingOutputPayload();
		String message=mapper.writeValueAsString(bingInput);
		logger.info("Bing Input Payload Object is {} " ,message);
		response.setStatusCode(Integer.parseInt(ResponseCodes.SERVICE_NOT_AVAILABLE));
		response.setStatusDescription(Constant.BING_MSG);
		return response;
	}

}
