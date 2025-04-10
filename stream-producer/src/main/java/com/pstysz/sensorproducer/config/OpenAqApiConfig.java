package com.pstysz.sensorproducer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OpenAqApiConfig {
    @Value("${openaq.base-url}")
    private String baseUrl;

    @Value("${openaq.api-key}")
    private String apiKey;

    @Value("${custom.kafka.measurements-topic}")
    private String measurementsTopic;

    @Value("${custom.kafka.stations-topic}")
    private String stationsTopic;

    public String sensorUrl(String sensorId) {
        return baseUrl + "/sensors/" + sensorId;
    }

    public String stationUrl(String locationId) {
        return baseUrl + "/locations/" + locationId;
    }
}
