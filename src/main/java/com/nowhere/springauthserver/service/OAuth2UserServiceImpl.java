package com.nowhere.springauthserver.service;

import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Custom implementation of {@link DefaultOAuth2UserService} to persist user details from
 * OAuth2 provider.}.
 */
@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final AuthUserService authUserService;

    public OAuth2UserServiceImpl(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        // delegate to the default implementation
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        persistIfNotExist(oAuth2UserRequest, oAuth2User);
        return oAuth2User;
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
        authUserService.createUserIfNotExists(username);
    }
}
