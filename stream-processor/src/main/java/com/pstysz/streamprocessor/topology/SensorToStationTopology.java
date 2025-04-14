package com.pstysz.streamprocessor.topology;

import com.pstysz.airquality.model.SensorToStation;
import com.pstysz.airquality.model.MeasuringStation;
import com.pstysz.streamprocessor.domain.AvroTopic;
import com.pstysz.streamprocessor.domain.StreamType;
import com.pstysz.streamprocessor.domain.TopicsRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorToStationTopology {

    private final TopicsRegistry topicsRegistry;
    private final KTable<String, MeasuringStation> measuringStationTable;

    @PostConstruct
    public void build() {
        AvroTopic<String, SensorToStation> sensorToStationTopic = topicsRegistry.get(StreamType.SENSOR_TO_STATION);

        measuringStationTable
                .toStream()
                .flatMap(this::explodeSensorIds)
                .peek((key, value) -> log.info("Mapped: sensorId={} => stationId={}", key, value.getStationId()))
                .groupByKey()
                .reduce((oldValue, newValue) -> newValue, sensorToStationTopic.materialized());
    }

    private Iterable<KeyValue<String, SensorToStation>> explodeSensorIds(String stationId, MeasuringStation station) {
        if (station == null || station.getSensorIds() == null) {
            return List.of();
        }

        return station.getSensorIds().stream()
                .map(sensorId -> new KeyValue<>(
                        sensorId,
                        SensorToStation.newBuilder()
                                .setSensorId(sensorId)
                                .setStationId(stationId)
                                .build()))
                .toList();
    }
}
