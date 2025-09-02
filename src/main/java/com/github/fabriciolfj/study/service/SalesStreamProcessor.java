package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.entity.SalesEvent;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SalesStreamProcessor {

    private KafkaStreams streams;
    private final String STORE_NAME = "sales-aggregates-store";

    @PostConstruct
    public void startStreams() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sales-analytics");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost:7070"); // Para discovery

        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, props);
        streams.start();
    }

    private Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, SalesEvent> salesStream = builder.stream("sales-events");

        // Agregação por usuário em state store
        KTable<String, Double> userTotals = salesStream
                .groupBy((key, salesEvent) -> salesEvent.getUserId())
                .aggregate(
                        () -> 0.0,
                        (userId, salesEvent, aggregate) -> aggregate + salesEvent.getAmount(),
                        Materialized.<String, Double, KeyValueStore<Bytes, byte[]>>as(STORE_NAME)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Double())
                );

        return builder.build();
    }

    public KafkaStreams getStreams() {
        return streams;
    }

    public String getStoreName() {
        return STORE_NAME;
    }
}