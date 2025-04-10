package com.pstysz.sensorproducer.service;

import com.pstysz.airquality.model.AirQualityMeasurement;
import com.pstysz.sensorproducer.config.OpenAqApiConfig;
import com.pstysz.sensorproducer.db.DbMock;
import com.pstysz.sensorproducer.parser.AirQualityDataParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SensorDataProducer extends AbstractKafkaProducer<AirQualityMeasurement> {

    public SensorDataProducer(
            AirQualityDataParser parser,
            RestTemplate restTemplate,
            KafkaTemplate<String, SpecificRecord> kafkaTemplate,
            OpenAqApiConfig config,
            DbMock db,
            @Value("${custom.kafka.measurements-topic}") String topic
    ) {
        super(parser, restTemplate, kafkaTemplate, config,
                db::getSubscribedSensorsIds,
                config::sensorUrl,
                AirQualityMeasurement::getSensorId,
                topic);
    }

    @Scheduled(fixedDelayString = "${openaq.sensor-data-fetch-interval-ms}")
    public void run() {
        fetchAndSend();
    }
}
