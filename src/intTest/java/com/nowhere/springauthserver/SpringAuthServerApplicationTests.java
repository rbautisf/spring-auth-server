package com.nowhere.springauthserver;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Integration tests for the SpringAuthServerApplication
 */
@SpringBootTest
@EnableConfigurationProperties
class SpringAuthServerApplicationTests {

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        registry.add("rsa-key.publicKey", () -> publicKey);
        registry.add("rsa-key.privateKey", () -> privateKey);
    }


    @Test
    void contextLoads() {
    }

}
