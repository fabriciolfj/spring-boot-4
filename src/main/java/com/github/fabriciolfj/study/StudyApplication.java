package com.github.fabriciolfj.study;

import com.github.fabriciolfj.study.configuration.MessageServiceRegister;
import com.github.fabriciolfj.study.service.PgVectorBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableTransactionManagement
@EnableJdbcRepositories
@SpringBootApplication
@EnableResilientMethods
@Import(MessageServiceRegister.class)
@EnableJpaRepositories(
		basePackages = "com.github.fabriciolfj.study.repositories",
		entityManagerFactoryRef = "entityManagerFactory",
		transactionManagerRef = "transactionManager"
)
public class StudyApplication implements CommandLineRunner {

	@Autowired
	private PgVectorBookService pgVectorBookService;

	static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		pgVectorBookService.save();
		log.info("saved book new");

		pgVectorBookService.searchByYear();
		log.info("===================");
		pgVectorBookService.searchByYearRangeSimilarity();
	}
}
