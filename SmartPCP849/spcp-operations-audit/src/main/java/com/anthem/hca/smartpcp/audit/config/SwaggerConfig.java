package com.anthem.hca.smartpcp.audit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			SwaggerConfig contains all the configuration details to generate a swagger file.
 * 
 * @author AF56159 
 */

@EnableSwagger2
@Configuration
public class SwaggerConfig {
	
	private ApiInfo apiInfo(){
		return new ApiInfoBuilder()
				.title("Smart PCP Operations Audit Microservice")
				.description("Log microservice operations into SQL Server table")
				.version("1.0.0")
				.build();
	}
	
	@Bean
	public Docket docket(){
		
		return new Docket(DocumentationType.SWAGGER_2)
				.useDefaultResponseMessages(false)
				.apiInfo(apiInfo())
				.pathMapping("/spcp-operations-audit")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.anthem.hca.smartpcp.audit.controller"))
				.paths(PathSelectors.any())
				.build();
	}

}
