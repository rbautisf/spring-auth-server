package com.nowhere.springauthserver.api;

import com.nowhere.springauthserver.api.dto.ApiResponse;
import com.nowhere.springauthserver.api.dto.UserResponse;
import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.service.AuthUserService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@EnableMethodSecurity(prePostEnabled = true)
public class UserController {
    public static final String READ = "hasAuthority('SCOPE_message.read')";
    public static final String WRITE_AND_ADMIN = "hasRole('ADMIN') && hasAuthority('SCOPE_message.write')";
    private final AuthUserService authUserService;

    public UserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PreAuthorize(READ)
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathParam(value = "username") String username) {
        AuthUser user = authUserService.getByUsername(username);
        UserResponse result = new UserResponse(user.getUsername(), user.getRoles().stream().map(role -> role.getRole().name()).toList());
        return ResponseEntity.ok().body(new ApiResponse<>(200, result));
    }


    @PreAuthorize(WRITE_AND_ADMIN)
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody Model userM) {

        Optional<Object> username = Optional.ofNullable(userM.getAttribute("username"));
        Optional<Object> password = Optional.ofNullable(userM.getAttribute("password"));
        if (username.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "username and password are required"));
        }
        AuthUser user = authUserService.createUser(username.get().toString(), password.get().toString());
        UserResponse result = new UserResponse(user.getUsername(), user.getRoles().stream().map(role -> role.getRole().name()).toList());
        return ResponseEntity.ok().body(new ApiResponse<>(200, result));
    }
}