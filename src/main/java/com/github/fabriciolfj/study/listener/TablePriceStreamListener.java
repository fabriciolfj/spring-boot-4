package com.github.fabriciolfj.study.listener;

import com.study.preco.TabelaPreco;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TablePriceStreamListener {

    @KafkaListener(topics = {"${topic.price}"})
    public void listener(final TabelaPreco tabelaPreco) {
        log.info("recebimento do evento {}", tabelaPreco.toString());
    }
}
