package com.github.fabriciolfj.study.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fabriciolfj.study.entity.SalesAggregator;
import com.github.fabriciolfj.study.entity.SalesEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.springframework.context.annotation.Bean;
import java.time.Duration;

public class StreamsTopologyWindowing {

    @Bean
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Stream de entrada com eventos de vendas
        KStream<String, SalesEvent> salesStream = builder
                .stream("sales-events",
                        Consumed.with(Serdes.String(), salesEventSerde())
                                // Extrator customizado de timestamp do campo do evento
                                .withTimestampExtractor(new SalesEventTimestampExtractor()));

        // Agregação em janelas de tempo fixas de 1 minuto
        KTable<Windowed<String>, Double> salesByMinute = salesStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(30)))
                .aggregate(
                        () -> 0.0,
                        (key, salesEvent, aggregate) -> aggregate + salesEvent.getAmount(),
                        Materialized.with(Serdes.String(), Serdes.Double()));

        // Stream resultante das agregações
        salesByMinute.toStream()
                .map((windowedKey, totalSales) -> KeyValue.pair(
                        windowedKey.key() + "@" + windowedKey.window().startTime(),
                        totalSales))
                .to("sales-aggregated", Produced.with(Serdes.String(), Serdes.Double()));

        // Session windows para detectar sessões de compra do mesmo usuário
        KTable<Windowed<String>, Long> userSessions = salesStream
                .groupBy((key, salesEvent) -> salesEvent.getUserId())
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(Duration.ofMinutes(10))) // 10min de inatividade
                .count();

        userSessions.toStream()
                .map((sessionKey, count) -> KeyValue.pair(
                        sessionKey.key() + "-session-" + sessionKey.window().start(),
                        count))
                .to("user-sessions", Produced.with(Serdes.String(), Serdes.Long()));

        // Hopping windows para médias móveis (janela de 5min, avança a cada 1min)
        KTable<Windowed<String>, Double> movingAverage = salesStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5))
                        .advanceBy(Duration.ofMinutes(1)))
                .aggregate(
                        SalesAggregator::new,
                        (key, salesEvent, aggregator) -> {
                            aggregator.addSale(salesEvent.getAmount());
                            return aggregator;
                        },
                        Materialized.with(Serdes.String(), salesAggregatorSerde()))
                .mapValues(SalesAggregator::getAverage);

        movingAverage.toStream()
                .to("sales-moving-average", Produced.with(windowedSerde(), Serdes.Double()));

        return builder.build();
    }

    private Serde<Windowed<String>> windowedSerde() {
        return WindowedSerdes.timeWindowedSerdeFrom(String.class, Long.MAX_VALUE);
    }

    private Serde<SalesAggregator> salesAggregatorSerde() {
        return Serdes.serdeFrom(
                (topic, data) -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                       // mapper.registerModule(new JavaTimeModule());
                        return mapper.writeValueAsBytes(data);
                    } catch (Exception e) {
                        throw new RuntimeException("Error serializing SalesAggregator", e);
                    }
                },
                (topic, data) -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                       // mapper.registerModule(new JavaTimeModule());
                        return mapper.readValue(data, SalesAggregator.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error deserializing SalesAggregator", e);
                    }
                }
        );
    }

    private Serde<SalesEvent> salesEventSerde() {
        return Serdes.serdeFrom(
                (topic, data) -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                       // mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                        return mapper.writeValueAsBytes(data);
                    } catch (Exception e) {
                        throw new RuntimeException("Error serializing SalesEvent", e);
                    }
                },
                (topic, data) -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                       // mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                        return mapper.readValue(data, SalesEvent.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error deserializing SalesEvent", e);
                    }
                }
        );
    }

    // Extrator customizado de timestamp
    public static class SalesEventTimestampExtractor implements TimestampExtractor {
        @Override
        public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
            SalesEvent salesEvent = (SalesEvent) record.value();
            // Usa o timestamp do evento, não do processamento
            return salesEvent.getEventTime();
        }
    }
}