package com.linktic_test.notifications_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Notifications Service Application
 * Microservicio para gestionar notificaciones por email basadas en eventos de Kafka
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@EnableAsync
public class NotificationsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationsServiceApplication.class, args);
	}

}

