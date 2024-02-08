package com.nowhere.springauthserver.security.converter;

import com.nowhere.springauthserver.persistence.RegisteredClientFixture;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.authorization.oidc.OidcClientRegistration;
import org.springframework.security.oauth2.server.authorization.oidc.converter.RegisteredClientOidcClientRegistrationConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientRegistrationConverterCustomTest {
    private final RegisteredClientOidcClientRegistrationConverter delegate = mock(RegisteredClientOidcClientRegistrationConverter.class);

    @Test
    void testConvert() {
        List<String> customClientMetadata = List.of("customMetadata");
        when(delegate.convert(any())).thenReturn(RegisteredClientFixture.builderOidcClientRegistration().build());
        ClientRegistrationConverterCustom converterCustom = new ClientRegistrationConverterCustom(customClientMetadata, delegate);
        Consumer<Map<String, Object>> clientMetadata = map -> map.put("customMetadata", "testCustomMetadata");
        var fakeRegisteredClient = RegisteredClientFixture.builderRegisteredClientWith(
                RegisteredClientFixture.builderDefaultClientSettings().settings(clientMetadata),
                RegisteredClientFixture.builderDefaultTokenSettings());

        OidcClientRegistration oidcClientRegistration = converterCustom.convert(fakeRegisteredClient.build());

        assertNotNull(oidcClientRegistration);
        assertEquals("testCustomMetadata", oidcClientRegistration.getClaims().get("customMetadata"));
    }

}

