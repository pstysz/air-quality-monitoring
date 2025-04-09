package com.pstysz.sensorproducer.service;

import com.pstysz.sensorproducer.config.OpenAqApiConfig;
import com.pstysz.sensorproducer.db.DbMock;
import com.pstysz.sensorproducer.model.AirQualityMeasurement;
import com.pstysz.sensorproducer.parser.JsonToMeasurementParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SensorDataProducer extends AbstractKafkaProducer<AirQualityMeasurement> {

    private final DbMock db;

    public SensorDataProducer(
            JsonToMeasurementParser<AirQualityMeasurement> parser,
            RestTemplate restTemplate,
            KafkaTemplate<String, AirQualityMeasurement> kafkaTemplate,
            OpenAqApiConfig config,
            DbMock db
    ) {
        super(parser, restTemplate, kafkaTemplate, config);
        this.db = db;
    }

    @Override
    @Scheduled(fixedDelayString = "${openaq.interval-ms}")
    public void fetchAndSend() {
        db.getSubscribedSensorIds().stream()
                .map(config::sensorUrl)
                .forEach(super::process);
    }
}
