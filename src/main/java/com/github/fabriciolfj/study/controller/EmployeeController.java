package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.entity.Employee;
import com.github.fabriciolfj.study.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @PostMapping
    public void create(@RequestBody final Employee employee) {
        service.executeSave(employee);
    }
}
