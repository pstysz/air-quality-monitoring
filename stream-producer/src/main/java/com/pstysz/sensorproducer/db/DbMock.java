package com.pstysz.sensorproducer.db;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbMock {
    public List<String> getSubscribedSensorsIds() {
        //  co-sensor-id: 35995
        //  no2-sensor-id: 35967
        //  pm25-sensor-id: 28502
        return List.of("35995", "35967", "28502");
    }

    public List<String> getStationsIds() {
        return List.of("9342"); // Wroclaw, al. Wisniowa
    }
}
