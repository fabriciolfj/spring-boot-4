package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.entity.Car;
import com.github.fabriciolfj.study.service.CarsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarsController {

    private final CarsService carsService;

    @PostMapping
    public void save(@RequestBody final Car car) {
        carsService.create(car);
    }

    @GetMapping("/{id}")
    public Car findCar(@PathVariable("id") final Long id) {
        return carsService.findById(id);
    }
}
