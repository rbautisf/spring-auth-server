package com.nowhere.springauthserver.security.converter;

import java.util.List;
import java.util.function.Consumer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientConfigurationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationProvider;

public class ClientMetadataConfigCustom {
    public static Consumer<List<AuthenticationProvider>> configureCustomClientMetadataConverters() {
        List<String> customClientMetadata = List.of("logo_uri", "contacts");

        var registeredClientConverter = new RegisteredClientConverterCustom(customClientMetadata);
        var clientRegistrationConverter = new ClientRegistrationConverterCustom(customClientMetadata);

        return (authenticationProviders) -> {
            authenticationProviders.forEach(authProvider -> {
                switch(authProvider){
                    case OidcClientRegistrationAuthenticationProvider provider -> {
                        provider.setRegisteredClientConverter(registeredClientConverter);
                        provider.setClientRegistrationConverter(clientRegistrationConverter);
                    }
                    case OidcClientConfigurationAuthenticationProvider provider -> {
                        provider.setClientRegistrationConverter(clientRegistrationConverter);
                    }
                    default -> {}
                }
            });
        };
    }

}
