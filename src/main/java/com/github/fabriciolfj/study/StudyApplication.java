package com.github.fabriciolfj.study;

import com.github.fabriciolfj.study.configuration.MessageServiceRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableJdbcRepositories
@SpringBootApplication
@EnableResilientMethods
@Import(MessageServiceRegister.class)
public class StudyApplication {

	static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}

}
