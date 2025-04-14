package com.pstysz.streamprocessor.utils;

import com.pstysz.streamprocessor.config.KafkaStreamsConfig;
import com.pstysz.streamprocessor.config.KafkaTopicConfig;
import com.pstysz.streamprocessor.domain.StreamType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTopicInitializer {

    private final KafkaStreamsConfig kafkaStreamsConfig;
    private final KafkaTopicConfig kafkaTopicConfig;

    @PostConstruct
    public void initializeOutputTopics() {
        try (AdminClient adminClient = AdminClient.create(kafkaStreamsConfig.getProps())) {
            List<NewTopic> topicsToCreate = new ArrayList<>();
            List<StreamType> outputStreams = Arrays.stream(StreamType.values())
                    .filter(StreamType::isOutput)
                    .toList();

            for (StreamType streamType : outputStreams) {
                String topicName = kafkaTopicConfig.getTopicForType(streamType);

                if (!topicExists(adminClient, topicName)) {
                    log.info("Topic '{}' not found. It will be created.", topicName);
                    topicsToCreate.add(new NewTopic(topicName, kafkaTopicConfig.getPartitions(), kafkaTopicConfig.getReplicationFactor()));
                } else {
                    log.info("Topic '{}' already exists.", topicName);
                }
            }

            if (!topicsToCreate.isEmpty()) {
                adminClient.createTopics(topicsToCreate).all().get();
                log.info("Successfully created missing topics: {}", topicsToCreate);
            }

        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                log.warn("Topic already exists: {}", e.getCause().getMessage());
            } else {
                log.error("Error creating topics", e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Topic creation was interrupted", e);
        }
    }

    private boolean topicExists(AdminClient adminClient, String topicName) throws ExecutionException, InterruptedException {
        return adminClient.listTopics().names().get().contains(topicName);
    }
}
