package com.github.fabriciolfj.study.configuration;

import com.study.preco.TabelaPreco;
import com.study.produto.Produto;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopologyProductTableConfiguration {

    @Value("${topic.product}")
    private String topicProduct;
    @Value("${topic.price}")
    private String topicTable;
    private final SpecificAvroSerde<Produto> productSerde;
    private final SpecificAvroSerde<TabelaPreco> tableSerde;
    private final StreamsBuilder streamsBuilder;

    private static final ValueMapper<Produto, TabelaPreco> tableMapper = product -> {
        var margin = product.getCusto().multiply(BigDecimal.valueOf(0.34));
        var price = product.getCusto().add(margin);
        return TabelaPreco.newBuilder()
                .setIdProduto(product.id)
                .setMargem(margin)
                .setPreco(price)
                .build();
    };

    @Autowired
    public KStream<String, Produto> topoloyProduct() {
        final Serde<String> stringSerde = Serdes.String();
        var stream = streamsBuilder.stream(topicProduct, Consumed.with(stringSerde, productSerde));
        stream
                .peek((key, value) -> log.info("recebimento evento produto {} {}", key, value.id)
                )
                .mapValues(tableMapper)
                .to(topicTable, Produced.with(stringSerde, tableSerde));

        return stream;

    }
}
