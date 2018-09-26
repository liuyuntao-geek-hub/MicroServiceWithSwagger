package com.anthem.hca.smartpcp.affinity.config;

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
 * @author AF65409 
 */

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Affinity Microservice").description("Member to Provider with Provider Validation Information").version("1").build();
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.pathMapping("/")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.anthem.hca.smartpcp.affinity.controller"))
				.paths(PathSelectors.any())
				.build();
	}

}