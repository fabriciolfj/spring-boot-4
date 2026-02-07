package com.github.fabriciolfj.study.listener;

import com.study.greeting.GreetingTest;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;

@Component
@KafkaListener(topics = "greeting")
public class GreetingListener {

    @KafkaHandler
    @RetryableTopic(
            backOff = @BackOff(value = 3000l),
            attempts = "5",
            autoCreateTopics = "false",
            include = SocketTimeoutException.class, exclude = NullPointerException.class)
    public void handleGreeting(final GreetingTest greeting) {
        IO.println("greeting receive " + greeting);
    }
}
