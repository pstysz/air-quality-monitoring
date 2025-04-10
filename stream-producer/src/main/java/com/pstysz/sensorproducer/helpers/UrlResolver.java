package com.pstysz.sensorproducer.helpers;

@FunctionalInterface
public interface UrlResolver {
    String resolve(String id);
}
