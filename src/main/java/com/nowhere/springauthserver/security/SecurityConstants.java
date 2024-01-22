package com.nowhere.springauthserver.security;

public final class SecurityConstants {
    public static final String ROLES_CLAIM = "roles";

    public static final String DEFAULT_AUTHORITY_PREFIX = "ROLE_";

    public static final String LOGIN_PATH = "/login";

    public static final String ID_TOKEN_VALUE = "id token";

    public static final String ACCESS_TOKEN_VALUE = "access token";

    public static final String RSA_KEY_PROPERTY = "rsa-key";

    public static final String BCRYPT_PASSWORD_ENCODER = "bcrypt";

    private SecurityConstants() {
    }
}
