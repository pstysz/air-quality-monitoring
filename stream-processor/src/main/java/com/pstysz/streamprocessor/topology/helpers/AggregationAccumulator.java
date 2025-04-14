package com.pstysz.streamprocessor.topology.helpers;

import com.pstysz.airquality.model.AggregatedMeasurement;
import com.pstysz.airquality.model.SensorMeasurement;

import java.time.Instant;

public class AggregationAccumulator {
    private double sum = 0;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private long count = 0;

    public AggregationAccumulator add(SensorMeasurement m) {
        double value = m.getValue();
        sum += value;
        min = Math.min(min, value);
        max = Math.max(max, value);
        count++;
        return this;
    }

    public AggregatedMeasurement toAvro(String sensorId, long windowStart, long windowEnd, String granularity) {
        return AggregatedMeasurement.newBuilder()
                .setSensorId(sensorId)
                .setWindowStart(Instant.ofEpochMilli(windowStart).toString())
                .setWindowEnd(Instant.ofEpochMilli(windowEnd).toString())
                .setAvgValue(sum / count)
                .setMinValue(min)
                .setMaxValue(max)
                .setCount(count)
                .setGranularity(granularity)
                .build();
    }
}

