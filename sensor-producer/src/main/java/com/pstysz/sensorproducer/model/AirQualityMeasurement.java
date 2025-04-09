package com.pstysz.sensorproducer.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AirQualityMeasurement implements DataRecord {
    private String sensorId;
    private String name;
    private String units;
    private String displayName;
    private double value;
    private String datetimeLast;
    private String fetchDateTime;

    @Override
    public String kafkaKey() {
        return sensorId;
    }
}
