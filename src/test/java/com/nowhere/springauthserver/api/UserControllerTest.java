package com.nowhere.springauthserver.api;

import com.nowhere.springauthserver.api.UserController;
import com.nowhere.springauthserver.api.dto.ApiResponse;
import com.nowhere.springauthserver.api.dto.CreateUserRequest;
import com.nowhere.springauthserver.api.dto.UserResponse;
import com.nowhere.springauthserver.persistence.AuthUserFixture;
import com.nowhere.springauthserver.service.AuthUserService;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserController
 */
//@Test//Instance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
    // stub the AuthUserService
    private final AuthUserService mockService = Mockito.mock(AuthUserService.class);
    private final Principal mockPrincipal = Mockito.mock(Principal.class);
    private final UserController controller = new UserController(mockService);

    @BeforeEach
    public void setUp() {
        Mockito.reset(mockService, mockPrincipal);
    }

    @Test
    public void testGetDetails() {
        when(mockPrincipal.getName())
                .thenReturn("test");
        when(mockService.getByUsername("test"))
                .thenReturn(AuthUserFixture.authUserNoRoles());

        final String EXPECTED_USERNAME = "test";
        ResponseEntity<ApiResponse<UserResponse>> result = controller.getDetails(mockPrincipal);
        assertEquals(HttpStatus.OK, result.getStatusCode(), "Response status should be OK");
        assertEquals(EXPECTED_USERNAME, result.getBody().data().username(), "Username should match the predefined username");
        assertEquals(0, result.getBody().data().roles().size(), "Roles should be empty");
    }

    @Test
    public void testCreateUser() {
        when(mockService.createUser("test", "test", List.of("USER")))
                .thenReturn(AuthUserFixture.createAuthUserWithRoles(Set.of("USER")));

        final String EXPECTED_USERNAME = "test";
        ResponseEntity<ApiResponse<UserResponse>> result = controller.createUser(new CreateUserRequest("test", "test"));
        assertEquals(HttpStatus.OK, result.getStatusCode(), "Response status should be OK");
        assertEquals(EXPECTED_USERNAME, result.getBody().data().username(), "Username should match the predefined username");
        assertEquals(1, result.getBody().data().roles().size(), "Roles should match the predefined roles");
    }

    @Test
    public void testGetUser() {
        when(mockService.getByUsername("test"))
                .thenReturn(AuthUserFixture.createAuthUserWithRoles(Set.of("USER", "ADMIN")));
        final String EXPECTED_USERNAME = "test";
        ResponseEntity<ApiResponse<UserResponse>> result = controller.getUser("test");
        assertEquals(HttpStatus.OK, result.getStatusCode(), "Response status should be OK");
        assertEquals(EXPECTED_USERNAME, result.getBody().data().username(), "Username should match the predefined username");
        assertEquals(2, result.getBody().data().roles().size(), "Roles should match the predefined roles");
    }

    @Test
    public void testCreateAdmin() {
        when(mockService.createUser("test", "test", List.of("ADMIN")))
                .thenReturn(AuthUserFixture.createAuthUserWithRoles(Set.of("ADMIN")));

        final String EXPECTED_USERNAME = "test";
        ResponseEntity<ApiResponse<UserResponse>> result = controller.createAdmin(new CreateUserRequest("test", "test"));
        assertEquals(HttpStatus.OK, result.getStatusCode(), "Response status should be OK");
        assertEquals(EXPECTED_USERNAME, result.getBody().data().username(), "Username should match the predefined username");
        assertEquals(1, result.getBody().data().roles().size(), "Roles should match the predefined roles");
    }
}
