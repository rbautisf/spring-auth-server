package com.nowhere.springauthserver.api;

import com.nowhere.springauthserver.api.dto.ApiResponse;
import com.nowhere.springauthserver.api.dto.CreateUserRequest;
import com.nowhere.springauthserver.api.dto.UserResponse;
import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.service.AuthUserService;
import jakarta.websocket.server.PathParam;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@EnableMethodSecurity(prePostEnabled = true)
public class UserController {
    public static final String SCOPE_READ = "hasAuthority('SCOPE_message.read')";
    public static final String WRITE_AND_ADMIN = "hasRole('ADMIN') && hasAuthority('SCOPE_message.write')";
    public static final String READ_AND_ADMIN = "hasRole('ADMIN') && hasAuthority('SCOPE_message.read')";
    private final AuthUserService authUserService;

    public UserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PreAuthorize(SCOPE_READ)
    @GetMapping("/whoami")
    public ResponseEntity<ApiResponse<UserResponse>> getDetails(Principal principal) {
        AuthUser user = authUserService.getByUsername(principal.getName());
        return constructResponseEntity(user);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest createUserRequest) {
        AuthUser user = authUserService.createUser(createUserRequest.username(), createUserRequest.password(), List.of("USER"));
        return constructResponseEntity(user);
    }

    @PreAuthorize(READ_AND_ADMIN)
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathParam(value = "username") String username) {
        AuthUser user = authUserService.getByUsername(username);
        return constructResponseEntity(user);
    }

    @PreAuthorize(WRITE_AND_ADMIN)
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<UserResponse>> createAdmin(@RequestBody CreateUserRequest createUserRequest) {
        AuthUser user = authUserService.createUser(createUserRequest.username(), createUserRequest.password(), List.of("ADMIN"));
        return constructResponseEntity(user);
    }

    public ResponseEntity<ApiResponse<UserResponse>> constructResponseEntity(AuthUser user) {
        List<String> roles = user.getRoles().stream().map(Role::getType).map(Role.RoleType::name).toList();
        UserResponse result = new UserResponse(user.getId().toString(), user.getUsername(), user.isEnabled(), roles);
        return ResponseEntity.ok().body(new ApiResponse<>(200, result));
    }

}
