package com.pstysz.sensorproducer.parser;

import java.util.Optional;

@FunctionalInterface
public interface JsonToRecordParser<T> {
    Optional<T> parse(String json);
}
