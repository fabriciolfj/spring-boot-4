package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.clients.CarClient;
import com.github.fabriciolfj.study.dto.CarDTO;
import com.github.fabriciolfj.study.exceptions.CarNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.smartcardio.CardNotPresentException;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarClient carClient;
    private final static RandomGenerator RANDOM_GENERATOR = RandomGenerator.getDefault();

    public CarDTO getCar() {
        var result = RANDOM_GENERATOR.nextInt(0, 1000);
        if (result % 2 == 0) {
            throw new CarNotFoundException("card not found");
        }

        return carClient.firstCar();
    }
}
