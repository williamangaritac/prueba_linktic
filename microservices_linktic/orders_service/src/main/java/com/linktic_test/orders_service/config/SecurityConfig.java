package com.linktic_test.orders_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el microservicio
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
            )
            .authorizeHttpRequests(authz -> authz
                // Permitir acceso público a endpoints de documentación (con y sin context-path)
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/webjars/**",
                    "/api/v1/swagger-ui/**",
                    "/api/v1/swagger-ui.html",
                    "/api/v1/v3/api-docs/**",
                    "/api/v1/webjars/**",
                    "/api/v1/api-docs/**"
                ).permitAll()
                // Permitir acceso público a endpoints de salud y métricas
                .requestMatchers(
                    "/actuator/**",
                    "/api/v1/actuator/**",
                    "/api/v1/orders/status"
                ).permitAll()
                // Permitir acceso público a todos los endpoints por ahora (desarrollo)
                .requestMatchers("/api/v1/**").permitAll()
                // Cualquier otra petición requiere autenticación
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
