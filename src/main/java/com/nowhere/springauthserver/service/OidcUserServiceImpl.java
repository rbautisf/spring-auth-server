package com.nowhere.springauthserver.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OidcUserServiceImpl extends OidcUserService {
    private final AuthUserService authUserService;
    private final Logger log = LoggerFactory.getLogger(OAuth2UserServiceImpl.class);

    public OidcUserServiceImpl(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        persistIfNotExist(userRequest, oidcUser);
        return oidcUser;
    }

    private void persistIfNotExist(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        // defined in the OAuth2 provider's configuration user-name-attribute
        String usernameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> userAttributes = oAuth2User.getAttributes();
        if (!StringUtils.hasText(usernameAttributeName)) {
            // default to 'sub' if not defined
            usernameAttributeName = "sub";
        }
        String username = userAttributes.get(usernameAttributeName).toString();
        if(StringUtils.isEmpty(username)) {
            log.error("Username is empty on OAuth2User. Cannot persist user details.");
        }
        authUserService.createUserIfNotExists(username);
    }
}
