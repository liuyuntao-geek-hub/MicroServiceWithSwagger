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

/**
 * The SwaggerConfig class is used to store configuration for Swagger Documentation page.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	/**
	 * This method gets the API Information of the Swagger Documentation page.
	 * 
	 * @param  None
	 * @return The API Info
	 * @see    ApiInfo
	 * @see    ApiInfoBuilder
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Rules Engine MicroService API").description("Rules Engine MicroService for SmartPCP, Provider Validation (Affinity & MDO), MDO Pooling and MDO Scoring").version("1").build();
	}

	/**
	 * This method creates a Docket Bean.
	 * 
	 * @param  None
	 * @return Docket Bean
	 * @see    Docket
	 * @see    DocumentationType
	 * @see    RequestHandlerSelectors
	 */
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
