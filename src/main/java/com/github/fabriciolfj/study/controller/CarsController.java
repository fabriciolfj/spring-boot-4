package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.entity.Car;
import com.github.fabriciolfj.study.service.CarsService;
import com.github.fabriciolfj.study.validation.CreateCar;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarsController {

    private final CarsService carsService;

    @PostMapping
    public void save(@Validated(CreateCar.class) @RequestBody final Car car) {
        carsService.create(car);
    }

    @GetMapping("/{id}")
    public Car findCar(@PathVariable("id") final Long id) {
        return carsService.findById(id);
    }
}
