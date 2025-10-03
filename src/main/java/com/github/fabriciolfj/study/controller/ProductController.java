package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.dto.ProductDTO;
import com.github.fabriciolfj.study.service.ProductProduceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

    private final ProductProduceService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody final ProductDTO dto) {
        log.info("request recebida {}", dto);

        service.send(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findProduct(@PathVariable("id") final Long id) {
        var product = service.getProduct(id);
        return product
                .map(productDTO -> ResponseEntity.accepted().body(productDTO))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }
}
