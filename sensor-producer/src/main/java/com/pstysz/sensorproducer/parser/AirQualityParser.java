package com.pstysz.sensorproducer.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pstysz.airquality.model.AirQualityMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AirQualityParser implements JsonToRecordParser<AirQualityMeasurement> {

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

            AirQualityMeasurement measurement = AirQualityMeasurement.newBuilder()
                    .setSensorId(result.path("id").asText())
                    .setName(result.path("parameter").path("name").asText())
                    .setUnits(result.path("parameter").path("units").asText())
                    .setDisplayName(result.path("parameter").path("displayName").asText())
                    .setValue(result.path("latest").path("value").asDouble())
                    .setDatetimeLast(result.path("datetimeLast").path("utc").asText())
                    .setFetchDateTime(OffsetDateTime.now().toString())
                    .build();

            return Optional.of(measurement);

        } catch (Exception e) {
            log.error("Error while parsing OpenAQ response", e);
            return Optional.empty();
        }
    }
}
