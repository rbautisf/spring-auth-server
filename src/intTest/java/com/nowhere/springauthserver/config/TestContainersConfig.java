package com.nowhere.springauthserver.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for integration tests.
 * Provides PostgreSQL and Redis containers for testing.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("oauth_nowhere")
            .withUsername("postgres")
            .withPassword("nowhere")
            .withReuse(true);

    public static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    static {
        postgres.start();
        redis.start();
    }

    /**
     * PostgreSQL container bean for dependency injection if needed.
     */
    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        return postgres;
    }

    /**
     * Redis container bean for dependency injection if needed.
     */
    @Bean
    public GenericContainer<?> redisContainer() {
        return redis;
    }
}