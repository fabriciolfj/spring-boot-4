package com.github.fabriciolfj.study.configuration;


import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository(
        dataSourceRef = "batchDataSource",
        transactionManagerRef = "batchTransactionManager",
        tablePrefix = "BATCH_",
        maxVarCharLength = 1000)
public class MyJobConfiguration {
}
