package com.github.fabriciolfj.study.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
public class SmsMessageService implements NotificationService{


    @Override
    public void process() {
        log.info("sms notify");
    }
}
