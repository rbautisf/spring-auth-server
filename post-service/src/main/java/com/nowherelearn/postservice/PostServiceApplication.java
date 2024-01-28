package com.nowherelearn.postservice;

import com.nowherelearn.postservice.openapi.OAuthFlowDetails;
import com.nowherelearn.postservice.openapi.SpringDocInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({OAuthFlowDetails.class, SpringDocInfo.class})
public class PostServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }

}
