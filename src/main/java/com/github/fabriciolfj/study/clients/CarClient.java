package com.github.fabriciolfj.study.clients;

import com.github.fabriciolfj.study.dto.CarDTO;
import org.springframework.web.service.annotation.GetExchange;

public interface CarClient {

    @GetExchange
    CarDTO firstCar();
}
