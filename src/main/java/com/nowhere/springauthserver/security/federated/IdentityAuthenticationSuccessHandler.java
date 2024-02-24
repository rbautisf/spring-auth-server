package com.nowhere.springauthserver.security.federated;

import com.nowhere.springauthserver.service.AuthUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class IdentityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthenticationSuccessHandler delegate;
    private final AuthUserService authUserService;

    public IdentityAuthenticationSuccessHandler(AuthenticationSuccessHandler delegate, AuthUserService authUserService) {
        this.delegate = delegate;
        this.authUserService = authUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        switch (authentication) {
            case OAuth2AuthenticationToken oauth ->
                    authUserService.createUserIfNotExists(oauth.getPrincipal().getName());
            case OidcUser oidc -> authUserService.createUserIfNotExists(oidc.getName());
            default -> {
            }
        }

        this.delegate.onAuthenticationSuccess(request, response, authentication);
    }

}
