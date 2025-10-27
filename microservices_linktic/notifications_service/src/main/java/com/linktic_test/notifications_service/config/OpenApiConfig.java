package com.linktic_test.notifications_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI notificationsServiceOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort + "/api/v1");
        server.setDescription("Notifications Service - Development");

        Contact contact = new Contact();
        contact.setEmail("contacto@linktic.com");
        contact.setName("LINKTIC Support");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Notifications Service API")
                .version("1.0.0")
                .contact(contact)
                .description("API REST para el microservicio de notificaciones. " +
                        "Este servicio escucha eventos de Kafka cuando se crean órdenes " +
                        "y envía notificaciones por email a los clientes.")
                .termsOfService("https://www.linktic.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}

