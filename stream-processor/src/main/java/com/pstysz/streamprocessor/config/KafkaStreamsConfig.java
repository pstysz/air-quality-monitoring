package com.pstysz.streamprocessor.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.Getter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


@Getter
@Configuration
public class KafkaStreamsConfig {

    private final Properties props;

    public KafkaStreamsConfig(
            @Value("${custom.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.application.name}") String applicationId,
            @Value("${custom.kafka.schema-registry}") String schemaRegistryUrl
    ) {
        this.props = new Properties();
        this.props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        this.props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        this.props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        this.props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    }
}
