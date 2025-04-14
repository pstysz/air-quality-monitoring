package com.pstysz.streamprocessor.topology.helpers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;


public class AggregationAccumulatorSerde implements Serde<AggregationAccumulator> {
    private final ObjectMapper mapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    @Override
    public Serializer<AggregationAccumulator> serializer() {
        return (topic, data) -> {
            try {
                return mapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new SerializationException(e);
            }
        };
    }

    @Override
    public Deserializer<AggregationAccumulator> deserializer() {
        return (topic, data) -> {
            try {
                return mapper.readValue(data, AggregationAccumulator.class);
            } catch (Exception e) {
                throw new SerializationException(e);
            }
        };
    }
}
