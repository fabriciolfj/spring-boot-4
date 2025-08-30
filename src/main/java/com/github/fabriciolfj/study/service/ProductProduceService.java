package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.dto.ProductDTO;
import com.study.produto.Produto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductProduceService {

    @Value("${topic.product}")
    private String topic;
    private final KafkaTemplate<String, Produto> kafkaTemplate;

    public void send(final ProductDTO dto) {
        var avro = Produto.newBuilder()
                .setId(dto.getId())
                .setNome(dto.getName())
                .setCusto(dto.getCost())
                .build();

        kafkaTemplate.send(topic, avro).whenComplete((value, ex) -> {
            if (ex == null) {
                log.info("mensagem enviada com sucesso para topic {}, produto {}", topic, dto.getId());
            }
        });

    }
}
