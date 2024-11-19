package com.edgeapi.service.fastporteiot.domain.model.entities;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SensorReadingWithTripId(
        Integer tripId,
        SensorReading reading,
        String timestamp
) {
    public SensorReadingWithTripId {
        if (tripId < 0) {
            throw new IllegalArgumentException("tripId cannot be negative");
        }

        if (timestamp == null || timestamp.isBlank()) {
            throw new IllegalArgumentException("timestamp cannot be null or empty");
        }
    }

    @JsonProperty("temperatureValue")
    public float getTemperature() {
        return reading.temperature();
    }

    @JsonProperty("humidityValue")
    public float getHumidity() {
        return reading.humidity();
    }

    @JsonProperty("pressureValue")
    public float getPressure() {
        return reading.pressure();
    }

    @JsonProperty("gasValue")
    public float getGas() {
        return reading.gas();
    }
}
