package com.pstysz.streamprocessor.topology.helpers;

import com.pstysz.streamprocessor.domain.StreamType;

import java.time.Duration;

public final class AggregationConst {
    public static String granularity(StreamType streamType) {
        return switch (streamType) {
            case AGG_MEASUREMENT_5M -> "5m";
            case AGG_MEASUREMENT_1H -> "1h";
            default -> throw new IllegalStateException("Unexpected value: " + streamType);
        };
    }

    public static Duration windowSize(StreamType streamType) {
        return switch (streamType) {
            case AGG_MEASUREMENT_5M -> Duration.ofMinutes(5);
            case AGG_MEASUREMENT_1H -> Duration.ofHours(1);
            default -> throw new IllegalStateException("Unexpected value: " + streamType);
        };
    }
}
