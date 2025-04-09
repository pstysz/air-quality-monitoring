package com.pstysz.sensorproducer.service;

import com.pstysz.sensorproducer.config.OpenAqApiConfig;
import com.pstysz.sensorproducer.model.DataRecord;
import com.pstysz.sensorproducer.parser.JsonToMeasurementParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T extends DataRecord> {

    private final JsonToMeasurementParser<T> parser;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, T> kafkaTemplate;
    protected final OpenAqApiConfig config;

    abstract void fetchAndSend();

    protected void process(String url) {
        ResponseEntity<String> response = fetch(url);

        if (isSuccessful(response)) {
            parser.parse(response.getBody()).ifPresent(measurement -> {
                sendToKafka(config.getTopic(), measurement.kafkaKey(), measurement);
            });
        }
    }

    ResponseEntity<String> fetch(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", config.getApiKey());
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
    }

    boolean isSuccessful(ResponseEntity<String> response) {
        boolean success = response.getStatusCode().is2xxSuccessful() && response.getBody() != null;

        if (!success) {
            log.warn("Request failed. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
        }

        return success;
    }

    void sendToKafka(String topic, String key, T value) {
        kafkaTemplate.send(topic, key, value);
        log.info("Sent to Kafka [{}]: {}", topic, value);
    }
}
