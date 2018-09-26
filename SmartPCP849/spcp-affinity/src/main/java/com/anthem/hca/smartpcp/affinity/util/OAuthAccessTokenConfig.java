package com.anthem.hca.smartpcp.affinity.util;

import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			The OAuthAccessTokenConfig class is the Component Layer implementation 
 * 			of retrieving the access_token from apigee.
 * 
 * @author AF65409 
 */
@Component
public class OAuthAccessTokenConfig {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RetryTemplate retryTemplate;

	@Value("${security.client.id}")
	private String clientId;
	@Value("${security.secret.key}")
	private String secretKey;
	@Value("${security.api.key}")
	private String apiKey;
	@Value("${security.token.uri}")
	private String tokenUri;

	private static final String AUTHORIZATION = "Authorization";
	private static final String API_KEY = "apikey";
	private static final String BEARER = "Bearer ";
	private static final String BASIC = "Basic ";

	private Logger logger = LoggerFactory.getLogger(OAuthAccessTokenConfig.class);

	/**
	 * @return HttpHeaders - header with access token
	 * @exception Exception - may occur when apigee is down for any reason
	 * 
	 * 			getHeaderWithAccessToken is called in all the service to get the headers with access token.
	 * 			If for any reason apigee can not serve the access_token then it will retry
	 * 			three times to get the same from apigee 
	 * 
	 */
	public HttpHeaders getHeaderWithAccessToken() {
		return retryTemplate.execute(retryContext -> {
			ResponseEntity<String> response = restTemplate.exchange(
					tokenUri,
					HttpMethod.POST,
					buildRequestForAccessToekn(),
					String.class
					);
			String accessToken = retrieveTokenFromJson(response.getBody());
			return buildHttpHeaders(BEARER, accessToken, MediaType.APPLICATION_JSON);

		}, new RecoveryCallback<HttpHeaders>() {
			@Override
			public HttpHeaders recover(RetryContext context) throws Exception {
				logger.error("Error retrieving access token from apigee ", context.getLastThrowable());
				return buildHttpHeaders(BEARER, null, MediaType.APPLICATION_JSON);
			}
		});
	}

	/**
	 * @return HttpEntity 
	 * 
	 * 			This method prepares the header entity with authentication type, media type,
	 * 			oauth grant type and scope to get the access token from apigee. 
	 * 
	 * 
	 */
	private HttpEntity<MultiValueMap<String, String>> buildRequestForAccessToekn() {
		return new HttpEntity<>(setRequestParam(), buildHttpHeaders(
				BASIC, encodeClientCredentials(), MediaType.APPLICATION_FORM_URLENCODED));
	}

	/**
	 * @param authType  -  Authorization Type(Basic/Bearer)
	 * @param authValue -  Authorization Value(access_toen/encoded credential for authentication)
	 * @param mediaType -  Media Type(application/json,application/x-www-form-urlencoded)
	 * 
	 * @return HttpHeaders
	 * 
	 * 			This method is a generic one to build the http headers based on the parameters 
	 * 
	 */
	private HttpHeaders buildHttpHeaders(String authType, String authValue, MediaType mediaType) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, authType + authValue);
		headers.add(API_KEY, apiKey);
		headers.setContentType(mediaType);
		return headers;
	}

	/**
	 *  @return String - returns the encoded client credential 
	 *  
	 *  		This method encrypts the client id and secret key for authentication with apigee
	 * 
	 */
	private String encodeClientCredentials() {
		String cred = clientId + ":" + secretKey;
		return  new String(Base64.getEncoder().encode(cred.getBytes()));
	}

	/**
	 * @return MultiValueMap<String, String>
	 * 
	 * 			This method sets the grant type and provides the scope for authentication with apigee
	 * 
	 */
	private MultiValueMap<String, String> setRequestParam() {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		map.add("scope", "public");
		return map;
	}

	/**
	 * @return String
	 * 
	 * 			This method gets the access_token from the json response being sent from apigee
	 * 
	 */
	private String retrieveTokenFromJson(String response) {
		String token = null;
		try {
			final ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
			if (node.has("access_token")) {
				token = node.get("access_token").asText();
			}  
		} catch (IOException e) {
			logger.error("Error retrieving access token from apigee - ", e);
		}
		return token;
	}

}