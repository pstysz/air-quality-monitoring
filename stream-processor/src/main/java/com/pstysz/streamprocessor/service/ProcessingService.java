package com.pstysz.streamprocessor.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final KafkaStreams streams;

    public Double getSensorTotal(String sensorId) {

        ReadOnlyKeyValueStore<String, Double> store =
                streams.store(StoreQueryParameters.fromNameAndType("sensor-sum-store", QueryableStoreTypes.keyValueStore()));

        return store.get(sensorId);
    }
}
