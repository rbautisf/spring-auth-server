package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.AuthUserFixture;
import com.nowhere.springauthserver.persistence.RoleFixture;
import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.persistence.repository.AuthUserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthUserServiceTest {
    private final AuthUserRepository authUserRepository = Mockito.mock(AuthUserRepository.class);
    private final RoleService roleService = Mockito.mock(RoleService.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final AuthUserServiceImpl authUserService = new AuthUserServiceImpl(roleService, authUserRepository, passwordEncoder);

    @BeforeEach
    void setUp() {
        Mockito.reset(authUserRepository, roleService, passwordEncoder);
    }

    @Test
    void testCreateUser() {
        var roleE = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.USER);
        var authUserE = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(roleE));
        assertNotNull(authUserE, "AuthUser should not be null");

        assertNotNull(roleE, "Role should not be null");

        when(authUserRepository.findByUsername(authUserE.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(authUserE.getPassword())).thenReturn("testEncoded");
        when(roleService.getByType(roleE.getType().name())).thenReturn(roleE);

        when(authUserRepository.save(any())).then(invocation -> invocation.getArgument(0));

        var result = authUserService.createUser(authUserE.getUsername(), authUserE.getPassword(), List.of("USER"));

        assertEquals(authUserE.getUsername(), result.getUsername(), "Username should match the predefined username");
        assertEquals("testEncoded", result.getPassword(), "Password should match the predefined password");
        assertEquals(1, result.getRoles().size(), "Roles should match the predefined roles");
        var roleResult = result.getRoles().stream().findFirst();
        assertTrue(roleResult.isPresent(), "Role should not be null");
        assertNotNull(result.getRoles().stream().findFirst(), "Role id should not be null");
        assertEquals(roleE.getType(), roleResult.get().getType(), "Role should match the predefined role");
    }

    @Test
    void testGetByUsername() {
        var roleE = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.USER);
        var authUserE = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(roleE));
        assertNotNull(authUserE, "AuthUser should not be null");
        assertNotNull(roleE, "Role should not be null");

        when(authUserRepository.findByUsername(authUserE.getUsername())).thenReturn(Optional.of(authUserE));

        var result = authUserService.getByUsername(authUserE.getUsername());

        assertEquals(authUserE.getUsername(), result.getUsername(), "Username should match the predefined username");
        assertEquals(authUserE.getPassword(), result.getPassword(), "Password should match the predefined password");
        assertEquals(1, result.getRoles().size(), "Roles should match the predefined roles");
        var roleResult = result.getRoles().stream().findFirst();
        assertTrue(roleResult.isPresent(), "Role should not be null");
        assertEquals(roleE.getType(), roleResult.get().getType(), "Role should match the predefined role");
    }
}
