package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.persistence.repository.RoleRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RoleServiceTest {
    private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    private final RoleService roleService = new RoleServiceImpl(roleRepository);

    @BeforeEach
    void setUp() {
        Mockito.reset(roleRepository);
    }

    @Test
    void testGetByType() {
        roles().forEach(role -> {
            Mockito.when(roleRepository.findByType(role.getType())).thenReturn(java.util.Optional.of(role));
            var result = roleService.getByType(role.getType().name());
            org.junit.jupiter.api.Assertions.assertEquals(role, result, "Role should match the predefined role");
        });
    }

    @Test
    void testGetAllRoles() {
        var roles = roles();
        Mockito.when(roleRepository.findAll()).thenReturn(roles);
        var result = roleService.getAllRoles();
        org.junit.jupiter.api.Assertions.assertEquals(roles, result, "Roles should match the predefined roles");
    }
    private List<Role> roles() {
        return List.of(
                new Role.Builder()
                        .id(java.util.UUID.randomUUID())
                        .type(Role.RoleType.USER)
                        .build(),
                new Role.Builder()
                        .id(java.util.UUID.randomUUID())
                        .type(Role.RoleType.ADMIN)
                        .build()
        );
    }
}
