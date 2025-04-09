package com.pstysz.sensorproducer.parser;

import java.util.Optional;

@FunctionalInterface
public interface JsonToMeasurementParser<T> {
    Optional<T> parse(String json);
}
