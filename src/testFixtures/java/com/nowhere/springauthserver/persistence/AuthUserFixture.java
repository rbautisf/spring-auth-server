package com.nowhere.springauthserver.persistence;

import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.persistence.entity.Role;
import java.util.Set;
import java.util.UUID;


public final class AuthUserFixture {
    /**
     * Fixture for AuthUser, optional fields are set to default values
     *
     * @param id default value is random UUID
     * @param username default value is "test"
     * @param password default value is "test"
     * @param roles default value is empty set
     * @param enabled required field
     * @param accountNonExpired required field
     * @param accountNonLocked required field
     * @param credentialsNonExpired required field
     * @return AuthUser
     */
    public static AuthUser authUserFixture(
            UUID id,
            String username,
            String password,
            Set<Role> roles,
            boolean enabled,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired) {
        return AuthUser
                .builder()
                .id(id)
                .username(username)
                .password(password)
                .enabled(enabled)
                .accountNonExpired(accountNonExpired)
                .accountNonLocked(accountNonLocked)
                .credentialsNonExpired(credentialsNonExpired)
                .roles(roles)
                .build();
    }

    public static AuthUser defaultAuthUserWithRolesFixture(Set<Role> roles) {
        return AuthUser
                .builder()
                .id(UUID.randomUUID())
                .username("test")
                .password("pass")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(roles)
                .build();
    }
}
