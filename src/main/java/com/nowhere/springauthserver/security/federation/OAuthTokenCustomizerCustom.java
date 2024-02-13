package com.nowhere.springauthserver.security.federation;

import com.nowhere.springauthserver.security.SecurityConstants;
import com.nowhere.springauthserver.service.AuthUserService;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import static com.nowhere.springauthserver.security.SecurityConstants.ACCESS_TOKEN_VALUE;
import static com.nowhere.springauthserver.security.SecurityConstants.ROLES_CLAIM;

@Component
public class OAuthTokenCustomizerCustom implements OAuth2TokenCustomizer<JwtEncodingContext> {
    private final AuthUserService authUserService;
    public OAuthTokenCustomizerCustom(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public void customize(JwtEncodingContext context) {
         if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
            Authentication principal = context.getPrincipal();
            var localUser = authUserService.getByUsername(principal.getName());
            context.getClaims().claims((claims) -> {
                claims.put(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, ACCESS_TOKEN_VALUE);
                Set<String> roles = localUser.getRoles().stream()
                        .map(role-> SecurityConstants.DEFAULT_AUTHORITY_PREFIX + role.getType().name())
                        .collect(Collectors.toSet());
                claims.put(ROLES_CLAIM, roles);
            });
        }
    }

}