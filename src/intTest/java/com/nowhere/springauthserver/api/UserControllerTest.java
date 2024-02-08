package com.nowhere.springauthserver.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowhere.springauthserver.api.dto.ApiResponse;
import com.nowhere.springauthserver.api.dto.CreateUserRequest;
import com.nowhere.springauthserver.api.dto.UserResponse;
import com.nowhere.springauthserver.config.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

public class UserControllerTest extends BaseIntegrationTest {

    private final String DEFAULT_USERNAME = "user@user.com";
    private final String DEFAULT_PASSWORD = "user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD, authorities = {"ROLE_USER", "SCOPE_message.read"})
    void whenUserHasRightCredentialsAccessWhoami() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/whoami")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    ApiResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNotNull(response);
                    assertNotNull(response.data());
                    assertEquals(DEFAULT_USERNAME, response.data().username());
                    assertEquals(2, response.data().roles().size());
                });
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD, authorities = {"ROLE_USER", "SCOPE_message.read"})
    void whenUserAnyRoleCreateUserAccessShouldSucceed() throws Exception {
        // new username to avoid collision
        var newUser = new CreateUserRequest("another@user.com", "some");
        var jsonReq = objectMapper.writeValueAsString(newUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .with(csrf()) // for csrf token in post request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq))
                .andExpect(result ->{
                    assertEquals(200, result.getResponse().getStatus());
                    ApiResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNotNull(response);
                    assertNotNull(response.data());
                    assertEquals(newUser.username(), response.data().username());
                });
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD, authorities = {"ROLE_USER", "SCOPE_message.read"})
    void whenUserHasNoRightToCreateAdminUserAccessShouldBeDenied() throws Exception {
        var newUser = new CreateUserRequest("some@user.com", "some");
        var jsonReq = objectMapper.writeValueAsString(newUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/admin")
                        .with(csrf()) // for csrf token in post request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq))
                .andExpect(result -> assertEquals(403, result.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD, authorities = {"ROLE_ADMIN", "SCOPE_message.write"})
    void whenUserHasRightToCreateAdminUserAccessShouldGranted() throws Exception {
        var newUser = new CreateUserRequest("some@user.com", "some");
        var jsonReq = objectMapper.writeValueAsString(newUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/admin")
                        .with(csrf()) // for csrf token in post request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    ApiResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNotNull(response);
                    assertNotNull(response.data());
                    assertEquals(newUser.username(), response.data().username());
                });
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD, authorities = {"ROLE_USER", "SCOPE_message.read"})
    void whenUserHasNoRightToGetUserAccessShouldBeDenied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .param("username", "user@user.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals(403, result.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD, authorities = {"ROLE_ADMIN", "SCOPE_message.read"})
    void whenUserHasRightToGetUserAccessShouldBeGranted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .param("username", "user@user.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    ApiResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNotNull(response);
                    assertNotNull(response.data());
                    assertEquals(DEFAULT_USERNAME, response.data().username());
                });
                }

}
