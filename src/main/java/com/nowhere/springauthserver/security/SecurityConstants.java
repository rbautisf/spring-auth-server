package com.nowhere.springauthserver.security;

public final class SecurityConstants {
    public static final String REGISTER_USER_PATH = "/register";
    public static final String SIGNUP_PATH = "/signup";
    public static final String LOGIN_PATH = "/login";
    public static final String ACTUATOR_PATH = "/actuator/**";
    public static final String ASSETS_PATH = "/assets/**";
    public static final String ANY_PATH = "/**";
    public static final String CONSENT_PAGE_URI_CUSTOM = "/oauth2/consent";

    public static final String ROLES_CLAIM = "roles";
    public static final String DEFAULT_AUTHORITY_PREFIX = "ROLE_";
    public static final String ID_TOKEN_VALUE = "id token";
    public static final String ACCESS_TOKEN_VALUE = "access token";

    public static final String RSA_KEY_PROPERTIES_PREFIX = "rsa-key";
    public static final String BCRYPT_ENCODER_STRATEGY_NAME = "bcrypt";
    public static final int BCRYPT_STRENGTH = 10;

    private SecurityConstants() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
