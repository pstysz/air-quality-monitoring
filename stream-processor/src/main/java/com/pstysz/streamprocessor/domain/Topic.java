package com.pstysz.streamprocessor.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

@Getter
@RequiredArgsConstructor
public class Topic<K, V> {
    private final String name;
    private final Serde<K> keySerde;
    private final Serde<V> valueSerde;

    public Consumed<K, V> consumed() {
        return Consumed.with(keySerde, valueSerde);
    }

    public Produced<K, V> produced() {
        return Produced.with(keySerde, valueSerde);
    }

    public <Vwith extends SpecificRecord> Joined<K, V, Vwith> joinedWith(Serde<Vwith> withValueSerde) {
        return Joined.with(keySerde, valueSerde, withValueSerde);
    }

    public Materialized<K, V, KeyValueStore<Bytes, byte[]>> materialized() {
        return Materialized.<K, V, KeyValueStore<Bytes, byte[]>>as(name + "-store")
                .withKeySerde(keySerde)
                .withValueSerde(valueSerde);
    }
}
