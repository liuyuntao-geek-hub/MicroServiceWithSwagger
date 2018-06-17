package com.anthem.hca.smartpcp.drools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
@RefreshScope
public class Application {

    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);        
    }

    @Bean	
	@RefreshScope
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
