package com.edgeapi.service.fastporteiot.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record SensorReading(float temperature, float humidity, float pressure) {
    public float getValueForType(String sensorType) {
        return switch (sensorType.toUpperCase()) {
            case "SENSOR_TEMPERATURE" -> temperature;
            case "SENSOR_HUMIDITY" -> humidity;
            case "SENSOR_PRESSURE" -> pressure;
            default -> throw new IllegalArgumentException("Unknown sensor type: " + sensorType);
        };
    }

    public SensorReading {
        if (temperature < 0 || humidity < 0 || pressure < 0) {
            throw new IllegalArgumentException("Sensor readings cannot be negative");
        }
    }
}