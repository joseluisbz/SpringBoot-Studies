package org.bz.app.mspeople.configurations;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@RequiredArgsConstructor
@Configuration(value = "customHSQLDBConfiguration")
public class CustomHSQLDBConfiguration {

    private final DataSource dataSource;

    @Value("${only.for.testing:#{null}}")
    private String onlyForTesting;

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void getDbManager() {
        if (onlyForTesting == null) {
            DatabaseManagerSwing.main(new String[]{"--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", ""});
        }
    }

}