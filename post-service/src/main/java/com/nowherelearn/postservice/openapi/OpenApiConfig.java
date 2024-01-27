package com.nowherelearn.postservice.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {
    private static final String SECURITY_SCHEME_NAME = "security_auth";
    private static final String SECURITY_REQUIREMENT_NAME = "security_auth";

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${springdoc.info.title}") String title,
            @Value("${springdoc.info.description}") String description,
            @Value("${springdoc.info.version}") String version,
            @Value("${springdoc.oAuthFlow.authorizationUrl}") String authorizationUrl,
            @Value("${springdoc.oAuthFlow.tokenUrl}") String tokenUrl,
            @Value("${springdoc.oAuthFlow.scopes}") Map<String, String> scopes
    ) {
        Scopes scopesModel = new Scopes();
        scopes.forEach(scopesModel::addString);

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_REQUIREMENT_NAME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme(authorizationUrl, tokenUrl, scopesModel)))
                .info(new io.swagger.v3.oas.models.info.Info().title(title).version(version).description(description));
    }

    private SecurityScheme createSecurityScheme(String authorizationUrl, String tokenUrl, Scopes scopesModel) {
        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows().authorizationCode(
                        new OAuthFlow()
                                .authorizationUrl(authorizationUrl)
                                .tokenUrl(tokenUrl)
                                .scopes(scopesModel)
                ));
    }
}
