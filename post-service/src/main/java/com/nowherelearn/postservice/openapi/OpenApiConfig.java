package com.nowherelearn.postservice.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class OpenApiConfig {
    private static final String SECURITY_SCHEME_NAME = "security_auth";
    private static final String SECURITY_REQUIREMENT_NAME = "security_auth";

    @Bean
    public OpenAPI customOpenAPI(
            SpringDocInfo springDocInfo,
            OAuthFlowDetails oAuthFlowDetails
    ) {
        Scopes scopesModel = new Scopes();
        oAuthFlowDetails.scopes().forEach(scopesModel::addString);

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_REQUIREMENT_NAME))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                createSecurityScheme(
                                        oAuthFlowDetails.authorizationUrl(),
                                        oAuthFlowDetails.tokenUrl(),
                                        scopesModel
                                )
                        )
                )
                .info(new Info()
                        .title(springDocInfo.title())
                        .version(springDocInfo.version())
                        .description(springDocInfo.description())
                );
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
