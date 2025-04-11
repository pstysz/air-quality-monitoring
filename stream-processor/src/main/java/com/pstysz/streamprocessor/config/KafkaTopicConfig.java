package com.pstysz.streamprocessor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KafkaTopicConfig {
    @Value("${custom.kafka.measurements-topic}")
    private String measurementsTopic;

    @Value("${custom.kafka.stations-topic}")
    private String stationsTopic;
}
