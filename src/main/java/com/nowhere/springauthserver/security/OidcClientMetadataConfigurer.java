package com.nowhere.springauthserver.security;

import com.nowhere.springauthserver.security.converter.ClientRegistrationConverterCustom;
import com.nowhere.springauthserver.security.converter.RegisteredClientConverterCustom;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientConfigurationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationProvider;

/**
 * Configure custom client metadata converters for OidcClientRegistrationAuthenticationProvider and
 * OidcClientConfigurationAuthenticationProvider.
 * For more details on the client metadata please refer to the following link: https://openid.net/specs/openid-connect-registration-1_0.html#ClientMetadata
 */
public class OidcClientMetadataConfigurer implements Consumer<List<AuthenticationProvider>> {

    public OidcClientMetadataConfigurer(
            RegisteredClientConverterCustom registeredClientConverter,
            ClientRegistrationConverterCustom clientRegistrationConverter) {
        this.registeredClientConverter = registeredClientConverter;
        this.clientRegistrationConverter = clientRegistrationConverter;
    }

    public OidcClientMetadataConfigurer(List<String> customClientMetadata) {
        this(new RegisteredClientConverterCustom(customClientMetadata),
                new ClientRegistrationConverterCustom(customClientMetadata));
    }

    private final RegisteredClientConverterCustom registeredClientConverter;
    private final ClientRegistrationConverterCustom clientRegistrationConverter;


    @Override
    public void accept(List<AuthenticationProvider> authenticationProviders) {
        authenticationProviders.forEach(authProvider -> assignConverters(authProvider));
    }

    private void assignConverters(AuthenticationProvider authProvider) {
        switch (authProvider) {
            case OidcClientRegistrationAuthenticationProvider provider -> {
                provider.setRegisteredClientConverter(registeredClientConverter);
                provider.setClientRegistrationConverter(clientRegistrationConverter);
            }
            case OidcClientConfigurationAuthenticationProvider provider ->
                    provider.setClientRegistrationConverter(clientRegistrationConverter);
            default -> {
            }
        }
    }
}
