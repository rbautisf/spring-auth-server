package com.nowhere.springauthserver.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nowhere.springauthserver.security.converter.ClientRegistrationConverterCustom;
import com.nowhere.springauthserver.security.converter.RegisteredClientConverterCustom;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.converter.OidcClientRegistrationRegisteredClientConverter;
import org.springframework.security.oauth2.server.authorization.oidc.converter.RegisteredClientOidcClientRegistrationConverter;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import static com.nowhere.springauthserver.security.SecurityConstants.CONSENT_PAGE_URI_CUSTOM;
import static com.nowhere.springauthserver.security.SecurityConstants.LOGIN_PATH;

/**
 * The Authorization Server Configuration.
 * Is responsible for issuing access tokens to the client after successfully authenticating the resource owner and obtaining authorization.
 * The Authorization Server is a role defined in the OAuth 2.0 Authorization Framework.
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    private final List<String> registeredClientMetadataCustomClaims = List.of("logo_uri", "contacts", "application_type", "environment");

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

        OAuth2AuthorizationServerConfigurer configurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
        configurer.authorizationEndpoint(authEndpoint -> authEndpoint.consentPage(CONSENT_PAGE_URI_CUSTOM));
        applyOidcConfiguration(configurer);

        http.cors(Customizer.withDefaults()); // require  CorsConfigurationSource bean
        http.csrf(Customizer.withDefaults());
        http.exceptionHandling(exceptionHandlingCustomizer());
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * Applies the OIDC (OpenID Connect) configuration to the provided OAuth2AuthorizationServerConfigurer.
     *
     * @param configurer the OAuth2AuthorizationServerConfigurer to apply the OIDC configuration to
     */
    private void applyOidcConfiguration(OAuth2AuthorizationServerConfigurer configurer) {
        var oidcClientRegistrationRegisteredClientConverter = new OidcClientRegistrationRegisteredClientConverter();
        var oidcClientMetadataConfigurer = getOidcClientMetadataConfigurer(oidcClientRegistrationRegisteredClientConverter);
        configurer.oidc(oidc -> {
            // By design OIDC only supports client registration https://openid.net/specs/openid-connect-registration-1_0.html
            oidc.clientRegistrationEndpoint(clientRegistrationEndpoint -> {
                clientRegistrationEndpoint.authenticationProviders(oidcClientMetadataConfigurer);
            });
        });
    }

    private OidcClientMetadataConfigurer getOidcClientMetadataConfigurer(OidcClientRegistrationRegisteredClientConverter oidcClientRegistrationRegisteredClientConverter) {
        var registeredClientOidcClientRegistrationConverter = new RegisteredClientOidcClientRegistrationConverter();
        var registeredClientConverterCustom = new RegisteredClientConverterCustom(registeredClientMetadataCustomClaims, oidcClientRegistrationRegisteredClientConverter);
        var clientRegistrationConverterCustom = new ClientRegistrationConverterCustom(registeredClientMetadataCustomClaims, registeredClientOidcClientRegistrationConverter);
        var oidcClientMetadataConfigurer = new OidcClientMetadataConfigurer(registeredClientConverterCustom, clientRegistrationConverterCustom);
        return oidcClientMetadataConfigurer;
    }

    /**
     * Returns a customizer for exception handling configuration in the HttpSecurity.
     *
     * @return a Customizer object for exception handling configuration
     */
    private Customizer<ExceptionHandlingConfigurer<HttpSecurity>> exceptionHandlingCustomizer() {
        return exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint(LOGIN_PATH),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        );
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

}
