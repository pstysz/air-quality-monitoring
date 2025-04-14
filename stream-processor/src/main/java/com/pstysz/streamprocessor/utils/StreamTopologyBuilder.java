package com.pstysz.streamprocessor.utils;


import com.pstysz.airquality.model.SensorMeasurement;
import com.pstysz.airquality.model.SensorToStation;
import com.pstysz.airquality.model.MeasuringStation;
import com.pstysz.streamprocessor.domain.AvroTopic;
import com.pstysz.streamprocessor.domain.StreamType;
import com.pstysz.streamprocessor.domain.TopicsRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StreamTopologyBuilder {

    private final StreamsBuilder builder = new StreamsBuilder();

    public Topology build() {
        return builder.build();
    }

    @Bean // sensorId -> sensor info + measurement values
    public KStream<String, SensorMeasurement> airQualityMeasurementsStream(TopicsRegistry topicsRegistry) {
        AvroTopic<String, SensorMeasurement> topic = topicsRegistry.get(StreamType.MEASUREMENT);
        return builder.stream(topic.getName(), topic.consumed());
    }

    @Bean // stationId -> station info
    public KTable<String, MeasuringStation> measuringStationTable(TopicsRegistry topicsRegistry) {
        AvroTopic<String, MeasuringStation> topic = topicsRegistry.get(StreamType.STATION);
        return builder.table(topic.getName(), topic.consumed(), topic.materialized());
    }

    @Bean // stationId -> station info
    public GlobalKTable<String, MeasuringStation> measuringStationGlobalTable(TopicsRegistry topicsRegistry) {
        AvroTopic<String, MeasuringStation> topic = topicsRegistry.get(StreamType.STATION);
        return builder.globalTable(topic.getName(), topic.consumed(), topic.materialized());
    }

    @Bean // GlobalKTable: sensorId -> stationId
    public GlobalKTable<String, SensorToStation> sensorToStationGlobalTable(TopicsRegistry topicsRegistry) {
        AvroTopic<String, SensorToStation> topic = topicsRegistry.get(StreamType.SENSOR_TO_STATION);
        return builder.globalTable(topic.getName(), topic.consumed(), topic.materialized());
    }

}
