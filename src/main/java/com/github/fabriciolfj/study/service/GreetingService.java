package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.dto.Greeting;
import com.study.greeting.GreetingTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class GreetingService {

    private final KafkaTemplate<String, GreetingTest> template;
    private final String topic;

    public GreetingService(
            final ObjectMapper objectMapper,
            final KafkaTemplate<String, GreetingTest> template,
            @Value("${topic.greeting}") final
            String topic) {
        this.template = template;
        this.topic = topic;
    }

    public void sendMessage(final Greeting greeting) {
        var greetingAvro = GreetingTest.newBuilder()
                .setName(greeting.name())
                .setText(greeting.msg())
                .build();
        template.send(topic, UUID.randomUUID().toString(), greetingAvro);
    }
}
