package com.github.fabriciolfj.study.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    // Flyway no banco WRITE
    @Bean
    public Flyway flywayWrite(@Qualifier("writeDataSource") DataSource datasource) {
        var flyway = Flyway.configure()
                .dataSource(datasource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        return flyway;
    }

    // Flyway no banco READ
    @Bean
    public Flyway flywayRead(@Qualifier("readDataSource") DataSource read) {
        var flyway = Flyway.configure()
                .dataSource(read)
                .locations("classpath:db/migration") // mesma pasta de scripts
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        return flyway;
    }
}