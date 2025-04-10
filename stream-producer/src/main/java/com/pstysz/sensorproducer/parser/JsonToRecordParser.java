package com.pstysz.sensorproducer.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class JsonToRecordParser<T extends SpecificRecord> {

    private final ObjectMapper objectMapper;

    public Optional<T> parse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode results = root.path("results");

            if (!results.isArray() || results.isEmpty()) {
                log.warn("No results found in OpenAQ response");
                return Optional.empty();
            }

            JsonNode result = results.get(0);
            return Optional.of(fromJsonNode(result));

        } catch (Exception e) {
            log.error("Error while parsing OpenAQ response", e);
            return Optional.empty();
        }
    }

    protected abstract T fromJsonNode(JsonNode json);
}
