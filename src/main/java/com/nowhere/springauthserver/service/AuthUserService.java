package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.entity.AuthUser;
import java.util.List;

public interface AuthUserService {
    AuthUser createUser(String username, String password, List<String> roles);

    AuthUser getByUsername(String username);
}
