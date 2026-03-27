package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.service.DummyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/dummy")
public class DummyController {

    private final DummyService dummyService;

    @PostMapping
    public String test(@RequestBody final String value) {
        return dummyService.foo(value);
    }
}
