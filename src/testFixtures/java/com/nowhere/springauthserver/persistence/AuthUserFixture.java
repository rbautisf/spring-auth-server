package com.nowhere.springauthserver.persistence;

import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.persistence.entity.Role;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AuthUserFixture {
    public static AuthUser authUserNoRoles() {
        return new AuthUser.Builder()
                .id(UUID.randomUUID())
                .username("test")
                .password("test")
                .roles(Set.of())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(Set.of())
                .build();
    }

    public static AuthUser createAuthUserWithRoles(Set<String> roles) {
        Set<Role> roleSet = roles.stream().map(role -> {
            Role r = new Role();
            r.setId(UUID.randomUUID());
            r.setType(Role.RoleType.valueOf(role));
            return r;
        }).collect(Collectors.toSet());
        return new AuthUser.Builder()
                .id(UUID.randomUUID())
                .username("test")
                .password("test")
                .roles(Set.of())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(roleSet)
                .build();
    }

}
