package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notify")
public class NotificacaoController {

    private final NotificationService notificationService;

    @GetMapping()
    public void sendNotify() {
        notificationService.process();
    }
}
