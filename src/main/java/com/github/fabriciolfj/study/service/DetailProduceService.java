package com.github.fabriciolfj.study.service;

import com.study.details.Detalhes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetailProduceService {

    @Value("${topic.details}")
    private String topic;
    private final KafkaTemplate<String, Detalhes> kafkaTemplate;

    public void send(final String description, final Long id) {
        var avro = Detalhes.newBuilder()
                .setId(id)
                .setDescricao(description)
                .build();

        kafkaTemplate.send(topic, avro).whenComplete((value, ex) -> {
            if (ex == null) {
                log.info("mensagem enviada com sucesso para topic {}, produto {}", topic, id);
            }
        });

    }
}
