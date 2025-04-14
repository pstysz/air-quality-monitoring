package com.pstysz.streamprocessor.config;

import com.pstysz.streamprocessor.domain.StreamType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KafkaTopicConfig {

    @Value("${custom.kafka.partitions:3}")
    private int partitions;

    @Value("${custom.kafka.replication-factor:1}")
    private short replicationFactor;

    @Value("${custom.kafka.topics.measurements}")
    private String measurementsTopic;

    @Value("${custom.kafka.topics.stations}")
    private String stationsTopic;

    @Value("${custom.kafka.topics.station-measurements}")
    private String stationMeasurementsTopic;

    @Value("${custom.kafka.topics.measurement-to-station}")
    private String measurementToStationTopic;

    public String getTopicForType(StreamType type) {
        return switch (type) {
            case MEASUREMENT -> measurementsTopic;
            case STATION -> stationsTopic;
            case STATION_MEASUREMENT -> stationMeasurementsTopic;
            case MEASUREMENT_TO_STATION -> measurementToStationTopic;
        };
    }
}
