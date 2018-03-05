package com.anthem.emep.dckr.microsvc;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Docker MS Prototype!
 *
 */

@SpringBootApplication
@ComponentScan(value = { "com.anthem" })
@RefreshScope
public class Application 
{
	
    public static void main( String[] args )
    {
    	SpringApplication.run(Application.class, args);        
    }
    
    @Bean
    public KieContainer kieContainer(){
    	
    	return KieServices.Factory.get().getKieClasspathContainer();
    }
    
    
}
