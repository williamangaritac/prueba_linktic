package com.linktic_test.inventory_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Swagger documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .description("API for managing product inventory in the microservices architecture")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Linktic Development Team")
                                .email("dev@linktic.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082/api/v1")
                                .description("Development server")
                ));
    }
}
