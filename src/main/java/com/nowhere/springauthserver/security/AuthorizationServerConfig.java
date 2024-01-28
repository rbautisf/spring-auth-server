package com.nowhere.springauthserver.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
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

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // OpenID Connect 1.0
                .oidc(Customizer.withDefaults());

        http.cors(Customizer.withDefaults());

        http.csrf(Customizer.withDefaults());

        http.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint(SecurityConstants.LOGIN_PATH),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        ));

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RsaKeyProperties keyPair) {
        var rsaKey = new RSAKey.Builder(keyPair.publicKey()).privateKey(keyPair.privateKey()).build();
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return (JwtEncodingContext context) -> {
            Authentication principal = context.getPrincipal();
            if (Objects.equals(context.getTokenType().getValue(), OidcParameterNames.ID_TOKEN)) {
                context.getClaims().claim(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, SecurityConstants.ID_TOKEN_VALUE);
            } else if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
                context.getClaims().claims((claims) -> {
                    claims.put(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, SecurityConstants.ACCESS_TOKEN_VALUE);
                    Set<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
                    claims.put(SecurityConstants.ROLES_CLAIM, roles);
                });
            }
        };
    }
}
