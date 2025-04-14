package com.pstysz.sensorproducer.service;

import com.pstysz.airquality.model.SensorMeasurement;
import com.pstysz.sensorproducer.config.KafkaTopicConfig;
import com.pstysz.sensorproducer.config.OpenAqApiConfig;
import com.pstysz.sensorproducer.db.DbMock;
import com.pstysz.sensorproducer.parser.SensorDataParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SensorDataProducer extends AbstractKafkaProducer<SensorMeasurement> {

    public SensorDataProducer(
            SensorDataParser parser,
            RestTemplate restTemplate,
            KafkaTemplate<String, SpecificRecord> kafkaTemplate,
            OpenAqApiConfig config,
            DbMock db,
            KafkaTopicConfig topicConfig
    ) {
        super(parser, restTemplate, kafkaTemplate, config,
                db::getSubscribedSensorsIds,
                config::sensorUrl,
                SensorMeasurement::getSensorId,
                topicConfig.getSensorTopic());
    }

    @Scheduled(fixedDelayString = "${openaq.sensor-data-fetch-interval-ms}")
    public void run() {
        fetchAndSend();
    }
}
