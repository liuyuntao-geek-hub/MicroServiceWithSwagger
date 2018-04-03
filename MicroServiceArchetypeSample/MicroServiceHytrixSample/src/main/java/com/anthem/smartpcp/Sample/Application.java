package com.anthem.smartpcp.Sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * Docker MS Prototype!
 *
 */

@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
@RefreshScope
@RestController
@EnableCircuitBreaker
@EnableHystrixDashboard
public class Application 
{
	
	@Value("${defaultConnectionTimeout}")
    private int connectionTimeout;
	
	@Value("${defaultReadTimeout}")
    private int readTimeout;
	
    public static void main( String[] args )
    {
    	SpringApplication.run(Application.class, args);        
    }
    
    @RequestMapping("/")    
	public String welcome() {		
		return "Hello world - From spring boot";
	}
    
    @RequestMapping("/testhystrix") 
    @HystrixCommand(fallbackMethod = "testHystrixFallback")
   	public String testHystrix() {	

    	URI uri = URI.create("");
    	return this.restTemplate().getForObject(uri, String.class);
   		//return "Actual response";
   	}
    
    public String testHystrixFallback() {		
   		return "Fallback response";
   	}
    
    @Bean	
	@RefreshScope
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(connectionTimeout);
		((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setReadTimeout(readTimeout);			
		return restTemplate;
	}
    
    
}
