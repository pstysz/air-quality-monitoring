package com.pstysz.streamprocessor.domain;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;

public class AvroTopic<K, V extends SpecificRecord> extends Topic<K, V> {
    public AvroTopic(String name, Serde<K> keySerde, SpecificAvroSerde<V> valueSerde) {
        super(name, keySerde, valueSerde);
    }

    public SpecificAvroSerde<V> avroSerde() {
        return (SpecificAvroSerde<V>) super.getValueSerde();
    }
}
