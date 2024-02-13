package com.nowhere.springauthserver.security;

import com.nowhere.springauthserver.security.converter.JwtAuthenticationConverterCustom;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.nowhere.springauthserver.security.SecurityConstants.ACTUATOR_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.ANY_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.ASSETS_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.BCRYPT_ENCODER_STRATEGY_NAME;
import static com.nowhere.springauthserver.security.SecurityConstants.BCRYPT_STRENGTH;
import static com.nowhere.springauthserver.security.SecurityConstants.LOGIN_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.SIGN_UP_PATH;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {
    //value swagger path
    @Value("${springdoc.swagger-ui.path}")
    private String SWAGGER_PATH;
    @Value("${springdoc.api-docs.path}")
    private String API_DOCS_PATH;

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
        http
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer)
                .formLogin(formLogin ->
                        formLogin
                                .loginPage(LOGIN_PATH)
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage(LOGIN_PATH)
                )
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
                .requestMatchers(
                        LOGIN_PATH,
                        SIGN_UP_PATH,
                        SWAGGER_PATH+ANY_PATH,
                        API_DOCS_PATH+ANY_PATH,
                        ASSETS_PATH,
                        ACTUATOR_PATH
                )
                .permitAll()
                .anyRequest()
                .authenticated();
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(
                BCRYPT_ENCODER_STRATEGY_NAME,
                new BCryptPasswordEncoder(BCRYPT_STRENGTH));
        return new DelegatingPasswordEncoder(BCRYPT_ENCODER_STRATEGY_NAME, encoders);
    }


}
