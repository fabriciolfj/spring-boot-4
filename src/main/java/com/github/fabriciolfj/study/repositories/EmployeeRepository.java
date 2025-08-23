package com.github.fabriciolfj.study.repositories;

import com.github.fabriciolfj.study.entity.Employee;
import com.github.fabriciolfj.study.entity.EmployeeId;
import org.springframework.data.repository.CrudRepository;


public interface EmployeeRepository extends CrudRepository<Employee, EmployeeId>, InsertRepository<Employee> {

    Employee save(Employee employee);
}
