package com.anthem.hca.smartpcp.mdoprocessing.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
/**
 * The RetryTemplateConfig class is the Configuration Level implementation 
 * for setting the required attributes to retry getting the token from apigee
 * in case the same is down for any reason
 * 
 * @version 1.8
 */
@Configuration
public class RetryTemplateConfig {
	
	@Value("${retry.timeout}")
	private int timeout;
	
	@Value("${retry.delay}")
	private int delayPeriod;
	
	@Value("${retry.attempts}")
	private int attempts;
	
	 /**
	 * RetryTemplate acts a bean and available through out the drools module.
	 * It has got three property configured.
	 * backOffPeriod - this is known as delay period for retrying the service call
	 * attempts      - number of attempts has to be made to call the service
	 * timeOut       - within the specified timeout the retry event will occur  
	 * 
	 * @return RetryTemplate  -  header with access token
	*/
	@Bean
	public RetryTemplate retryTemplate() {
		final FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(delayPeriod);

		final SimpleRetryPolicy attemptsPolicy = new SimpleRetryPolicy();
		attemptsPolicy.setMaxAttempts(attempts);
		final TimeoutRetryPolicy timeoutPolicy = new TimeoutRetryPolicy();
		timeoutPolicy.setTimeout(timeout);
		
		final CompositeRetryPolicy retryPolicy = new CompositeRetryPolicy();
		retryPolicy.setPolicies(new RetryPolicy[] { timeoutPolicy, attemptsPolicy });

		final RetryTemplate template = new RetryTemplate();
		template.setBackOffPolicy(backOffPolicy);
		template.setRetryPolicy(retryPolicy);
		
		return template;
	}
}
