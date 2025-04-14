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

    @Value("${custom.kafka.topics.agg-measurement-5m}")
    private String aggMeasurement5mTopic;

    @Value("${custom.kafka.topics.agg-measurement-1h}")
    private String aggMeasurement1hTopic;

    @Value("${custom.kafka.topics.measurement-trend}")
    private String measurementTrendTopic;

    public String getTopicForType(StreamType type) {
        return switch (type) {
            case MEASUREMENT -> sensorTopic;
            case STATION -> stationsTopic;
            case SENSOR_WITH_STATION_DATA -> sensorWithStationDataTopic;
            case SENSOR_TO_STATION -> sensorToStationTopic;
            case AGG_MEASUREMENT_5M -> aggMeasurement5mTopic;
            case AGG_MEASUREMENT_1H -> aggMeasurement1hTopic;
            case MEASUREMENT_TREND -> measurementTrendTopic;
        };
    }
}
