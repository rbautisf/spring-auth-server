package com.nowhere.springauthserver.api;

import com.nowhere.springauthserver.persistence.AuthUserFixture;
import com.nowhere.springauthserver.persistence.RoleFixture;
import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.service.AuthUserService;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ConstructApiResponseTest {
    private final AuthUserService mockService = Mockito.mock(AuthUserService.class);
    private final UserController controller = new UserController(mockService);

    @ParameterizedTest(name = "{0}")
    @MethodSource("authUsers")
    void testConstructApiResponse(String testName, AuthUser user) {
        var result = controller.constructResponseEntity(user);
        assertEquals(HttpStatus.OK, result.getStatusCode(), "Response status should be OK");
        assertNotNull(result.getBody(), "Response body should not be null");
        assertEquals(user.getUsername(), result.getBody().data().username(), "Username should match the predefined username");
        assertEquals(user.getRoles().size(), result.getBody().data().roles().size(), "Roles should match the predefined roles");
    }

    private static Stream<Arguments> authUsers() {
        var userRole = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.USER);
        var adminRole = RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.ADMIN);
        var userNoRoles = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of());
        var user = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(userRole));
        var admin = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(adminRole));
        var userAndAdmin = AuthUserFixture.defaultAuthUserWithRolesFixture(Set.of(userRole, adminRole));
        return Stream.of(
                Arguments.of("when user has No Roles",userNoRoles),
                Arguments.of("when user has User Role",user),
                Arguments.of("when user has Admin Role",admin),
                Arguments.of("when user has Admin and User Roles",userAndAdmin)
        );
    }
}
