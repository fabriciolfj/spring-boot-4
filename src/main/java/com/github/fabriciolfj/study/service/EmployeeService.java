package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.entity.Employee;
import com.github.fabriciolfj.study.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void executeSave(final Employee employee) {
        repository.insert(employee);
    }
}
