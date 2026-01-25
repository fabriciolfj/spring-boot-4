package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.dto.CarDTO;
import com.github.fabriciolfj.study.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/cars")
@RestController
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public CarDTO queryCar() {
        return carService.getCar();
    }
}
