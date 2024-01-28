package com.nowherelearn.postservice.security;

import com.nowherelearn.postservice.security.converter.JwtAuthenticationConverterCustom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer)
                .oauth2ResourceServer(oauth2ResourceServerCustomizer);
        return http.build();
    }

    private final Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> oauth2ResourceServerCustomizer = oauth2ResourceServer -> {
        oauth2ResourceServer.jwt(jwt -> {
            jwt.jwtAuthenticationConverter(new JwtAuthenticationConverterCustom());
        });
    };
    private final Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestsCustomizer = authorizeRequests -> {
        authorizeRequests
                .requestMatchers(SecurityConstants.AUTH_WHITELIST).permitAll()
                .requestMatchers(HttpMethod.GET, "/posts")
                .hasAnyAuthority("ROLE_USER")
                .anyRequest().authenticated();
    };

}