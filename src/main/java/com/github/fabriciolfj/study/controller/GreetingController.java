package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.dto.Greeting;
import com.github.fabriciolfj.study.service.GreetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/greetings")
public class GreetingController {

    private final GreetingService greetingService;

    @PostMapping
    public void createGreeting(@RequestBody final Greeting greeting) {
        greetingService.sendMessage(greeting);
    }
}
