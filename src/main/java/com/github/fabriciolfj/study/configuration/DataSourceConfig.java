package com.github.fabriciolfj.study.configuration;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

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
     * DataSource PRINCIPAL para dados de negócio.
     *
     * Configurado via application.yml com suas credenciais.
     * Este é o DataSource que suas entidades JPA vão usar.
     */
    @Bean(name = "businessDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties businessDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "writeDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource businessDataSource(
            @Qualifier("businessDataSource") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource-read")
    public DataSourceProperties readDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("readDataSource")
    public DataSource readDataSource() {
        return readDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    // ── ROUTING — decide qual usar com base no @Transactional
    @Primary
    @Bean("dataSource")
    @DependsOn({"writeDataSource", "readDataSource"}) // garante ordem
    public DataSource routingDataSource(
            @Qualifier("writeDataSource") DataSource write,
            @Qualifier("readDataSource")  DataSource read) {

        var routing = new ReadWriteRoutingDataSource();

        routing.setTargetDataSources(Map.of(
                "write", write,
                "read",  read
        ));
        routing.setDefaultTargetDataSource(write);
        routing.afterPropertiesSet();
        return new LazyConnectionDataSourceProxy(routing);
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
             EntityManagerFactory entityManagerFactory) {
        var jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Bean
    @DependsOn({"flywayWrite", "flywayRead"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource) {

        var factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.github.fabriciolfj.study.entity");

        var adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.POSTGRESQL);
        factory.setJpaVendorAdapter(adapter);

        var props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "validate");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        factory.setJpaProperties(props);

        return factory;
    }
}
