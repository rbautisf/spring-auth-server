package com.nowherelearn.postservice.openapi;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.nowherelearn.postservice.openapi.OAuthFlowDetails.PREFIX;


@ConfigurationProperties(prefix = PREFIX)
public record OAuthFlowDetails(
        String authorizationUrl,
        String tokenUrl,
        Map<String, String> scopes) {
    public OAuthFlowDetails {
        if (authorizationUrl == null || tokenUrl == null || scopes == null) {
            throw new IllegalArgumentException("authorizationUrl, tokenUrl and scopes are required");
        }
    }

    public static final String PREFIX = "springdoc.oauth-flow";
}
