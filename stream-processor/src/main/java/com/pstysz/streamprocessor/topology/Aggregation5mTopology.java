package com.pstysz.streamprocessor.topology;

import com.pstysz.airquality.model.SensorMeasurement;
import com.pstysz.streamprocessor.domain.StreamType;
import com.pstysz.streamprocessor.domain.TopicsRegistry;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Component;

@Component
public class Aggregation5mTopology extends AggregationTopology {

    public Aggregation5mTopology(KStream<String, SensorMeasurement> inputStream, TopicsRegistry topicsRegistry) {
        super(inputStream, topicsRegistry.get(StreamType.AGG_MEASUREMENT_5M), StreamType.AGG_MEASUREMENT_5M);
    }
}
