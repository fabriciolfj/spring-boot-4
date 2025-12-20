package com.github.fabriciolfj.study.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceH2Config {

    /**
     * DataSource para METADADOS do Spring Batch.
     *
     * Usa H2 em memória - criado automaticamente pelo Spring Batch.
     * Não precisa criar as tabelas manualmente.
     */
    @Bean(name = "batchDataSource")
    public DataSource batchDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                    .setName("batch_metadata")  // Nome do banco H2
                .addScript("org/springframework/batch/core/schema-h2.sql")
                .build();
    }

    @Bean(name = "batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager(@Qualifier("batchDataSource") DataSource batchDataSource) {
        return new DataSourceTransactionManager(batchDataSource);
    }
}
