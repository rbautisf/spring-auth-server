package com.nowhere.springauthserver.config;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(
        initializers = {ContextConfig.class},
        classes = {TestContainersConfig.class}
)
// Spring Boot is used to automatically configure and inject a MockMvc instance into your test class.
@AutoConfigureMockMvc
// Enable TestContainers for this test class
@Testcontainers
public abstract class BaseIntegrationTest {

    /**
     * Configure dynamic properties for TestContainers.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainersConfig.postgres::getJdbcUrl);
        registry.add("spring.datasource.username", TestContainersConfig.postgres::getUsername);
        registry.add("spring.datasource.password", TestContainersConfig.postgres::getPassword);
        registry.add("spring.data.redis.host", TestContainersConfig.redis::getHost);
        registry.add("spring.data.redis.port", TestContainersConfig.redis::getFirstMappedPort);
    }
}