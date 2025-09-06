package com.github.fabriciolfj.study.listener;

import com.study.details.Detalhes;
import com.study.preco.TabelaPreco;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetailsStreamListener {

    @KafkaListener(topics = {"${topic.details}"})
    public void listener(final Detalhes detalhes) {
        log.info("recebimento do evento detalhes {}", detalhes.toString());
    }
}
