package com.nowhere.springauthserver.security.converter;

import java.time.Duration;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.oidc.OidcClientRegistration;
import org.springframework.security.oauth2.server.authorization.oidc.converter.OidcClientRegistrationRegisteredClientConverter;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.CollectionUtils;

/**
 * RegisteredClientConverterCustom is a custom converter for OidcClientRegistration to RegisteredClient.
 * It extends the OidcClientRegistrationRegisteredClientConverter and adds custom client metadata to the client settings.
 *
 */
public class RegisteredClientConverterCustom implements Converter<OidcClientRegistration, RegisteredClient> {

    private final List<String> customClientMetadata;
    private final OidcClientRegistrationRegisteredClientConverter delegate;

    public RegisteredClientConverterCustom(List<String> customClientMetadata) {
        this(customClientMetadata, new OidcClientRegistrationRegisteredClientConverter());
    }

    public RegisteredClientConverterCustom(List<String> customClientMetadata, OidcClientRegistrationRegisteredClientConverter delegate) {
        this.customClientMetadata = customClientMetadata;
        this.delegate = delegate;
    }

    @Override
    public RegisteredClient convert(OidcClientRegistration clientRegistration) {
        RegisteredClient registeredClient = this.delegate.convert(clientRegistration);
        assert registeredClient != null;
        ClientSettings.Builder clientSettingsBuilder = ClientSettings.withSettings(
                registeredClient.getClientSettings().getSettings());
        if (!CollectionUtils.isEmpty(this.customClientMetadata)) {
            clientRegistration.getClaims().forEach((claim, value) -> {
                if (this.customClientMetadata.contains(claim)) {
                    clientSettingsBuilder.setting(claim, value);
                }
            });
        }

        return RegisteredClient.from(registeredClient)
                .clientSettings(clientSettingsBuilder.build())
                .tokenSettings(
                        TokenSettings.builder()
                                .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
                                .accessTokenTimeToLive(Duration.ofHours(24))
                                .build()
                )
                .build();
    }
}