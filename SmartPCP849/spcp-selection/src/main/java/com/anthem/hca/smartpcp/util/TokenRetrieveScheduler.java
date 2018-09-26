package com.anthem.hca.smartpcp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class TokenRetrieveScheduler {
	
	@Value("${spcp.token.delay}")
	private String delayPeriod;
	
	@Autowired
	private OAuthAccessTokenConfig oAuthAccessTokenConfig;
	
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenRetrieveScheduler.class);
    
    /**
	 * This method is called at an configured time interval before the  token
	 * expires which gets the token from apigee and use the same to build the
	 * http header
	 * 
	 */
    @Scheduled(fixedDelayString="${spcp.token.delay}")
    public void retrieveAccessToken() {
    	String token = oAuthAccessTokenConfig.fetchAccessToken();
        oAuthAccessTokenConfig.buildHttpHeadersWithAccessToken(token);
        LOGGER.debug("Scheduler executed to retrieve token after {} milliseconds", delayPeriod);
    }
}
