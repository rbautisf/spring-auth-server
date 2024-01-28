package com.nowhere.springauthserver.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = SecurityConstants.RSA_KEY_PROPERTIES_PREFIX)
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
    public RsaKeyProperties {
        if (publicKey == null || privateKey == null) {
            throw new IllegalArgumentException("Public and private keys are required");
        }
    }
}
