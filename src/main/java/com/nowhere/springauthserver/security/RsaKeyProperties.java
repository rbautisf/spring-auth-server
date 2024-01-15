package com.nowhere.springauthserver.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = RsaKeyProperties.RSA_KEY_PROPERTIES_PREFIX)
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
    public static final String RSA_KEY_PROPERTIES_PREFIX = "rsa-key";
}
