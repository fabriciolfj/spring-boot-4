package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.clients.CarClient;
import com.github.fabriciolfj.study.dto.CarDTO;
import com.github.fabriciolfj.study.exceptions.CarNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.smartcardio.CardNotPresentException;
import java.util.random.RandomGenerator;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {

    private final CarClient carClient;
    private final static RandomGenerator RANDOM_GENERATOR = RandomGenerator.getDefault();

    @Cacheable(value = "cars-by-place", cacheManager = "carCacheManager")
    public CarDTO getCar() {
        log.info("executou");
        var result = RANDOM_GENERATOR.nextInt(0, 1000);
        if (result % 2 == 0) {
            throw new CarNotFoundException("card not found");
        }

        return carClient.firstCar();
    }
}
