package org.bz.app.mspeople.configurations;

import jakarta.annotation.PostConstruct;
import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;


@Configuration(value = "customHSQLDBConfiguration")
public class CustomHSQLDBConfiguration {

    @Value("${only.for.testing:#{null}}")
    private String onlyForTesting;

    @Bean
    public DataSource dataSource() {
        //jdbc:hsqldb:mem:testdb
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.HSQL)
                //.addScript("db/hsqldb/db.sql")
                .build();
        return db;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @PostConstruct
    public void getDbManager() {
        if (onlyForTesting == null) {
            DatabaseManagerSwing.main(new String[]{"--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", ""});
        }
    }

}