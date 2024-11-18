package com.edgeapi.service.fastporteiot.domain.model.entities;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class RealTimeSensorData {
    @Embedded
    private final List<SensorReading> readings = new ArrayList<>();

    public void addReading(SensorReading reading) {
        readings.add(reading);
    }

    public List<SensorReading> getReadings() {
        return new ArrayList<>(readings);
    }

    public void clear() {
        readings.clear();
    }
}
