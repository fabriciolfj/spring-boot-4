package com.github.fabriciolfj.study.configuration;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.*;
import java.util.stream.Collectors;

@EnableKafkaStreams
@Configuration
@Slf4j
public class KafkaStreamCustomConfiguration {

    @Value("${spring.kafka.bootstrap-servers[0]:localhost:29092}")
    private String server1;

    @Value("${spring.kafka.bootstrap-servers[1]:localhost:29093}")
    private String server2;

    @Value("${spring.kafka.bootstrap-servers[2]:localhost:29094}")
    private String server3;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Value("${spring.kafka.streams.application-id}")
    private String applicationId;

    @Bean
    public Map<String, Object> kafkaStreamsProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("specific.avro.reader", true);

        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);

        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 3);

        return props;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    @Primary
    public KafkaStreamsConfiguration kStreamConfig() {
        return new KafkaStreamsConfiguration(kafkaStreamsProperties());
    }

    private List<String> getBootstrapServers() {
        return Arrays.asList(server1, server2, server3)
                .stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}