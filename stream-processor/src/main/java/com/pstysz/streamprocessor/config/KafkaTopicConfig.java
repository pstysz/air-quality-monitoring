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

    @Value("${custom.kafka.topics.sensor-measurements}")
    private String sensorTopic;

    @Value("${custom.kafka.topics.measuring-stations}")
    private String stationsTopic;

    @Value("${custom.kafka.topics.sensor-with-station-data}")
    private String sensorWithStationDataTopic;

    @Value("${custom.kafka.topics.sensor-to-station}")
    private String sensorToStationTopic;

    public String getTopicForType(StreamType type) {
        return switch (type) {
            case MEASUREMENT -> sensorTopic;
            case STATION -> stationsTopic;
            case SENSOR_WITH_STATION_DATA -> sensorWithStationDataTopic;
            case SENSOR_TO_STATION -> sensorToStationTopic;
        };
    }
}
