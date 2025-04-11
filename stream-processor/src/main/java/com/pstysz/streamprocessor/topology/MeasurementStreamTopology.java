package com.pstysz.streamprocessor.topology;

import com.pstysz.streamprocessor.config.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeasurementStreamTopology {

    private final KafkaTopicConfig kafkaTopicConfig;

    public Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        builder.stream(kafkaTopicConfig.getMeasurementsTopic())
                .peek((key, value) -> System.out.println("Received: " + key + " -> " + value));

        return builder.build();
    }
}
