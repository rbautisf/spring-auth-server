package com.nowhere.springauthserver.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import redis.embedded.RedisServer;

@Configuration
@EnableRedisHttpSession
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