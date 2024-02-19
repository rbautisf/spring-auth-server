package com.nowhere.springauthserver.security;

import com.nowhere.springauthserver.security.converter.JwtAuthenticationConverterCustom;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
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

import static com.nowhere.springauthserver.security.SecurityConstants.ACTUATOR_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.ANY_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.ASSETS_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.BCRYPT_ENCODER_STRATEGY_NAME;
import static com.nowhere.springauthserver.security.SecurityConstants.BCRYPT_STRENGTH;
import static com.nowhere.springauthserver.security.SecurityConstants.LOGIN_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.REGISTER_USER_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.SIGNUP_PATH;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;
    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;

    /**
     * The default Security Filter Chain is responsible for processing all incoming requests to the application.
     * Configure the jwtAuthenticationConverter to use the custom JwtAuthenticationConverterCustom.
     *
     * @param http the {@link HttpSecurity} to use
     * @return the {@link SecurityFilterChain}
     * @throws Exception if an error occurs
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequestsCustomizer)
                .formLogin(formLogin -> formLogin.loginPage(LOGIN_PATH))
                .oauth2Login(oauth2Login -> oauth2Login.loginPage(LOGIN_PATH))
                .cors(Customizer.withDefaults())// required  CorsConfigurationSource bean
                .oauth2ResourceServer(oauth2ResourceServerCustomizer);
        return http.build();
    }

    /**
     * The variable oauth2ResourceServerCustomizer is an instance of Customizer interface for configuring an OAuth2ResourceServerConfigurer for HttpSecurity.
     *
     * It is used to customize the OAuth2 resource server configuration by configuring the JwtAuthenticationConverter and its conversion process for Jwt tokens.
     * It sets the jwtAuthenticationConverter to an instance of JwtAuthenticationConverterCustom which extends JwtAuthenticationConverter and adds custom roles to the authorities
     *.
     */
    private final Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> oauth2ResourceServerCustomizer = oauth2ResourceServer -> {
        Converter<Jwt, AbstractAuthenticationToken> jwtConverter = new JwtAuthenticationConverterCustom();
        oauth2ResourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter));
    };

    /**
     * The variable authorizeHttpRequestsCustomizer is used to customize the authorization configuration in the HttpSecurity object.
     * It specifies the request matchers to permit access to specific paths and require authentication for any other request.
     */
    private final Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestsCustomizer = (authorizeRequests) -> {
        authorizeRequests.requestMatchers(
                REGISTER_USER_PATH,
                SIGNUP_PATH,
                LOGIN_PATH,
                swaggerPath + ANY_PATH,
                apiDocsPath + ANY_PATH,
                ASSETS_PATH,
                ACTUATOR_PATH)
                .permitAll().anyRequest().authenticated();
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = Map.of(BCRYPT_ENCODER_STRATEGY_NAME, new BCryptPasswordEncoder(BCRYPT_STRENGTH));
        return new DelegatingPasswordEncoder(BCRYPT_ENCODER_STRATEGY_NAME, encoders);
    }


}
