package com.pstysz.sensorproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${custom.kafka.stations-topic}")
    private String stationTopic;

    @Value("${custom.kafka.measurements-topic}")
    private String measurementTopic;

    @Bean
    public NewTopic stationDataTopic() {
        return TopicBuilder.name(stationTopic)
                .partitions(3)
                .replicas(1)
                .config("cleanup.policy", "compact")
                .build();
    }

    @Bean
    public NewTopic measurementTopic() {
        return TopicBuilder.name(measurementTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}