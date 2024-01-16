package com.nowhere.springauthserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class SpringAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAuthServerApplication.class, args);
    }

}
