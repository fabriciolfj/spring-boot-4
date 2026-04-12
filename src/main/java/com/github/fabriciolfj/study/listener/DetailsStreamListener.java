package com.github.fabriciolfj.study.listener;

import com.study.details.Detalhes;
import com.study.preco.TabelaPreco;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetailsStreamListener {

    @RetryableTopic(
            attempts         = "4",
            backOff          = @BackOff(delay = 30_000, multiplier = 4.0, maxDelay = 120_000),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix   = "-dlt",
            include          = { Exception.class }
    )
    @KafkaListener(topics = {"${topic.details}"})
    public void listener(final Detalhes detalhes) {
        log.info("recebimento do evento detalhes {}", detalhes.toString());

        var random = RandomGenerator.getDefault()
                .nextInt();
        if (random % 2 ==0) {
            throw new RuntimeException("test retry topic kafka");
        }
    }
}
