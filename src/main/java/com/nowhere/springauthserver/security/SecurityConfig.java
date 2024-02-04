package com.nowhere.springauthserver.security;

import com.nowhere.springauthserver.security.converter.JwtAuthenticationConverterCustom;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/actuator/**",
            "/api-docs/**",
            "/swagger-ui/**"
    };

    /**
     * The default Security Filter Chain is responsible for processing all incoming requests to the application.
     * Configure the jwtAuthenticationConverter to use the custom JwtAuthenticationConverterCustom.
     *
     *
     * @param http the {@link HttpSecurity} to use
     * @return the {@link SecurityFilterChain}
     * @throws Exception if an error occurs
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // set the jwtAuthenticationConverter to the default jwtAuthenticationConverter
        http
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer)
                .formLogin(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2ResourceServerCustomizer);
        return http.build();
    }

    private final Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> oauth2ResourceServerCustomizer = oauth2ResourceServer -> {
        oauth2ResourceServer.jwt(jwt -> {
            jwt.jwtAuthenticationConverter(new JwtAuthenticationConverterCustom());
        });
    };
    private final Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestsCustomizer = (authorizeRequests) -> {
        authorizeRequests
                .requestMatchers(AUTH_WHITELIST)
                .permitAll()
                .anyRequest()
                .authenticated();
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(
                SecurityConstants.BCRYPT_ENCODER_STRATEGY_NAME,
                new BCryptPasswordEncoder(SecurityConstants.BCRYPT_STRENGTH));
        return new DelegatingPasswordEncoder(SecurityConstants.BCRYPT_ENCODER_STRATEGY_NAME, encoders);
    }


}
