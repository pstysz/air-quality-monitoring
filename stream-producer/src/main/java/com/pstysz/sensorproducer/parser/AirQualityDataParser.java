package com.pstysz.sensorproducer.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pstysz.airquality.model.AirQualityMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
public class AirQualityDataParser extends JsonToRecordParser<AirQualityMeasurement> {

    public AirQualityDataParser(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected AirQualityMeasurement fromJsonNode(JsonNode json) {
        return AirQualityMeasurement.newBuilder()
                .setSensorId(json.path("id").asText())
                .setName(json.path("parameter").path("name").asText())
                .setUnits(json.path("parameter").path("units").asText())
                .setDisplayName(json.path("parameter").path("displayName").asText())
                .setValue(json.path("latest").path("value").asDouble())
                .setDatetimeLast(json.path("datetimeLast").path("utc").asText())
                .setFetchDateTime(OffsetDateTime.now().toString())
                .build();
    }
}
