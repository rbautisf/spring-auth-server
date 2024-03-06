package com.nowhere.springauthserver.security.federated;

import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.security.SecurityConstants;
import com.nowhere.springauthserver.service.AuthUserService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import static com.nowhere.springauthserver.security.SecurityConstants.ACCESS_TOKEN_VALUE;
import static com.nowhere.springauthserver.security.SecurityConstants.ROLES_CLAIM;

@Component
public class OAuthTokenCustomizerCustom implements OAuth2TokenCustomizer<JwtEncodingContext> {
    private static final Set<String> ID_TOKEN_CLAIMS = Set.of(IdTokenClaimNames.ISS, IdTokenClaimNames.SUB, IdTokenClaimNames.AUD, IdTokenClaimNames.EXP, IdTokenClaimNames.IAT, IdTokenClaimNames.AUTH_TIME, IdTokenClaimNames.NONCE, IdTokenClaimNames.ACR, IdTokenClaimNames.AMR, IdTokenClaimNames.AZP, IdTokenClaimNames.AT_HASH, IdTokenClaimNames.C_HASH);
    private final AuthUserService authUserService;

    public OAuthTokenCustomizerCustom(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public void customize(JwtEncodingContext context) {
        switch (context.getTokenType().getValue()) {
            case OidcParameterNames.ID_TOKEN  -> processIdToken(context);
            case OAuth2ParameterNames.ACCESS_TOKEN -> processAccessToken(context);
            default -> {}
        }
    }

    private void processIdToken(JwtEncodingContext context){
        context.getClaims().claim(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, SecurityConstants.ID_TOKEN_VALUE);

        Map<String, Object> thirdPartyClaims = extractClaims(context.getPrincipal());
        context.getClaims().claims(existingClaims -> {
            // Remove conflicting claims set by this authorization server
            existingClaims.keySet().forEach(thirdPartyClaims::remove);
            // Remove standard id_token claims that could cause problems with clients
            ID_TOKEN_CLAIMS.forEach(thirdPartyClaims::remove);
            // Add all other claims directly to id_token
            existingClaims.putAll(thirdPartyClaims);
        });
    }

    private void processAccessToken(JwtEncodingContext context){
        Authentication principal = context.getPrincipal();
        switch (principal){
            case OAuth2ClientAuthenticationToken clientPrincipal -> processClientAccessToken(context, clientPrincipal);
            case UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken -> processUserAccessToken(context, usernamePasswordAuthenticationToken);
            case OAuth2AuthenticationToken authentication -> processUserAccessToken(context, authentication);
            default -> {}
        }
    }

    private void processClientAccessToken(JwtEncodingContext context, OAuth2ClientAuthenticationToken clientPrincipal){
        context.getClaims().claim(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, SecurityConstants.CLIENT_CREDENTIALS_VALUE);
        context.getClaims().claim(OAuth2TokenIntrospectionClaimNames.CLIENT_ID, clientPrincipal.getName());
    }

    private void processUserAccessToken(JwtEncodingContext context, Authentication principal){
        AuthUser authUser = authUserService.getByUsername(principal.getName());
        context.getClaims().claims((claims) -> {
            claims.put(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, ACCESS_TOKEN_VALUE);
            Set<String> roles = authUser.getRoles().stream()
                    .map(role -> SecurityConstants.DEFAULT_AUTHORITY_PREFIX + role.getType().name())
                    .collect(Collectors.toSet());
            claims.put(ROLES_CLAIM, roles);
        });
    }

    private Map<String, Object> extractClaims(Authentication principal) {
        Map<String, Object> claims = switch (principal.getPrincipal()) {
            case OidcUser oidcUser -> oidcUser.getIdToken().getClaims();
            case OAuth2User oauth2User -> oauth2User.getAttributes();
            default -> Collections.emptyMap();
        };
        return new HashMap<>(claims);
    }


}