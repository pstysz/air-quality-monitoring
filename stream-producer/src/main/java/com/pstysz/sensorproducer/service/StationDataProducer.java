package com.pstysz.sensorproducer.service;

import com.pstysz.airquality.model.MeasuringStation;
import com.pstysz.sensorproducer.config.OpenAqApiConfig;
import com.pstysz.sensorproducer.db.DbMock;
import com.pstysz.sensorproducer.parser.MeasuringStationDataParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class StationDataProducer extends AbstractKafkaProducer<MeasuringStation> {

    public StationDataProducer(
            MeasuringStationDataParser parser,
            RestTemplate restTemplate,
            KafkaTemplate<String, SpecificRecord> kafkaTemplate,
            OpenAqApiConfig config,
            DbMock db,
            @Value("${custom.kafka.stations-topic}") String topic

    ) {
        super(parser, restTemplate, kafkaTemplate, config,
                db::getStationsIds,
                config::stationUrl,
                MeasuringStation::getStationId,
                topic);
    }

    @Scheduled(fixedDelayString = "${openaq.station-data-fetch-interval-ms}")
    public void run() {
        fetchAndSend();
    }
}
