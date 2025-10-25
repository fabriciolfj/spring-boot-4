package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.entity.Employee;
import com.github.fabriciolfj.study.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService service;

    //passar no header o X-API-VERSION: 1
    @PostMapping(version = "1")
    @Retryable(maxAttempts = 3, delay = 2000, multiplier = 2.0, jitter = 10)
    @ConcurrencyLimit(value = 2)
    public void create(@RequestBody final Employee employee) {
        log.info("request received api version 1");
        service.executeSave(employee);
    }

    @PostMapping(version = "2")
    public void createEmployer(@RequestBody final Employee employee) {
        log.info("request received api version 2");
        service.executeSave(employee);
    }
}
