package com.nowhere.springauthserver.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(SecurityConstants.RSA_KEY_PATH_PROPERTY)
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
