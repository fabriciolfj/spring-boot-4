package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.dto.OrderDTO;
import com.github.fabriciolfj.study.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping()
    public OrderDTO getOrderCreated() {
        return orderService.createOrder();
    }
}
