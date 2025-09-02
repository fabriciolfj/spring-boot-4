package com.github.fabriciolfj.study.service;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SalesQueryService {

    @Autowired
    private SalesStreamProcessor streamProcessor;

    // Consulta por chave específica
    public Double getUserTotal(String userId) {
        KafkaStreams streams = streamProcessor.getStreams();
        String storeName = streamProcessor.getStoreName();

        ReadOnlyKeyValueStore<String, Double> store = streams.store(
                StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore())
        );

        return store.get(userId);
    }

    // Consulta range de chaves
    public Map<String, Double> getUserTotalsRange(String from, String to) {
        KafkaStreams streams = streamProcessor.getStreams();
        ReadOnlyKeyValueStore<String, Double> store = streams.store(
                StoreQueryParameters.fromNameAndType(streamProcessor.getStoreName(),
                        QueryableStoreTypes.keyValueStore())
        );

        Map<String, Double> results = new HashMap<>();
        try (KeyValueIterator<String, Double> iterator = store.range(from, to)) {
            while (iterator.hasNext()) {
                KeyValue<String, Double> next = iterator.next();
                results.put(next.key, next.value);
            }
        }
        return results;
    }

    // Consulta todos os registros
    public Map<String, Double> getAllUserTotals() {
        ReadOnlyKeyValueStore<String, Double> store = streamProcessor.getStreams().store(
                StoreQueryParameters.fromNameAndType(streamProcessor.getStoreName(),
                        QueryableStoreTypes.keyValueStore())
        );

        Map<String, Double> results = new HashMap<>();
        try (KeyValueIterator<String, Double> iterator = store.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, Double> next = iterator.next();
                results.put(next.key, next.value);
            }
        }
        return results;
    }

    // Consulta aproximada por contagem
    public long getApproximateNumEntries() {
        ReadOnlyKeyValueStore<String, Double> store = streamProcessor.getStreams().store(
                StoreQueryParameters.fromNameAndType(streamProcessor.getStoreName(),
                        QueryableStoreTypes.keyValueStore())
        );

        return store.approximateNumEntries();
    }
}