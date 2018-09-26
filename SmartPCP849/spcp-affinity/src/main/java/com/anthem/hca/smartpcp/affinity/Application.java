package com.anthem.hca.smartpcp.affinity;

import java.time.ZoneId;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			Application is used to start Affinity application
 * 
 * @author AF65409 
 */
 
@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
@RefreshScope
@EnableCircuitBreaker
public class Application {

	@Value("${defaultConnectionTimeout}")
	private int connectionTimeout;
	@Value("${defaultReadTimeout}")
	private int readTimeout;

	public static void main( String[] args ) {
		SpringApplication.run(Application.class, args);        
	}

	@Bean	
	@RefreshScope
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(connectionTimeout);
		((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setReadTimeout(readTimeout);			
		return restTemplate;
	}
	
	@PostConstruct
    public void init(){
       TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
    }
}