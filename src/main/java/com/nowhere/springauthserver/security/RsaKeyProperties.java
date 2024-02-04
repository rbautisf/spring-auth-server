package com.nowhere.springauthserver.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RsaKeyProperties is a record class that holds the public and private RSA keys.
 * @param publicKey
 * @param privateKey
 */
@ConfigurationProperties(prefix = SecurityConstants.RSA_KEY_PROPERTIES_PREFIX)
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
    public RsaKeyProperties {
        if (publicKey == null || privateKey == null) {
            throw new IllegalArgumentException("Public and private keys are required");
        }
    }
}
