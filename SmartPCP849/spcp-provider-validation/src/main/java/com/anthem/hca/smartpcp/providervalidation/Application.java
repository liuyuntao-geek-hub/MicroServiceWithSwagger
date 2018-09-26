/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * 
 * @author AF69961
 */
package com.anthem.hca.smartpcp.providervalidation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import org.springframework.context.annotation.Bean;

/**
 * Docker MS Prototype!
 *
 */
@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
@RefreshScope
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
