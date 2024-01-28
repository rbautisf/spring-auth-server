package com.nowherelearn.postservice.security;

public final class SecurityConstants {
    public static String[] AUTH_WHITELIST = {
            "/actuator",
            "/swagger-ui/**",
            "/api-docs/**"
    };
    public static final String ROLES_CLAIM = "roles";

    public static final String DEFAULT_AUTHORITY_PREFIX = "ROLE_";

    private SecurityConstants() {
    }
}
