package com.github.fabriciolfj.study.configuration;

import com.github.fabriciolfj.study.join.ProductDetailsJoin;
import com.study.details.Detalhes;
import com.study.preco.TabelaPreco;
import com.study.produto.Produto;
import com.study.produtodetalhes.ProdutoDetalhes;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopologyProductTableConfiguration {

    @Value("${topic.product}")
    private String topicProduct;
    @Value("${topic.price}")
    private String topicTable;
    @Value("${topic.details}")
    private String topicDetais;
    @Value("${topic.productDetails}")
    private String topicProductDetails;
    private final SpecificAvroSerde<Produto> productSerde;
    private final SpecificAvroSerde<TabelaPreco> tableSerde;
    private final SpecificAvroSerde<Detalhes> detalhesSerde;
    private final SpecificAvroSerde<ProdutoDetalhes> produtoDetalhesSerde;
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

    @Autowired
    public KStream<String, Detalhes> topoloyDetalhes() {
        final Serde<String> stringSerde = Serdes.String();

        final KStream<String, Detalhes> detalhesStream = streamsBuilder.stream(topicDetais, Consumed.with(stringSerde, detalhesSerde));
        final KStream<String, Produto> productStream = streamsBuilder.stream(topicProduct, Consumed.with(stringSerde, productSerde));
        var joinProductDetails = new ProductDetailsJoin();
        var thirtyMinuteWindow = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(30)).after(Duration.ofSeconds(6));

        productStream.join(detalhesStream,
                joinProductDetails,
                thirtyMinuteWindow,
                StreamJoined.with(stringSerde, productSerde, detalhesSerde)
                        .withName("product-details")
                        .withStoreName("product-join-details"))
                .peek((key, value) -> log.info("join value {}, key {}", key, value))
                .to(topicProductDetails, Produced.with(stringSerde, produtoDetalhesSerde));

        return detalhesStream;
    }
}
