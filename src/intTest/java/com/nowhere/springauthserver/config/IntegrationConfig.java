package com.nowhere.springauthserver.config;

import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@TestConfiguration
public class IntegrationConfig {

    /**
     * Create an embedded H2 database for testing.
     * Reasons for using the EmbeddedDatabase are:
     * - It is lightweight and fast.
     * - It is easy to set up and tear down.
     * - It is a good fit for integration tests.
     * - It is a good fit for testing SQL scripts.
     *
     * @return the {@link DataSource}
     */
    @Bean
    @Primary
    public EmbeddedDatabase embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .continueOnError(false)
                .addDefaultScripts()
                .build();
    }

}
