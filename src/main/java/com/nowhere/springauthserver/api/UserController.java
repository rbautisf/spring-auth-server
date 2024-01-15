package com.nowhere.springauthserver.api;

import com.nowhere.springauthserver.api.dto.ApiResponse;
import com.nowhere.springauthserver.api.dto.CreateUserRequest;
import com.nowhere.springauthserver.api.dto.UserResponse;
import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.service.AuthUserService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@EnableMethodSecurity(prePostEnabled = true)
public class UserController {
    public static final String READ = "hasAuthority('SCOPE_message.read')";
    public static final String WRITE = "hasAuthority('SCOPE_message.write')";
    private final AuthUserService authUserService;
    public UserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PreAuthorize(READ)
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathParam(value = "username") String username)  {
        AuthUser user = authUserService.getByUsername(username);
        UserResponse result = new UserResponse(user.getId().toString(), user.getUsername(), user.isEnabled());
        return ResponseEntity.ok().body(new ApiResponse<>(200, result));
    }

    @PreAuthorize(WRITE)
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest createUserRequest){
        AuthUser user = authUserService.createUser(createUserRequest.username(), createUserRequest.password());
        UserResponse result = new UserResponse(user.getId().toString(), user.getUsername(), user.isEnabled());
        return ResponseEntity.ok().body(new ApiResponse<>(200, result));
    }
}
