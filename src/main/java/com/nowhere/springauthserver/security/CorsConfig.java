package com.nowhere.springauthserver.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
public class CorsConfig {
    private static final List<String> ALLOWED_HEADERS = List.of(
            "Access-Control-Allow-Origin",
            "x-requested-with",
            "Authorization"
    );
    private static final List<String> ALLOWED_METHODS = List.of("POST");
    private static final List<String> ALLOWED_ALL = List.of("http://localhost:9001", "http:localhost:9000");

    /**
     * CORS configuration for the Authorization Server.
     *
     * @return the {@link CorsConfigurationSource}
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_ALL);
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
