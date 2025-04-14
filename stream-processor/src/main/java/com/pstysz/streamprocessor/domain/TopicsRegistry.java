package com.pstysz.streamprocessor.domain;

import com.pstysz.streamprocessor.config.KafkaStreamsConfig;
import com.pstysz.streamprocessor.config.KafkaTopicConfig;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TopicsRegistry {

    private final KafkaStreamsConfig kafkaStreamsConfig;
    private final KafkaTopicConfig kafkaTopicConfig;

    @Getter
    private final Map<StreamType, AvroTopic<?, ?>> topics = new HashMap<>();

    @PostConstruct
    private void registerTopics() {
        for (StreamType type : StreamType.values()) {
            registerTopic(type);
        }
    }

    private <K, V extends SpecificRecord> void registerTopic(StreamType type) {
        String topicName = kafkaTopicConfig.getTopicForType(type);

        Serde<K> keySerde = getSerde(type.getKeyClass());
        SpecificAvroSerde<V> valueSerde = new SpecificAvroSerde<>();
        valueSerde.configure(Map.of(
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                kafkaStreamsConfig.get(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG)
        ), false);

        topics.put(type, new AvroTopic<>(topicName, keySerde, valueSerde));
    }

    @SuppressWarnings("unchecked")
    private <K> Serde<K> getSerde(Class<K> keyClass) {
        if (keyClass.equals(String.class)) {
            return (Serde<K>) Serdes.String();
        }
        throw new IllegalArgumentException("Unsupported key class: " + keyClass);
    }

    @SuppressWarnings("unchecked")
    public <K, V extends SpecificRecord> AvroTopic<K, V> get(StreamType type) {
        return (AvroTopic<K, V>) topics.get(type);
    }
}

