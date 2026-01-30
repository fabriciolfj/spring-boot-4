package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.dto.OrderDTO;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    @Observed(name = "order.create", contextualName = "processOrder")
    public OrderDTO createOrder() {
        return OrderDTO.builder()
                .id(UUID.randomUUID().toString())
                .description("test")
                .build();
    }
}
