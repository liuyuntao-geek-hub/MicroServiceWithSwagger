package com.anthem.hca.smartpcp.util;

import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.constants.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The OAuthAccessTokenConfig class is the Component Layer implementation 
 * of retrieving the access_token from apigee.
 * 
 * @version 1.8
 */
@Component
@RefreshScope
public class OAuthAccessTokenConfig {

    @Value("${security.client.id}")
	private String clientId;
	
    @Value("${security.secret.key}")
	private String secretKey;
	
    @Value("${security.api.key}")
	private String apiKey;
	
    @Value("${security.token.uri}")
	private String tokenUri;
    
    private HttpHeaders headers;
    
    @Autowired
   	private RestTemplate restTemplate;
    
    @Autowired
    private RetryTemplate retryTemplate;
    
    private Logger logger = LoggerFactory.getLogger(OAuthAccessTokenConfig.class);
	
    /**
	 * This method is called from the scheduler component TokenRetrieveScheduler
	 * to get the access token from apigee. If for any  reason  apigee  can  not
	 * serve the access_token then it will retry three times  to  get  the  same
	 * from apigee. In case of any exception it will return null value as  token
	 * 
	 * @return    String    - access token
	 * @exception Exception - may occur from apigee side
	 */
	public String fetchAccessToken() {
    	return retryTemplate.execute(retryContext -> {
    		ResponseEntity<String> response = restTemplate.exchange(
					tokenUri,
					HttpMethod.POST,
					buildRequestForAccessToekn(),
					String.class);
    		String accessToken = retrieveTokenFromJson(response.getBody());
    		logger.debug("Access Token retreived successfully {}","");
    		return accessToken;
		}, new RecoveryCallback<String>() {
			@Override
			public String recover(RetryContext context) throws Exception {
				logger.error("Error retrieving access token from apigee", context.getLastThrowable());
				return "";
			}
		});
	}
	
    /**
	 * This method prepares the header entity with basic authentication parameters
	 * to pass it on to get the token from apigee 
	 * 
	 * @return HttpEntity
	 */
	private HttpEntity<MultiValueMap<String, String>> buildRequestForAccessToekn() {
		HttpHeaders basicHeaders = new HttpHeaders();
		basicHeaders.add(HttpHeaders.AUTHORIZATION, Constants.BASIC + " " + encodeClientCredentials());
		basicHeaders.add(Constants.API_KEY, apiKey);
		basicHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return new HttpEntity<>(setRequestParam(), basicHeaders);
	}
	
    /**
	 * This method builds the header with the access_token being retrieving from apigee
	 * to pass it on while calling the apigee url of other micro service. If  the token
	 * is null by any means then one more attempt is made to hit the  apigee  and   the 
	 * header is built with the new token.
	 * 
	 * This method is called from the scheduler component TokenRetrieveScheduler before 
	 * the access token expires.
	 * 
	 * @param token - token fetched from apigee
	 * 
	 * @return HttpHeaders
	 */
	public HttpHeaders buildHttpHeadersWithAccessToken(String token) {
		if(StringUtils.isEmpty(token)) {
			ResponseEntity<String> response = null;
			try {
				response = restTemplate.exchange(
						tokenUri,
						HttpMethod.POST,
						buildRequestForAccessToekn(),
						String.class);
				token = retrieveTokenFromJson(response.getBody());
			} catch (Exception e) {
				logger.error("Error retrieving access token from apigee", e);
			}
		}
		HttpHeaders oauthHeaders = new HttpHeaders();
		oauthHeaders.add(HttpHeaders.AUTHORIZATION, Constants.BEARER + " " + token);
		oauthHeaders.add(Constants.API_KEY, apiKey);
		oauthHeaders.setContentType(MediaType.APPLICATION_JSON);
		headers = oauthHeaders;
		return headers;
	}
	
	/**
	 * This method encrypts the client id and secret key for authentication
	 * with apigee
	 * 
	 * @return String - returns the encoded client credential
	 */
	private String encodeClientCredentials() {
		String cred = clientId + ":" + secretKey;
    	return  new String(Base64.getEncoder().encode(cred.getBytes()));
	}
	
	/**
	 * This method sets the grant type and provides the scope for authentication
	 * with apigee
	 * 
	 * @return MultiValueMap<String, String>
	 */
	private MultiValueMap<String, String> setRequestParam() {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
		map.add(Constants.GRANT_TYPE, Constants.CLIENT_CREDENTIALS);
		map.add(Constants.SCOPE, Constants.PUBLIC);
		return map;
	}
	
	/**
	 * This method gets the access_token from the json response being sent from
	 * apigee
	 * 
	 * @return String
	 */
	private String retrieveTokenFromJson(String response) {
		String token = "";
		try {
			final ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
			if (node.has(Constants.ACCESS_TOKEN)) {
				token = node.get(Constants.ACCESS_TOKEN).asText();
			}  
		} catch (IOException e) {
		    logger.error("Error reading the token from json node", e);
		}
		return token;
	}
	
	/**
	 * This method returns the header information with token details. It is  being 
	 * called while invoking other micro service. The scheduler updates the header
	 * before the token expires
	 * 
	 * @return HttpHeaders
	 */
	public HttpHeaders getHeaders() {
		return headers;
	}

}
