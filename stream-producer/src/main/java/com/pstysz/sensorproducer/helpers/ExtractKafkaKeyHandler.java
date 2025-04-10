package com.pstysz.sensorproducer.helpers;

@FunctionalInterface
public interface ExtractKafkaKeyHandler<T> {
    String extractKey(T record);
}
