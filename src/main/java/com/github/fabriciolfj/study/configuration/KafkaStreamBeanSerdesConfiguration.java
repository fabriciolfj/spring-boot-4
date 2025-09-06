package com.github.fabriciolfj.study.configuration;

import com.study.details.Detalhes;
import com.study.details.DetalhesProduto;
import com.study.preco.TabelaPreco;
import com.study.produto.Produto;
import com.study.produtodetalhes.ProdutoDetalhes;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaStreamBeanSerdesConfiguration {

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean
    public SpecificAvroSerde<Produto> produtoSerde() {
        SpecificAvroSerde<Produto> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Map.of(
                "schema.registry.url", schemaRegistryUrl,
                "specific.avro.reader", "true"
        );
        serde.configure(serdeConfig, false);
        return serde;
    }

    @Bean
    public SpecificAvroSerde<TabelaPreco> tabelaPrecoSerde() {
        SpecificAvroSerde<TabelaPreco> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Map.of(
                "schema.registry.url", schemaRegistryUrl,
                "specific.avro.reader", "true"
        );
        serde.configure(serdeConfig, false);
        return serde;
    }

    @Bean
    public SpecificAvroSerde<Detalhes> detalhesSerde() {
        SpecificAvroSerde<Detalhes> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Map.of(
                "schema.registry.url", schemaRegistryUrl,
                "specific.avro.reader", "true"
        );
        serde.configure(serdeConfig, false);
        return serde;
    }

    @Bean
    public SpecificAvroSerde<ProdutoDetalhes> produtoDetalhesSerde() {
        SpecificAvroSerde<ProdutoDetalhes> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Map.of(
                "schema.registry.url", schemaRegistryUrl,
                "specific.avro.reader", "true"
        );
        serde.configure(serdeConfig, false);
        return serde;
    }
}
