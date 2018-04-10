package com.anthem.hca.smartpcp.drools.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Rule Engine Micro Service").description("Common Rule Engine Micro Service for SmartPCP,Affinity and MDO ").version("1").build();
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
	        		.apiInfo(apiInfo())
	        		.pathMapping("/")
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("com.anthem.hca.smartpcp.drools.controller"))
	                .paths(PathSelectors.any())
	                .build();
	 }

}
