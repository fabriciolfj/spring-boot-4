package com.github.fabriciolfj.study.configuration;

import com.github.fabriciolfj.study.entity.Employee;
import com.github.fabriciolfj.study.entity.EmployeeId;
import com.github.fabriciolfj.study.entity.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

import java.util.concurrent.atomic.AtomicLong;

@Configuration
@RequiredArgsConstructor
public class GenerationIdConfig extends AbstractJdbcConfiguration {

    @Bean
    BeforeConvertCallback<Employee> idGeneration() {
        return new BeforeConvertCallback<>() {
            AtomicLong counter = new AtomicLong();

            @Override
            public Employee onBeforeConvert(Employee employee) {
                if (employee.getId() == null) {
                    employee.setId(new EmployeeId(Organization.RND, counter.addAndGet(1)));
                }
                return employee;
            }
        };
    }
}
