package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.entity.Sales;
import com.github.fabriciolfj.study.repositories.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales")
public class SalesController {

    private final SalesRepository salesRepository;

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final Sales sales) {
        salesRepository.save(sales);
    }

    @Transactional(readOnly = false)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Sales> get() {
        return salesRepository.findAll();
    }
}
