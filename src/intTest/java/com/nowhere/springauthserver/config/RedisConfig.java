package com.nowhere.springauthserver.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

@TestConfiguration
public class RedisConfig {

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        try {
            redisServer =  RedisServer.builder()
                    .port(6379)
                    .setting("maxmemory 128M")
                    .build();
            redisServer.start();
        } catch (Exception e) {
            throw new RuntimeException("Could not start Redis server", e);
        }
    }
    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
    }

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }
}