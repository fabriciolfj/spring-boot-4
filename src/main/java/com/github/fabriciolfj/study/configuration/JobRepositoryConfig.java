package com.github.fabriciolfj.study.configuration;

import org.springframework.batch.core.job.DefaultJobKeyGenerator;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JdbcJobRepositoryFactoryBean;
import org.springframework.batch.infrastructure.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.batch.infrastructure.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class JobRepositoryConfig {

    @Bean
    public JobRepository jobRepository(@Qualifier("batchDataSource") DataSource batchDataSource,
                                       @Qualifier("batchTransactionManager") PlatformTransactionManager batchTransactionManager,
                                       DataFieldMaxValueIncrementerFactory incrementerFactory) throws Exception {
        JdbcJobRepositoryFactoryBean factory = new JdbcJobRepositoryFactoryBean();
        factory.setDataSource(batchDataSource);
        factory.setDatabaseType("h2");
        factory.setTransactionManager(batchTransactionManager);
        factory.setIncrementerFactory(incrementerFactory);
        factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        factory.setTablePrefix("BATCH_");
        factory.afterPropertiesSet();
        factory.setJobKeyGenerator(new DefaultJobKeyGenerator());
        return factory.getObject();
    }

    @Bean
    public DataFieldMaxValueIncrementerFactory incrementerFactory(@Qualifier("batchDataSource") DataSource batchDataSource) {
        return new DefaultDataFieldMaxValueIncrementerFactory(batchDataSource);
    }

    @Bean
    public JobOperatorFactoryBean jobOperator(JobRepository jobRepository) {
        JobOperatorFactoryBean jobOperatorFactoryBean = new JobOperatorFactoryBean();
        jobOperatorFactoryBean.setJobRepository(jobRepository);
        return jobOperatorFactoryBean;
    }
}
