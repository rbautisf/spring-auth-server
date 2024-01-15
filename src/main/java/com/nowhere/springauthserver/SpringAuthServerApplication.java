package com.nowhere.springauthserver;

import com.nowhere.springauthserver.security.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class SpringAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAuthServerApplication.class, args);
    }

}
