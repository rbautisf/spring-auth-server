package com.nowhere.springauthserver;

import com.nowhere.springauthserver.config.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class SpringAuthServerApplicationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() throws Exception {
        // use the mockMvc to test the application context
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")).andExpect(status().isOk())
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertEquals("{\"status\":\"UP\"}", content, "Actuator health endpoint should return UP");
                });
    }

}
