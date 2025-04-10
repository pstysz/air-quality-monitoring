package com.pstysz.sensorproducer.service;

import com.pstysz.sensorproducer.config.OpenAqApiConfig;
import com.pstysz.sensorproducer.parser.JsonToRecordParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T extends SpecificRecord> {

    private static final String TRACE_ID_HEADER = "traceId";
    private static final String EVENT_TYPE_HEADER = "eventType";
    private final JsonToRecordParser<T> parser;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    protected final OpenAqApiConfig config;

    abstract void fetchAndSend();

    abstract String extractKey(T measurement);

    protected void process(String url) {
        ResponseEntity<String> response = fetch(url);

        if (isSuccessful(response)) {
            parser.parse(response.getBody()).ifPresent(measurement -> {
                sendToKafka(config.getTopic(), extractKey(measurement), measurement);
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
        String traceId = MDC.get(TRACE_ID_HEADER);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        ProducerRecord<String, SpecificRecord> record = new ProducerRecord<>(topic, key, value);
        record.headers().add(TRACE_ID_HEADER, traceId.getBytes());
        record.headers().add(EVENT_TYPE_HEADER, value.getClass().getName().getBytes());

        kafkaTemplate.send(record);
        log.info("Send event {} on topic {}", value.getClass().getSimpleName(), topic);
    }
}
