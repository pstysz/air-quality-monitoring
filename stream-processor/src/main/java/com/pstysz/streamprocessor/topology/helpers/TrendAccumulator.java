package com.pstysz.streamprocessor.topology.helpers;

import com.pstysz.airquality.model.MeasurementTrend;
import com.pstysz.airquality.model.SensorMeasurement;
import com.pstysz.airquality.model.TrendType;

import java.time.Instant;

public class TrendAccumulator {
    private double previousSum = 0;
    private long previousCount = 0;

    private double currentSum = 0;
    private long currentCount = 0;

    public TrendAccumulator update(SensorMeasurement measurement) {
        previousSum = currentSum;
        previousCount = currentCount;

        currentSum = measurement.getValue();
        currentCount = 1;

        return this;
    }

    public MeasurementTrend toAvro(String sensorId, long windowStart, long windowEnd) {
        double prevAvg = previousCount == 0 ? 0 : previousSum / previousCount;
        double currAvg = currentCount == 0 ? 0 : currentSum / currentCount;

        TrendType trend;
        if (currAvg > prevAvg) trend = TrendType.UP;
        else if (currAvg < prevAvg) trend = TrendType.DOWN;
        else trend = TrendType.STABLE;

        return MeasurementTrend.newBuilder()
                .setSensorId(sensorId)
                .setWindowStart(Instant.ofEpochMilli(windowStart).toString())
                .setWindowEnd(Instant.ofEpochMilli(windowEnd).toString())
                .setTrend(trend)
                .setPreviousAvg(prevAvg)
                .setCurrentAvg(currAvg)
                .build();
    }
}
