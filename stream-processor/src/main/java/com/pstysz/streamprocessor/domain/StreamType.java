package com.pstysz.streamprocessor.domain;

import com.pstysz.airquality.model.*;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecord;

public enum StreamType {
    MEASUREMENT(String.class, SensorMeasurement.class, false),
    STATION(String.class, MeasuringStation.class, false),
    SENSOR_WITH_STATION_DATA(String.class, SensorWithStationData.class, true),
    SENSOR_TO_STATION(String.class, SensorToStation.class, true),
    AGG_MEASUREMENT_5M(String.class, AggregatedMeasurement.class, true),
    AGG_MEASUREMENT_1H(String.class, AggregatedMeasurement.class, true),
    MEASUREMENT_TREND(String.class, MeasurementTrend.class, true);

    private final Class<?> keyClass;
    private final Class<? extends SpecificRecord> valueClass;
    @Getter
    private final boolean isOutput;

    <K, V extends SpecificRecord> StreamType(Class<K> keyClass, Class<V> valueClass, boolean isOutput) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.isOutput = isOutput;
    }

    @SuppressWarnings("unchecked")
    public <K> Class<K> getKeyClass() {
        return (Class<K>) keyClass;
    }

    @SuppressWarnings("unchecked")
    public <V extends SpecificRecord> Class<V> getValueClass() {
        return (Class<V>) valueClass;
    }
}

