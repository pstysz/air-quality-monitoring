package com.pstysz.sensorproducer.config;

import com.pstysz.sensorproducer.model.AirQualityMeasurement;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    //ToDo: add avro later + specific record handling
//    @Value("${kafka.schema-registry}")
//    private String schemaRegistryUrl;

    @Bean
    public ProducerFactory<String, AirQualityMeasurement> producerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, AirQualityMeasurement> kafkaTemplate(ProducerFactory<String, AirQualityMeasurement> pf) {
        return new KafkaTemplate<>(pf);
    }

    private Map<String, Object> baseProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }
}
