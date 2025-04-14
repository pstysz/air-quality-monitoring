package com.pstysz.streamprocessor.utils;

import com.pstysz.streamprocessor.config.KafkaStreamsConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamsInitializer {

    @Bean
    public KafkaStreams kafkaStreams(StreamTopologyBuilder topology, KafkaStreamsConfig config) {
        return new KafkaStreams(topology.build(), config.getProps());
    }

    @Bean
    public SmartLifecycle kafkaStreamsLifecycle(KafkaStreams kafkaStreams) {
        return new SmartLifecycle() {
            private boolean running = false;

            @Override
            public void start() {
                kafkaStreams.start();
                running = true;
            }

            @Override
            public void stop() {
                kafkaStreams.close();
                running = false;
            }

            @Override
            public boolean isRunning() {
                return running;
            }
        };
    }
}
