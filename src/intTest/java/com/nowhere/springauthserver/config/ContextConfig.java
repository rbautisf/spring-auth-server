package com.nowhere.springauthserver.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ContextConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final int RSA_KEY_SIZE = 2048;
    private static final String RSA_PUBLIC_KEY_PROPERTY_NAME = "rsa-key.publicKey";
    private static final String RSA_PRIVATE_KEY_PROPERTY_NAME = "rsa-key.privateKey";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            KeyPair keyPair = generateKeyPair();
            initializeApplicationProperties(applicationContext, keyPair);
        } catch (Exception e) {
            throw new RuntimeException("Error while initializing the RSA key pair");
        }
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    private void initializeApplicationProperties(ConfigurableApplicationContext applicationContext, KeyPair keyPair) {
        var systemProps = applicationContext.getEnvironment().getSystemProperties();
        systemProps.put(RSA_PUBLIC_KEY_PROPERTY_NAME, keyPair.getPublic());
        systemProps.put(RSA_PRIVATE_KEY_PROPERTY_NAME, keyPair.getPrivate());
    }
}