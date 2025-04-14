//package com.pstysz.streamprocessor.topology;
//
//import com.pstysz.airquality.model.MeasuringStation;
//import com.pstysz.airquality.model.SensorMeasurement;
//import com.pstysz.airquality.model.SensorToStation;
//import com.pstysz.airquality.model.SensorWithStationData;
//import com.pstysz.streamprocessor.domain.AvroTopic;
//import com.pstysz.streamprocessor.domain.StreamType;
//import com.pstysz.streamprocessor.domain.TopicsRegistry;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.StoreQueryParameters;
//import org.apache.kafka.streams.kstream.KStream;
//import org.apache.kafka.streams.kstream.KTable;
//import org.apache.kafka.streams.state.QueryableStoreTypes;
//import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class SensorWithStationDataTopology {
//
//    private final TopicsRegistry topicsRegistry;
//    private final ApplicationContext applicationContext;
//    private final KStream<String, SensorMeasurement> sensorMeasurements;
//    private final KTable<String, SensorToStation> sensorToStation;
//
//    @PostConstruct
//    public void build() {
//        KafkaStreams kafkaStreams = applicationContext.getBean(KafkaStreams.class);
//        AvroTopic<String, SensorWithStationData> outputTopic = topicsRegistry.get(StreamType.SENSOR_WITH_STATION_DATA);
//
//        KStream<String, TemporaryMeasurements> enriched = sensorMeasurements
//                .join(sensorToStation,
//                        (sensorData, sensorToStationMapping) -> {
//                            if (sensorToStationMapping == null) return null;
//                            return new TemporaryMeasurements(sensorData, sensorToStationMapping.getStationId());
//                        })
//                .filter((key, enrichedMeasurement) -> enrichedMeasurement != null);
//
//        AvroTopic<String, MeasuringStation> stationTopic = topicsRegistry.get(StreamType.STATION);
//        ReadOnlyKeyValueStore<String, MeasuringStation> measuringStationStore = kafkaStreams.store(
//                StoreQueryParameters.fromNameAndType(
//                        stationTopic.storeName(),
//                        QueryableStoreTypes.keyValueStore()
//                )
//        );
//
//        KStream<String, SensorWithStationData> measurementsWithStationData = enriched
//                .mapValues(tm -> {
//                    MeasuringStation station = measuringStationStore.get(tm.stationId);
//                    if (station == null) return null;
//                    return toStationMeasurement(tm.measurement, station);
//                })
//                .filter((key, result) -> result != null)
//                .peek((key, value) -> log.info("Produced SensorWithStationData: {}", value));
//
//
//        measurementsWithStationData.to(outputTopic.getName(), outputTopic.produced());
//    }
//
//    private SensorWithStationData toStationMeasurement(SensorMeasurement m, MeasuringStation s) {
//        if (s == null) return null;
//
//        return SensorWithStationData.newBuilder()
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
//
//    private record TemporaryMeasurements(SensorMeasurement measurement, String stationId) {
//    }
//}
