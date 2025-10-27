package com.linktic_test.orders_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Orders Service API")
                        .description("Microservicio de gestión de órdenes - Sistema de E-commerce")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Linktic Test Team")
                                .email("support@linktic.com")
                                .url("https://linktic.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083/api/v1")
                                .description("Servidor de desarrollo local"),
                        new Server()
                                .url("https://api.linktic.com/orders/v1")
                                .description("Servidor de producción")
                ));
    }
}
