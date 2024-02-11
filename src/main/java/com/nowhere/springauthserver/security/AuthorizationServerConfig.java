package com.nowhere.springauthserver.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
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

import static com.nowhere.springauthserver.security.SecurityConstants.ACCESS_TOKEN_VALUE;
import static com.nowhere.springauthserver.security.SecurityConstants.CONSENT_PAGE_URI_CUSTOM;
import static com.nowhere.springauthserver.security.SecurityConstants.ID_TOKEN_VALUE;
import static com.nowhere.springauthserver.security.SecurityConstants.LOGIN_PATH;
import static com.nowhere.springauthserver.security.SecurityConstants.ROLES_CLAIM;
import static com.nowhere.springauthserver.security.converter.ClientMetadataConfigCustom.configureCustomClientMetadataConverters;

/**
 * The Authorization Server Configuration.
 * Is responsible for issuing access tokens to the client after successfully authenticating the resource owner and obtaining authorization.
 * The Authorization Server is a role defined in the OAuth 2.0 Authorization Framework.
 *
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    /**
     * The Authorization Server Security Filter Chain is responsible for processing
     * all incoming requests to the Authorization Server.
     *
     * @param http the {@link HttpSecurity} to use
     * @return the {@link SecurityFilterChain}
     * @throws Exception if an error occurs
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationEndpoint(authEndpoint -> authEndpoint.consentPage(CONSENT_PAGE_URI_CUSTOM))
                // OpenID Connect 1.0

                .oidc(oidc->{
                    // By design OIDC only supports client registration https://openid.net/specs/openid-connect-registration-1_0.html
                    oidc.clientRegistrationEndpoint(clientRegistrationEndpoint -> {
                        clientRegistrationEndpoint.authenticationProviders(configureCustomClientMetadataConverters());
                    });
                });

        http.cors(Customizer.withDefaults());

        http.csrf(Customizer.withDefaults());

        http.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint(LOGIN_PATH),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        ));

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    /**
     * The Authorization Server Settings for endpoints.
     *
     * @return the {@link AuthorizationServerSettings}
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * The JWK Source for the Authorization Server using the configured RSA key pair.
     *
     * @param keyPair the {@link RsaKeyProperties}
     * @return the {@link JWKSource}
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(RsaKeyProperties keyPair) {
        var rsaKey = new RSAKey.Builder(keyPair.publicKey()).privateKey(keyPair.privateKey()).build();
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    /**
     * The JWT Decoder for the Authorization Server using the JWK Source.
     *
     * @param jwkSource the {@link JWKSource}
     * @return the {@link JwtDecoder}
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Customizes the JWT for the Authorization Server.
     * This customizer adds the token type claim to the JWT.
     * The token type claim is used to indicate the type of token.
     * The token type claim is set to "access_token" for access tokens and "id_token" for ID tokens.
     * Additionally, the customizer adds the roles claim to the access token.
     * @return the {@link OAuth2TokenCustomizer}
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return (JwtEncodingContext context) -> {
            Authentication principal = context.getPrincipal();
            if (Objects.equals(context.getTokenType().getValue(), OidcParameterNames.ID_TOKEN)) {
                context.getClaims().claim(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, ID_TOKEN_VALUE);
            } else if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
                context.getClaims().claims((claims) -> {
                    claims.put(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, ACCESS_TOKEN_VALUE);
                    Set<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
                    claims.put(ROLES_CLAIM, roles);
                });
            }
        };
    }
}
