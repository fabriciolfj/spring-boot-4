package com.github.fabriciolfj.study.configuration;


import com.github.fabriciolfj.study.entity.SalesEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

public class SalesProcessor implements Processor<String, SalesEvent, String, Double> {

    private ProcessorContext<String, Double> context;
    private KeyValueStore<String, Double> stateStore;

    @Override
    public void init(ProcessorContext<String, Double> context) {
        this.context = context;
        this.stateStore = context.getStateStore("sales-totals");

        // Schedule punctuation a cada 30 segundos
        context.schedule(Duration.ofSeconds(30), PunctuationType.WALL_CLOCK_TIME,
                timestamp -> {
                    // Emite totais periodicamente
                    stateStore.all().forEachRemaining(entry -> {
                        Record<String, Double> record = new Record<>(
                                entry.key,
                                entry.value,
                                timestamp
                        );
                        context.forward(record);
                    });
                });
    }

    @Override
    public void process(Record<String, SalesEvent> record) {
        String userId = record.value().getUserId();
        Double currentTotal = stateStore.get(userId);

        if (currentTotal == null) currentTotal = 0.0;

        Double newTotal = currentTotal + record.value().getAmount();
        stateStore.put(userId, newTotal);

        // Forward apenas se total > 1000
        if (newTotal > 1000.0) {
            context.forward(record.withValue(newTotal));
        }
    }

    // 2. Construção da Topologia
    @Bean
    public Topology processorTopology() {
        Topology topology = new Topology();

        // Source
        topology.addSource("sales-source", "sales-events");

        // State Store
        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore("sales-totals"),
                        Serdes.String(),
                        Serdes.Double()),
                "sales-processor");

        // Processor
        topology.addProcessor("sales-processor",
                () -> new SalesProcessor(),
                "sales-source");

        // Sink
        topology.addSink("high-value-sink",
                "high-value-customers",
                "sales-processor");

        return topology;
    }
}
