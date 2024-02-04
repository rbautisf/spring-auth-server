package com.nowhere.springauthserver.api;

import com.nowhere.springauthserver.persistence.AuthUserFixture;
import com.nowhere.springauthserver.persistence.RoleFixture;
import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.service.AuthUserService;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


public class UserControllerTest {
    // stub the AuthUserService
    private final AuthUserService mockService = Mockito.mock(AuthUserService.class);
    private final Principal mockPrincipal = Mockito.mock(Principal.class);
    private final UserController controller = new UserController(mockService);

    @BeforeEach
    public void setUp() {
        Mockito.reset(mockService, mockPrincipal);
    }

    // write a test that receives parameters and returns a response entity

    @Test
    public void testConstructEntity() {
        List<AuthUser> authUsers = authUsers();
        authUsers.forEach(user -> {
            when(mockService.getByUsername(user.getUsername())).thenReturn(user);

            var result = controller.constructResponseEntity(user);
            assertEquals(HttpStatus.OK, result.getStatusCode(), "Response status should be OK");
            assertNotNull(result.getBody(), "Response body should not be null");
            assertEquals(user.getUsername(), result.getBody().data().username(), "Username should match the predefined username");
            assertEquals(user.getRoles().size(), result.getBody().data().roles().size(), "Roles should match the predefined roles");
        });
    }

    private static List<AuthUser> authUsers() {
        var userRole = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.USER);
        var adminRole = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.ADMIN);
        var userNoRoles = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of());
        var user = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(userRole));
        var admin = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(adminRole));
        var userAndAdmin = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(userRole, adminRole));
        return List.of(
                userNoRoles,
                user,
                admin,
                userAndAdmin
        );
    }
}
