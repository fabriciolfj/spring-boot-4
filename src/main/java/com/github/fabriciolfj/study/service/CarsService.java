package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.entity.Car;
import com.github.fabriciolfj.study.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarsService {

    private final CarRepository carRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void create(final Car car) {
        carRepository.save(car);
    }

    @Transactional(readOnly = true)
    public Car findById(final Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("car not found"));
    }
}
