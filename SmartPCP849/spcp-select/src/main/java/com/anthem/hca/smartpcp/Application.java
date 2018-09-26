package com.anthem.hca.smartpcp;

import java.time.ZoneId;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * The Application class starts the spcp-drools Spring Boot application
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
@RefreshScope
public class Application {
	
    public static void main(String[] args) {
	 TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
    	SpringApplication.run(Application.class, args);        
    }

    @Bean
	@RefreshScope
	public RestTemplate restTemplate() {
    	return new RestTemplate();
	}
    
    @PostConstruct
    public void init(){
       TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
    }
}
