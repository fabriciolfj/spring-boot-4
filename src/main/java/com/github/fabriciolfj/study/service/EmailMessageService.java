package com.github.fabriciolfj.study.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
public class EmailMessageService implements NotificationService {

    @Override
    public void process() {
        log.info("email notify");
    }
}
