package com.anthem.hca.smartpcp.configServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Docker MS Prototype!
 *
 */

@SpringBootApplication
@EnableConfigServer
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
