package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.AuthUserFixture;
import com.nowhere.springauthserver.persistence.RoleFixture;
import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.persistence.repository.AuthUserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.nowhere.springauthserver.security.SecurityConstants.DEFAULT_AUTHORITY_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class UserServiceDetailsTest {
    private final AuthUserRepository authUserRepository = Mockito.mock(AuthUserRepository.class);
    private final RoleService roleService = Mockito.mock(RoleService.class);
    private final PasswordEncoder passwordEncoder= Mockito.mock(PasswordEncoder.class);
    private final UserDetailsService userDetailsService = new AuthUserServiceImpl(roleService, authUserRepository, passwordEncoder);

    @BeforeEach
    void setUp() {
        Mockito.reset(authUserRepository, roleService, passwordEncoder);
    }

    @Test
    void testLoadUserByUsername() {
        var roleE = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.ADMIN);
        var authUserE = AuthUserFixture.authUserFixture(
                UUID.randomUUID(), "test", "passEncoded", Set.of(roleE), true, true, true, true
        );

        when(authUserRepository.findByUsername(authUserE.getUsername())).thenReturn(Optional.of(authUserE));

        assertNotNull(authUserE, "AuthUser should not be null");
        assertNotNull(roleE, "Role should not be null");

        when(authUserRepository.findByUsername(authUserE.getUsername())).thenReturn(Optional.of(authUserE));

        var result = userDetailsService.loadUserByUsername(authUserE.getUsername());

        assertEquals(authUserE.getUsername(), result.getUsername(), "Username should match the predefined username");
        assertEquals(authUserE.getPassword(), result.getPassword(), "Password should match the predefined password");
        assertEquals(authUserE.isEnabled(), result.isEnabled(), "Enabled should match the predefined enabled");
        assertEquals(authUserE.isAccountNonExpired(), result.isAccountNonExpired(), "AccountNonExpired should match the predefined accountNonExpired");
        assertEquals(authUserE.isAccountNonLocked(), result.isAccountNonLocked(), "AccountNonLocked should match the predefined accountNonLocked");
        assertEquals(authUserE.isCredentialsNonExpired(), result.isCredentialsNonExpired(), "CredentialsNonExpired should match the predefined credentialsNonExpired");
        authUserE.getRoles().stream().map(role -> new SimpleGrantedAuthority(DEFAULT_AUTHORITY_PREFIX+role.getType().name())).forEach(
                authority -> assertTrue(result.getAuthorities().contains(authority), "Authorities should match the predefined authorities")
        );
    }
}
