package com.nowherelearn.postservice.openapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.nowherelearn.postservice.openapi.SpringDocInfo.PREFIX;

@ConfigurationProperties(prefix = PREFIX)
public record SpringDocInfo(
        String title,
        String description,
        String version
) {
    public SpringDocInfo {
        if (title == null || description == null || version == null) {
            throw new IllegalArgumentException("title, description and version are required");
        }
    }

    public static final String PREFIX = "springdoc.info";
}
