//package com.pstysz.streamprocessor.topology;
//
//import com.pstysz.airquality.model.AirQualityMeasurement;
//import com.pstysz.airquality.model.MeasurementToStation;
//import com.pstysz.airquality.model.MeasuringStation;
//import com.pstysz.airquality.model.StationMeasurement;
//import com.pstysz.streamprocessor.domain.AvroTopic;
//import com.pstysz.streamprocessor.domain.StreamType;
//import com.pstysz.streamprocessor.domain.TopicsRegistry;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.kstream.*;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class StationMeasurementTopology{
//
//    private final TopicsRegistry topicsRegistry;
//
//
//    public void build(StreamsBuilder builder) {
//        // Topics
//        AvroTopic<String, AirQualityMeasurement> measurementTopic = topicsRegistry.get(StreamType.MEASUREMENT);
//        AvroTopic<String, MeasurementToStation> mappingTopic = topicsRegistry.get(StreamType.MEASUREMENT_TO_STATION);
//        AvroTopic<String, MeasuringStation> stationTopic = topicsRegistry.get(StreamType.STATION);
//        AvroTopic<String, StationMeasurement> outputTopic = topicsRegistry.get(StreamType.STATION_MEASUREMENT);
//
//        // Source stream
//        KStream<String, AirQualityMeasurement> measurements = builder
//                .stream(measurementTopic.getName(), measurementTopic.consumed())
//                .peek((key, value) -> log.info("Incoming measurement: key={}, value={}", key, value));
//
//        // GlobalKTable: sensorId -> stationId
//        GlobalKTable<String, MeasurementToStation> sensorToStation =
//                builder.globalTable(mappingTopic.getName(), mappingTopic.consumed());
//
//        // GlobalKTable: stationId -> MeasuringStation
//        GlobalKTable<String, MeasuringStation> stations =
//                builder.globalTable(stationTopic.getName(), stationTopic.consumed());
//
//        // First join: measurement -> measurement + stationId
//        KStream<String, EnrichedMeasurement> enriched = measurements
//                .join(sensorToStation,
//                        (sensorId, measurement) -> sensorId,
//                        (measurement, mapping) -> {
//                            if (mapping == null) return null;
//                            return new EnrichedMeasurement(measurement, mapping.getStationId());
//                        })
//                .filter((key, enrichedMeasurement) -> enrichedMeasurement != null);
//
//        // Second join: using stationId to get full MeasuringStation
//        KStream<String, StationMeasurement> stationMeasurements = enriched
//                .join(stations,
//                        (sensorId, enrichedMeasurement) -> enrichedMeasurement.stationId,
//                        (enrichedMeasurement, station) -> toStationMeasurement(enrichedMeasurement.measurement, station))
//                .filter((key, result) -> result != null)
//                .peek((key, value) -> log.info("Produced StationMeasurement: {}", value));
//
//        // Write to output
//        stationMeasurements.to(outputTopic.getName(),
//                Produced.with(outputTopic.getKeySerde(), outputTopic.getValueSerde()));
//    }
//
//    // Inner helper class to temporarily store enriched measurement
//    private record EnrichedMeasurement(AirQualityMeasurement measurement, String stationId) {}
//
//    private StationMeasurement toStationMeasurement(AirQualityMeasurement m, MeasuringStation s) {
//        if (s == null) return null;
//
//        return StationMeasurement.newBuilder()
//                .setStationId(s.getStationId())
//                .setStationName(s.getName())
//                .setLocality(s.getLocality())
//                .setCountryId(s.getCountryId())
//                .setSensorId(m.getSensorId())
//                .setSensorName(m.getName())
//                .setUnits(m.getUnits())
//                .setSensorDisplayName(m.getDisplayName())
//                .setValue(m.getValue())
//                .setDatetimeLast(m.getDatetimeLast())
//                .setFetchDateTime(m.getFetchDateTime())
//                .build();
//    }
//}
