package com.anthem.hca.smartpcp.track.audit;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Docker MS Prototype!
 *
 */

@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
@RefreshScope
@RestController
public class Application {

	@Value("${defaultConnectionTimeout}")
	private int connectionTimeout;

	@Value("${defaultReadTimeout}")
	private int readTimeout;

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
		SpringApplication.run(Application.class, args);
	}

	@Bean
	@RefreshScope
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(connectionTimeout);
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(readTimeout);
		return restTemplate;
	}
}
