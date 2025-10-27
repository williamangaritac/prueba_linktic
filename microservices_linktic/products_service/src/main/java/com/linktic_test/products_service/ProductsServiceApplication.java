package com.linktic_test.products_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(
		info = @Info(
				title = "Products Service API",
				version = "1.0.0",
				description = "API for managing products in the microservices architecture",
				contact = @Contact(
						name = "Linktic Development Team",
						email = "dev@linktic.com"
				)
		),
		servers = {
				@Server(url = "http://localhost:8081/api/v1", description = "Local Development Server")
		}
)
public class ProductsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsServiceApplication.class, args);
	}

}
