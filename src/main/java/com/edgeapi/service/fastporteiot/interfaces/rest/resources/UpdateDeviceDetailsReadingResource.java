package com.edgeapi.service.fastporteiot.interfaces.rest.resources;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;

public record UpdateDeviceDetailsReadingResource(
        float temperature,
        float humidity,
        float pressure
) {
    public SensorReading toSensorReading() {
        return new SensorReading(temperature, humidity, pressure);
    }
}
