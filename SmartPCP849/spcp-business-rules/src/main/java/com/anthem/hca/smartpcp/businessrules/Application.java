package com.anthem.hca.smartpcp.businessrules;

import java.time.ZoneId;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

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
