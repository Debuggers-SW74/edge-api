package com.edgeapi.service.fastporteiot.domain.model.entities;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import java.util.ArrayList;
import java.util.List;

public class RealTimeSensorData {
    private final List<SensorReadingWithTripId> readingsWithTripId = new ArrayList<>();

    public void addReading(Integer tripId, SensorReading reading, String timestamp) {
        readingsWithTripId.add(new SensorReadingWithTripId(tripId, reading, timestamp));
    }

    public List<SensorReadingWithTripId> getReadingsWithTripId() {
        return new ArrayList<>(readingsWithTripId);
    }

    public void clear() {
        readingsWithTripId.clear();
    }
}
