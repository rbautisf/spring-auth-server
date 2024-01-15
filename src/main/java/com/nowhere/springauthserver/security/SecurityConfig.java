package com.nowhere.springauthserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] UNSECURED_ENDPOINTS = {
            "/api-docs/**",
            "/swagger-ui/**"
    };
    public static final String BCRYPT_ENCODER_STRATEGY_NAME = "bcrypt";
    private static final int BCRYPT_STRENGTH = 10;

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(Customizer.withDefaults())
                .authorizeHttpRequests(
                        (authorizeRequests) ->
                                authorizeRequests
                                        .requestMatchers(UNSECURED_ENDPOINTS)
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
                )
                .formLogin(Customizer.withDefaults());
        // Accept access tokens for user endpoints
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(BCRYPT_ENCODER_STRATEGY_NAME, new BCryptPasswordEncoder(BCRYPT_STRENGTH));
        return new DelegatingPasswordEncoder(BCRYPT_ENCODER_STRATEGY_NAME, encoders);
    }


}
