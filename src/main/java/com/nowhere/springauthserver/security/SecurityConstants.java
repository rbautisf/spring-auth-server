package com.nowhere.springauthserver.security;

public final class SecurityConstants {
    public static final String ROLES_CLAIM = "roles";
    public static final String LOGIN_PATH = "/login";
    public static final String ID_TOKEN_VALUE = "id token";
    public static final String ACCESS_TOKEN_VALUE = "access token";
    public static final String RSA_KEY_PATH_PROPERTY = "rsa-key";
    public static final String BCRYPT_ENCODER_STRATEGY_NAME = "bcrypt";
    private SecurityConstants() {
        throw new AssertionError("Cannot instantiate static class");
    }
}
