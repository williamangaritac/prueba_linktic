package com.linktic_test.notifications_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.compatibility-verifier.enabled=false",
    "eureka.client.enabled=false"
})
class NotificationsServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}

