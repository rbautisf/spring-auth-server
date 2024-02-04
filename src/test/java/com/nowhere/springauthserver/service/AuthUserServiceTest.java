package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.AuthUserFixture;
import com.nowhere.springauthserver.persistence.RoleFixture;
import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.persistence.repository.AuthUserRepository;
import com.nowhere.springauthserver.service.AuthUserService;
import com.nowhere.springauthserver.service.AuthUserServiceImpl;
import com.nowhere.springauthserver.service.RoleService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthUserServiceTest {
    private final AuthUserRepository authUserRepository = Mockito.mock(AuthUserRepository.class);
    private final RoleService roleService = Mockito.mock(RoleService.class);
    private final PasswordEncoder passwordEncoder= Mockito.mock(PasswordEncoder.class);
    private final AuthUserService authUserService = new AuthUserServiceImpl(roleService, authUserRepository, passwordEncoder);


    @BeforeEach
    public void setUp() {
        Mockito.reset(authUserRepository, roleService, passwordEncoder);
    }

    @Test
    public void testCreateUser() {
        var roleE = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.USER);
        var authUserE = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(roleE));
        assertNotNull(authUserE, "AuthUser should not be null");

        assertNotNull(roleE, "Role should not be null");

        when(authUserRepository.findByUsername(authUserE.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(authUserE.getPassword())).thenReturn("testEncoded");
        when(roleService.getByType(roleE.getType().name())).thenReturn(roleE);
        // return the argument, means the user entity was created and saved
        when(authUserRepository.save(any())).then(invocation -> invocation.getArgument(0));

        var result = authUserService.createUser(authUserE.getUsername(), authUserE.getPassword(), List.of("USER"));

        assertEquals(authUserE.getUsername(), result.getUsername(), "Username should match the predefined username");
        assertEquals("testEncoded", result.getPassword() , "Password should match the predefined password");
        assertEquals(1, result.getRoles().size(), "Roles should match the predefined roles");
        var roleResult = result.getRoles().stream().findFirst();
        assertTrue(roleResult.isPresent(), "Role should not be null");
        assertNotNull(result.getRoles().stream().findFirst(), "Role id should not be null");
        assertEquals(roleE.getType(), roleResult.get().getType(),  "Role should match the predefined role");
    }

    @Test
    public void testGetByUsername() {
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

    @Test
    public void testLoadUserByUsername() {
        var roleE = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.ADMIN);
        var authUserE = AuthUserFixture.authUserFixture(
                UUID.randomUUID(), "test", "passEncoded", Set.of(roleE), true, true, true, true
        );

        UserDetails userDetails = new User(
                authUserE.getUsername(),
                authUserE.getPassword(),
                authUserE.isEnabled(),
                authUserE.isAccountNonExpired(),
                authUserE.isCredentialsNonExpired(),
                authUserE.isAccountNonLocked(),
                authUserE.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getType().name())).collect(Collectors.toList())
        );

        when(authUserRepository.findByUsername(authUserE.getUsername())).thenReturn(Optional.of(authUserE));

        assertNotNull(authUserE, "AuthUser should not be null");
        assertNotNull(roleE, "Role should not be null");

        when(authUserRepository.findByUsername(authUserE.getUsername())).thenReturn(Optional.of(authUserE));

        var result = ((UserDetailsService)authUserService).loadUserByUsername(authUserE.getUsername());

        assertEquals(userDetails.getUsername(), result.getUsername(), "Username should match the predefined username");
        assertEquals(userDetails.getPassword(), result.getPassword(), "Password should match the predefined password");
        assertEquals(userDetails.isEnabled(), result.isEnabled(), "Enabled should match the predefined enabled");
        assertEquals(userDetails.isAccountNonExpired(), result.isAccountNonExpired(), "AccountNonExpired should match the predefined accountNonExpired");
        assertEquals(userDetails.isAccountNonLocked(), result.isAccountNonLocked(), "AccountNonLocked should match the predefined accountNonLocked");
        assertEquals(userDetails.isCredentialsNonExpired(), result.isCredentialsNonExpired(), "CredentialsNonExpired should match the predefined credentialsNonExpired");
        assertEquals(userDetails.getAuthorities(), result.getAuthorities(), "Authorities should match the predefined authorities");
    }
}
