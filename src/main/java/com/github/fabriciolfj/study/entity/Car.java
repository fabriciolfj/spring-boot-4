package com.github.fabriciolfj.study.entity;

import com.github.fabriciolfj.study.validation.CreateCar;
import com.github.fabriciolfj.study.validation.UpdateCar;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "cars")
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotBlank(groups = UpdateCar.class)
    private Long id;
    @NotBlank(groups = UpdateCar.class)
    @NotBlank(groups = CreateCar.class)
    private String name;
}
