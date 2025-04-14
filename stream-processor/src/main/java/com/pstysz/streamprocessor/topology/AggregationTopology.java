package com.pstysz.streamprocessor.topology;

import com.pstysz.airquality.model.AggregatedMeasurement;
import com.pstysz.airquality.model.SensorMeasurement;
import com.pstysz.streamprocessor.domain.AvroTopic;
import com.pstysz.streamprocessor.domain.StreamType;
import com.pstysz.streamprocessor.topology.helpers.AggregationAccumulator;
import com.pstysz.streamprocessor.topology.helpers.AggregationAccumulatorSerde;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;

import static com.pstysz.streamprocessor.topology.helpers.AggregationConst.granularity;
import static com.pstysz.streamprocessor.topology.helpers.AggregationConst.windowSize;

@RequiredArgsConstructor
public abstract class AggregationTopology {

    private final KStream<String, SensorMeasurement> inputStream;
    private final AvroTopic<String, AggregatedMeasurement> outputTopic;
    private final StreamType streamType;

    @PostConstruct
    public void build() {

        inputStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowSize(streamType)))
                .aggregate(
                        AggregationAccumulator::new,
                        (key, value, acc) -> acc.add(value),
                        Materialized.<String, AggregationAccumulator, WindowStore<Bytes, byte[]>>as(outputTopic.storeName())
                                .withKeySerde(outputTopic.getKeySerde())
                                .withValueSerde(new AggregationAccumulatorSerde())
                )
                .toStream()
                .mapValues((windowedKey, acc) ->
                        acc.toAvro(
                                windowedKey.key(),
                                windowedKey.window().start(),
                                windowedKey.window().end(),
                                granularity(streamType)
                        )
                )
                .to(outputTopic.getName(), Produced.with(WindowedSerdes.timeWindowedSerdeFrom(
                        String.class, windowSize(streamType).toMillis()), outputTopic.getValueSerde())
                );
    }
}
