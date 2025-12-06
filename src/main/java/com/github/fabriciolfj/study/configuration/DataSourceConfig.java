package com.github.fabriciolfj.study.configuration;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JdbcJobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Configuração de DataSources separados:
 *
 * 1. batchDataSource (H2) → APENAS para metadados do Spring Batch
 *    - Tabelas: BATCH_JOB_INSTANCE, BATCH_JOB_EXECUTION, etc
 *    - Em memória (não polui seu banco principal)
 *    - Ideal para não misturar dados do batch com dados de negócio
 *
 * 2. businessDataSource (PostgreSQL/MySQL/etc) → Para dados de negócio
 *    - Tabela: transacoes (seus dados)
 *    - Seu banco principal
 *    - Configurado no application.yml
 */
@Configuration
public class DataSourceConfig {

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
                .build();
    }

    /**
     * DataSource PRINCIPAL para dados de negócio.
     *
     * Configurado via application.yml com suas credenciais.
     * Este é o DataSource que suas entidades JPA vão usar.
     */
    @Primary  // ← Este é o DataSource padrão
    @Bean(name = "businessDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties businessDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource businessDataSource(
            @Qualifier("businessDataSource") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * TransactionManager para metadados do Spring Batch.
     * Usa o batchDataSource (H2).
     */
    @Bean(name = "batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager(
            @Qualifier("batchDataSource") DataSource batchDataSource) {
        return new DataSourceTransactionManager(batchDataSource);
    }

    /**
     * TransactionManager para dados de negócio.
     * Usa o dataSource principal (PostgreSQL/MySQL).
     *
     * Este é usado pelos Steps para processar seus dados.
     */
    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager businessTransactionManager(
            @Qualifier("businessDataSource") EntityManagerFactory entityManagerFactory) {
        var jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Bean
    public JobRepository jobRepository(
            @Qualifier("batchDataSource") DataSource batchDataSource,
            @Qualifier("batchTransactionManager") PlatformTransactionManager batchTxManager)
            throws Exception {

        var factory = new JdbcJobRepositoryFactoryBean();
        factory.setDataSource(batchDataSource);
        factory.setTransactionManager(batchTxManager);
        factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        factory.setTablePrefix("BATCH_");  // Prefixo padrão
        factory.afterPropertiesSet();

        return factory.getObject();
    }
}
