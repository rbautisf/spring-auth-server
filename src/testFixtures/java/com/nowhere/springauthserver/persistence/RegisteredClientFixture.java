package com.nowhere.springauthserver.persistence;

import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.oidc.OidcClientRegistration;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

public class RegisteredClientFixture {
    public static RegisteredClient.Builder builderRegisteredClientWithDefaultValues() {
        return builderRegisteredClientWith(builderDefaultClientSettings(), builderDefaultTokenSettings());
    }

    public static RegisteredClient.Builder builderRegisteredClientWith(
            ClientSettings.Builder clientSettings,
            TokenSettings.Builder tokenSettings
    ) {
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(UUID.randomUUID().toString())
                .clientSecret(UUID.randomUUID().toString())
                .clientSettings(clientSettings.build())
                .tokenSettings(tokenSettings.build())
                .clientName("testClient")
                .redirectUris(uri -> uri.add("http://localhost:8080/redirect"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/redirec")
                .postLogoutRedirectUri("http://localhost:8080/post-logout")
                .scope("openid");
    }

    public static OidcClientRegistration.Builder builderOidcClientRegistration() {
        return OidcClientRegistration.builder()
                .clientId(UUID.randomUUID().toString())
                .clientIdIssuedAt(Instant.now())
                .clientName("testClient")
                .clientSecret(UUID.randomUUID().toString())
                .redirectUri("http://localhost:8080/redirect")
                .responseType(OAuth2AuthorizationResponseType.CODE.getValue())
                .scope("openid")
                .tokenEndpointAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue())
                .idTokenSignedResponseAlgorithm(SignatureAlgorithm.RS256.getName())
                .tokenEndpointAuthenticationSigningAlgorithm(SignatureAlgorithm.RS256.getName());
    }

    public static ClientSettings.Builder builderDefaultClientSettings() {
        return ClientSettings.builder()
                .requireProofKey(true)
                .requireAuthorizationConsent(true);
    }

    public static TokenSettings.Builder builderDefaultTokenSettings() {
        return TokenSettings.builder()
                .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256);
    }
}