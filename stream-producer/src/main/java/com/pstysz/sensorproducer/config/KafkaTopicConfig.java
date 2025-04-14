package com.pstysz.sensorproducer.config;

import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Getter
@Configuration
public class KafkaTopicConfig {

    @Value("${custom.kafka.topics.measuring-stations}")
    private String stationTopic;

    @Value("${custom.kafka.topics.sensor-measurements}")
    private String sensorTopic;

    @Bean
    public NewTopic stationNewTopic() {
        return TopicBuilder.name(stationTopic)
                .partitions(3)
                .replicas(1)
                .config("cleanup.policy", "compact")
                .build();
    }

    @Bean
    public NewTopic sensorNewTopic() {
        return TopicBuilder.name(sensorTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}