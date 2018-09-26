package com.anthem.hca.smartpcp.util;

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
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - SwaggerConfig contains all the configuration details to generate a swagger file.
 * 
 * 
 * @author AF71111
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Smart PCP Select Microservice")
				.description("Smart Select PCP for the received Member").version("1").build();
	}

	@Bean
	public Docket docket() {

		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).pathMapping("/").select()
				.apis(RequestHandlerSelectors.basePackage("com.anthem.hca.smartpcp.controller"))
				.paths(PathSelectors.any()).build();
	}

}