package com.anthem.hca.smartpcp.audit;

import java.time.ZoneId;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			Application is used to start operations-audit aka transaction microservice application
 * 
 * @author AF56159 
 */
 
@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@PostConstruct
    public void init(){
       TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
    } 
}
