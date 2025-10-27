package com.linktic_test.orders_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Configuración de WebClient para comunicación con otros microservicios
 */
@Configuration
public class WebClientConfig {

    @Value("${app.inventory-service.url}")
    private String inventoryServiceUrl;

    @Value("${app.products-service.url}")
    private String productsServiceUrl;

    /**
     * WebClient para comunicación con el servicio de inventario
     */
    @Bean("inventoryWebClient")
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl(inventoryServiceUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    /**
     * WebClient para comunicación con el servicio de productos
     */
    @Bean("productsWebClient")
    public WebClient productsWebClient() {
        return WebClient.builder()
                .baseUrl(productsServiceUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    /**
     * WebClient genérico para otras comunicaciones
     */
    @Bean("genericWebClient")
    public WebClient genericWebClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }
}
