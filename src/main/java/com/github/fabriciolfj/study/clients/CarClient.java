package com.github.fabriciolfj.study.clients;

import com.github.fabriciolfj.study.dto.CarDTO;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "${car.host}", accept = "application/json")
public interface CarClient {

    @GetExchange
    CarDTO firstCar();
}
