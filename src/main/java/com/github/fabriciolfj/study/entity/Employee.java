package com.github.fabriciolfj.study.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "employee")
public class Employee {
    @Id
    @EqualsAndHashCode.Include
    private EmployeeId id;
    private String name;

    @PersistenceCreator
    public Employee(final EmployeeId id, final String name) {
        this.id = id;
        this.name = name;
    }
}
