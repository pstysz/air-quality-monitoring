package com.pstysz.sensorproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StreamProducerApp {
    public static void main(String[] args) {
        SpringApplication.run(StreamProducerApp.class, args);
    }
}
