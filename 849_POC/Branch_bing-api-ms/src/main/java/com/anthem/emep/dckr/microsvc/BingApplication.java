package com.anthem.emep.dckr.microsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

/**
 * Docker MS Prototype!
 *
 */

@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
@RefreshScope
public class BingApplication 
{
	
	@Value("${defaultConnectionTimeout}")
    private int connectionTimeout;
	
	@Value("${defaultReadTimeout}")
    private int readTimeout;
	
    public static void main( String[] args )
    {
    	SpringApplication.run(BingApplication.class, args);        
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
