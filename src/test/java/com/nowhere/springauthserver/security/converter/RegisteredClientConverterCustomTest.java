package com.nowhere.springauthserver.security.converter;

import com.nowhere.springauthserver.persistence.RegisteredClientFixture;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.oidc.converter.OidcClientRegistrationRegisteredClientConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegisteredClientConverterCustomTest {
    final OidcClientRegistrationRegisteredClientConverter delegate = mock(OidcClientRegistrationRegisteredClientConverter.class);

    @Test
    void testConvertWithValidCustomClientMetadata() {
        List<String> metadata = List.of("customMetadata");
        var converter = new RegisteredClientConverterCustom(metadata, new OidcClientRegistrationRegisteredClientConverter());

        var clientRegistration = RegisteredClientFixture.builderOidcClientRegistration()
                .claim("customMetadata", "test-value").build();

        RegisteredClient registered = converter.convert(clientRegistration);

        assertNotNull(registered);
        assertEquals("test-value", registered.getClientSettings().getSettings().get("customMetadata"));
    }


    @Test
    void testConvertWithEmptyCustomClientMetadata() {
        List<String> metadata = List.of();
        var converter = new RegisteredClientConverterCustom(metadata, delegate);

        when(delegate.convert(any()))
                .thenReturn(RegisteredClientFixture.builderRegisteredClientWithDefaultValues().build());

        var clientRegistration = RegisteredClientFixture
                .builderOidcClientRegistration()
                .claim("customMetadata", "test-value")
                .build();

        RegisteredClient registered = converter.convert(clientRegistration);


        assertNotNull(registered);
        assertNull(registered.getClientSettings().getSettings().get("customMetadata"));
    }
}
