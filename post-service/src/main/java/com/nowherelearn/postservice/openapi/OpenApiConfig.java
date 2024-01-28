package com.nowherelearn.postservice.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String SECURITY_NAME = "NowhereOAuth2";

    @Bean
    public OpenAPI customOpenAPI(
            SpringDocInfo springDocInfo,
            OAuthFlowDetails oAuthFlowDetails
    ) {

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_NAME))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_NAME,
                                createSecurityScheme(oAuthFlowDetails)
                        )
                )
                .info(new Info()
                        .title(springDocInfo.title())
                        .version(springDocInfo.version())
                        .description(springDocInfo.description())
                );
    }

    private SecurityScheme createSecurityScheme(OAuthFlowDetails oAuthFlowDetails) {
        Scopes scopesModel = new Scopes();
        oAuthFlowDetails.scopes().forEach(scopesModel::addString);
        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows().authorizationCode(
                        new OAuthFlow()
                                .authorizationUrl(oAuthFlowDetails.authorizationUrl())
                                .tokenUrl(oAuthFlowDetails.tokenUrl())
                                .scopes(scopesModel)
                ));
    }
}
