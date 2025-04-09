package com.pstysz.sensorproducer.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pstysz.sensorproducer.model.AirQualityMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AirQualityParser implements JsonToMeasurementParser<AirQualityMeasurement> {

    private final ObjectMapper objectMapper;

    @Override
    public Optional<AirQualityMeasurement> parse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode results = root.path("results");

            if (!results.isArray() || results.isEmpty()) {
                log.warn("No results found in OpenAQ response");
                return Optional.empty();
            }

            JsonNode result = results.get(0);

            AirQualityMeasurement measurement = AirQualityMeasurement.builder()
                    .sensorId(result.path("id").asText())
                    .name(result.path("parameter").path("name").asText())
                    .units(result.path("parameter").path("units").asText())
                    .displayName(result.path("parameter").path("displayName").asText())
                    .value(result.path("latest").path("value").asDouble())
                    .datetimeLast(result.path("datetimeLast").path("utc").asText())
                    .fetchDateTime(OffsetDateTime.now().toString())
                    .build();

            return Optional.of(measurement);

        } catch (Exception e) {
            log.error("Error while parsing OpenAQ response", e);
            return Optional.empty();
        }
    }
}
