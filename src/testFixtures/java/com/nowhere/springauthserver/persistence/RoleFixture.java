package com.nowhere.springauthserver.persistence;

import com.nowhere.springauthserver.persistence.entity.Role;
import java.util.Optional;
import java.util.UUID;


public final class RoleFixture {
    /**
     * Fixture for Role, optional fields are set to default values
     *
     * @param id default value is random UUID
     * @param type default value is USER
     * @return Role
     */
    public static Role roleFixture(UUID id, Role.RoleType type) {
        return Role
                .builder()
                .id(id)
                .type(type)
                .build();
    }
}
