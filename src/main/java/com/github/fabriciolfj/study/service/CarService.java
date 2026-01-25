package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.clients.CarClient;
import com.github.fabriciolfj.study.dto.CarDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarClient carClient;

    public CarDTO getCar() {
        return carClient.firstCar();
    }
}
