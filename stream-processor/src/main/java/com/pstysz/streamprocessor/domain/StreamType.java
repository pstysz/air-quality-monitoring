package com.pstysz.streamprocessor.domain;

import com.pstysz.airquality.model.AirQualityMeasurement;
import com.pstysz.airquality.model.MeasurementToStation;
import com.pstysz.airquality.model.MeasuringStation;
import com.pstysz.airquality.model.StationMeasurement;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecord;

public enum StreamType {
    MEASUREMENT(String.class, AirQualityMeasurement.class, false),
    STATION(String.class, MeasuringStation.class, false),
    STATION_MEASUREMENT(String.class, StationMeasurement.class, true),
    MEASUREMENT_TO_STATION(String.class, MeasurementToStation.class, true);

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

