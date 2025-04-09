package com.pstysz.sensorproducer.utils;

import java.util.function.Supplier;

// In 2025 Java still doesn't have a clean and pretty `lazy val` construct, WTF
public class Lazy<T> {
    private Supplier<T> supplier;
    private T value;
    private boolean initialized = false;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (!initialized) {
            value = supplier.get();
            initialized = true;
            supplier = null;
        }
        return value;
    }
}