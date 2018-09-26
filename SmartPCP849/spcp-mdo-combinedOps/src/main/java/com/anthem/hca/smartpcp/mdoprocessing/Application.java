package com.anthem.hca.smartpcp.mdoprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Docker MS Prototype!
 *
 */

@SpringBootApplication
@Configuration
@ComponentScan("com.anthem")
@RefreshScope
@EnableHystrix
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
