package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.entity.AuthUser;

public interface AuthUserService {
    AuthUser createUser(String username, String password);
    AuthUser getByUsername(String username);
}
