package com.nowhere.springauthserver.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowhere.springauthserver.api.dto.ApiResponse;
import com.nowhere.springauthserver.api.dto.UserResponse;
import com.nowhere.springauthserver.config.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseIntegrationTest {

    private final String DEFAULT_USERNAME = "user@user.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@user.com", password = "user",  authorities = {"ROLE_USER","ROLE_ADMIN", "SCOPE_message.read"})
    void testGetUser() throws Exception {
        String jsonResponse = mockMvc.perform(MockMvcRequestBuilders.get("/user/whoami")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ApiResponse<UserResponse> response = objectMapper.readValue(jsonResponse, new TypeReference<ApiResponse<UserResponse>>() {});
        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(DEFAULT_USERNAME, response.data().username());
        assertEquals(2, response.data().roles().size());
    }
}
