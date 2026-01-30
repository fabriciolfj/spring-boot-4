package com.github.fabriciolfj.study.configuration;

import com.github.fabriciolfj.study.service.EmailMessageService;
import com.github.fabriciolfj.study.service.SmsMessageService;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MessageServiceRegister implements BeanRegistrar {

    @Override
    public void register(BeanRegistry registry, Environment env) {
        final String messageType = env.getProperty("app.message", "sms");
        switch (messageType.toLowerCase()) {
            case "email" -> {
                registry.registerBean("notificationService", EmailMessageService.class,
                        spec -> spec.description("notificacao via email"));

            }
            case "sms" -> {
                registry.registerBean("notificationService", SmsMessageService.class,
                        spec -> spec.description("notificacao via sms"));
            }
        }

    }
}
