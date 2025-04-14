package com.pstysz.streamprocessor.topology;

import com.pstysz.airquality.model.SensorMeasurement;
import com.pstysz.airquality.model.SensorToStation;
import com.pstysz.airquality.model.MeasuringStation;
import com.pstysz.airquality.model.SensorWithStationData;
import com.pstysz.streamprocessor.domain.AvroTopic;
import com.pstysz.streamprocessor.domain.StreamType;
import com.pstysz.streamprocessor.domain.TopicsRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StationMeasurementTopology {

    private final TopicsRegistry topicsRegistry;
    private final KStream<String, SensorMeasurement> measurements;
    private final GlobalKTable<String, SensorToStation> sensorToStation;
    private final GlobalKTable<String, MeasuringStation> stations;

    public void build() {
        AvroTopic<String, SensorWithStationData> outputTopic = topicsRegistry.get(StreamType.SENSOR_WITH_STATION_DATA);

        // First join: measurement -> sensorId + stationId
        KStream<String, EnrichedMeasurement> enriched = measurements
                .join(sensorToStation,
                        (sensorId, measurement) -> sensorId,
                        (measurement, mapping) -> {
                            if (mapping == null) return null;
                            return new EnrichedMeasurement(measurement, mapping.getStationId());
                        })
                .filter((key, enrichedMeasurement) -> enrichedMeasurement != null);

        // Second join: using stationId to get full MeasuringStation
        KStream<String, SensorWithStationData> stationMeasurements = enriched
                .join(stations,
                        (sensorId, enrichedMeasurement) -> enrichedMeasurement.stationId,
                        (enrichedMeasurement, station) -> toStationMeasurement(enrichedMeasurement.measurement, station))
                .filter((key, result) -> result != null)
                .peek((key, value) -> log.info("Produced SensorWithStationData: {}", value));

        // Write to output
        stationMeasurements.to(outputTopic.getName(),
                Produced.with(outputTopic.getKeySerde(), outputTopic.getValueSerde()));
    }

    // Inner helper class to temporarily store enriched measurement
    private record EnrichedMeasurement(SensorMeasurement measurement, String stationId) {
    }

    private SensorWithStationData toStationMeasurement(SensorMeasurement m, MeasuringStation s) {
        if (s == null) return null;

        return SensorWithStationData.newBuilder()
                .setStationId(s.getStationId())
                .setStationName(s.getName())
                .setLocality(s.getLocality())
                .setCountryId(s.getCountryId())
                .setSensorId(m.getSensorId())
                .setSensorName(m.getName())
                .setUnits(m.getUnits())
                .setSensorDisplayName(m.getDisplayName())
                .setValue(m.getValue())
                .setDatetimeLast(m.getDatetimeLast())
                .setFetchDateTime(m.getFetchDateTime())
                .build();
    }
}
