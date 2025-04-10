package com.pstysz.sensorproducer.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pstysz.airquality.model.MeasuringStation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class MeasuringStationDataParser extends JsonToRecordParser<MeasuringStation> {

    public MeasuringStationDataParser(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public MeasuringStation fromJsonNode(JsonNode json) {
        return MeasuringStation.newBuilder()
                .setStationId(json.path("id").asText())
                .setName(json.path("name").asText())
                .setLocality(json.path("locality").asText())
                .setCountryId(json.path("country").path("id").asText())
                .setSensorIds(StreamSupport.stream(json.path("sensors").spliterator(), false)
                        .map(node -> node.path("id").asText())
                        .toList())
                .setDatetimeLast(json.path("datetimeLast").path("utc").asText())
                .setFetchDateTime(OffsetDateTime.now().toString())
                .build();
    }
}
