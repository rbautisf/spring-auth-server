package com.nowhere.springauthserver.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(
        initializers = {ContextConfig.class}
)
// Spring Boot is used to automatically configure and inject a MockMvc instance into your test class.
@AutoConfigureMockMvc
//When you specify replace = AutoConfigureTestDatabase.Replace.ANY, it means that Spring Boot will replace any existing DataSource bean.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public abstract class BaseIntegrationTest {
}