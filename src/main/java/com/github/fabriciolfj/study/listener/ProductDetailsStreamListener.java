package com.github.fabriciolfj.study.listener;

import com.study.details.Detalhes;
import com.study.produtodetalhes.ProdutoDetalhes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDetailsStreamListener {

    @KafkaListener(topics = {"${topic.productDetails}"})
    public void listener(final ProdutoDetalhes productDetails) {
        log.info("recebimento do evento productDetails {}", productDetails.toString());
    }
}
