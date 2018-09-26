/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.swagger;

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
 * SwaggerConfig contains all the configuration details to generate a swagger
 * file.
 * 
 * @author AF71111
 *
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("MDO Pool Builder Microservice")
				.description("Build the PCP Pool for the received member").version("1").build();
	}

	@Bean
	public Docket docket() {

		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).pathMapping("/").select()
				.apis(RequestHandlerSelectors.basePackage("com.anthem.hca.smartpcp.mdo.pool.controller"))
				.paths(PathSelectors.any()).build();
	}

}
